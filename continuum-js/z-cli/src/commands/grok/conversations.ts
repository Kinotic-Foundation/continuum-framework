// src/commands/grok/conversations.ts
import { select } from '@inquirer/prompts'
import chalk from 'chalk'
import { BaseGrokCommand } from './BaseGrokCommand.js'
import { loadGrokConfig, saveGrokConfig } from '../../internal/state/ConfigGrok.js'
import { HTTPResponse, Page } from 'puppeteer'

interface Conversation {
    conversationId: string
    title: string
    createTime: string
    modifyTime: string
}

export default class Conversations extends BaseGrokCommand {
    static description = 'List and select Grok conversations'

    static examples = ['z grok:conversations']

    async run(): Promise<void> {
        let page: Page
        try {
            let responseText = ''
            page = await this.setupBrowser(
                'https://grok.com/rest/app-chat/conversations?pageSize=60&useNewImplementation=true',
                (p: Page) => {
                    p.once('response', async (response: HTTPResponse) => {
                        if (response.url() === 'https://grok.com/rest/app-chat/conversations?pageSize=60&useNewImplementation=true') {
                            responseText = await response.text()
                        }
                    })
                },
                true // Headless
            )

            await page.waitForFunction(
                () => document.readyState === 'complete',
                { timeout: 300000 }
            )

            if (!responseText) {
                this.log(chalk.red('No response data captured.'))
                return
            }

            const json: { conversations: Conversation[] } = JSON.parse(responseText)
            const conversations = json.conversations || []

            if (!conversations.length) {
                this.log(chalk.yellow('No conversations found.'))
                return
            }

            const choices = conversations.map((conv) => ({
                name: `${conv.title} (${conv.conversationId}) - Last modified: ${new Date(conv.modifyTime).toLocaleString()}`,
                value: conv.conversationId
            }))

            const selectedId = await select({
                                                message: chalk.blue('Select a conversation to set as active:'),
                                                choices
                                            })

            const config = await loadGrokConfig(this.config.configDir)
            config.activeConversationId = selectedId
            await saveGrokConfig(this.config.configDir, config)
            this.log(chalk.green(`Active conversation set to: ${selectedId}`))
        } catch (error) {
            this.log(chalk.red(`Error fetching conversations: ${(error as Error).message}`))
        } finally {
            await this.cleanup()
        }
    }
}