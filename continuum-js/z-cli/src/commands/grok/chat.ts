// src/commands/grok/chat.ts
import {input} from '@inquirer/prompts'
import {Flags} from '@oclif/core'
import chalk from 'chalk'
import {glob} from 'glob'
import ora from 'ora'
import {HTTPResponse, Page} from 'puppeteer'
import {loadGrokConfig, saveGrokConfig} from '../../internal/state/ConfigGrok.js'
import {BaseGrokCommand} from './BaseGrokCommand.js'

export default class Chat extends BaseGrokCommand {
    static description = 'Start an interactive chat session with Grok 3'

    static flags = {
        conversation: Flags.string({
                                       char: 'c',
                                       description: 'Use a specific conversation ID (overrides active ID)',
                                       name: 'conversation-id'
                                   }),
        files: Flags.string({
                                char: 'f',
                                description: 'Glob pattern of files to upload (e.g., "./docs/*.txt")',
                                name: 'files'
                            }),
        visible: Flags.boolean({
                                   char: 'v',
                                   description: 'Run with visible browser (non-headless mØde)',
                                   name: 'visible',
                                   default: false // Visible by default (headless: false)
                               })
    }

    async run(): Promise<void> {
        const { flags } = await this.parse(Chat)
        const config = await loadGrokConfig(this.config.configDir)

        let conversationId = flags.conversation || config.activeConversationId || crypto.randomUUID()
        if (conversationId !== config.activeConversationId) {
            config.activeConversationId = conversationId
            await saveGrokConfig(this.config.configDir, config)
            this.log(chalk.yellow(`Active conversation set to: ${conversationId}`))
        }

        let page: Page
        try {
            // Invert visible flag: --visible=true means headless=false
            page = await this.setupBrowser(`https://grok.com/chat/${conversationId}`, undefined, !flags.visible)

            try {
                await page.waitForSelector('textarea[aria-label="Ask Grok anything"]', { timeout: 30000 })
            } catch (error) {
                this.log(chalk.red('Timed out waiting for chat UI.'))
                const content = await page.content()
                this.log(chalk.yellow('Page content after timeout:'), content)
                throw new Error('Failed to load chat UI')
            }

            if (flags.files) {
                const fileInputSelector = 'input[type="file"]' // Adjust based on Grok's UI
                const filePaths = await glob(flags.files)
                if (filePaths.length > 0) {
                    const fileInput = await page.$(fileInputSelector)
                    if (fileInput) {
                        await fileInput.uploadFile(...filePaths)
                        this.log(chalk.green(`Uploaded files: ${filePaths.join(', ')}`))
                        await page.waitForSelector('button[type="submit"][aria-label="Submit"]', { timeout: 5000 })
                    } else {
                        this.log(chalk.red('File input not found. Skipping upload.'))
                    }
                } else {
                    this.log(chalk.yellow(`No files matched glob pattern: ${flags.files}`))
                }
            }

            this.log(chalk.blue('Interactive chat started. Type ') + chalk.yellow('\\q') + chalk.blue(' to exit.'))

            let chatActive = true

            while (chatActive) {
                const userInput = await input({ message: chalk.green('You:') })

                if (userInput.trim() === '\\q') {
                    chatActive = false
                } else {
                    const spinner = ora('Grok is typing...').start()

                    try {
                        const inputSelector = 'textarea[aria-label="Ask Grok anything"]'
                        const submitSelector = 'button[type="submit"][aria-label="Submit"]'
                        await page.waitForSelector(inputSelector, { timeout: 5000 })
                        await page.waitForSelector(submitSelector, { timeout: 5000 })

                        await page.evaluate((selector: string) => {
                            const input = document.querySelector(selector) as HTMLTextAreaElement
                            input.value = ''
                        }, inputSelector)
                        await page.type(inputSelector, userInput)

                        const responsePromise = new Promise<void>((resolve, reject) => {
                            const timeout = setTimeout(() => reject(new Error('Response timeout after 5 minutes')), 300000)

                            page.once('response', async (response: HTTPResponse) => {
                                if (
                                    response.url() ===
                                    `https://grok.com/rest/app-chat/conversations/${conversationId}/responses`
                                ) {
                                    const buffer = await response.buffer()
                                    const text = buffer.toString('utf-8')
                                    const lines = text.split('\n').filter(Boolean)

                                    spinner.stop()
                                    process.stdout.write(chalk.cyan('Grok: \n'))

                                    for (const line of lines) {
                                        try {
                                            const data: { result?: { token?: string, modelResponse?: { message: string, responseId: string } } } = JSON.parse(line)
                                            if (data.result?.token) {
                                                process.stdout.write(data.result.token)
                                            } else if (data.result?.modelResponse?.message) {
                                                process.stdout.write(data.result.modelResponse.message)
                                                clearTimeout(timeout)
                                                resolve()
                                            }
                                        } catch (e) {
                                            this.log(chalk.yellow(`Parse error in line: ${line}, Error: ${(e as Error).message}`))
                                        }
                                    }
                                    clearTimeout(timeout)
                                    resolve()
                                }
                            })
                        })

                        await page.click(submitSelector)

                        await responsePromise

                        process.stdout.write('\n')
                    } catch (error) {
                        spinner.fail(chalk.red(`Chat error: ${(error as Error).message}`))
                    }
                }
            }

            this.log(chalk.blue('Chat session ended.'))
        } catch (error) {
            this.log(chalk.red(`Setup error: ${(error as Error).message}`))
        } finally {
            await this.cleanup()
        }
    }
}