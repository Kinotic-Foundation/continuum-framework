import { Command } from '@oclif/core'
import { OpenAI } from 'openai'
import { input } from '@inquirer/prompts'
import chalk from 'chalk'
import ora from 'ora'
import { loadGptConfig } from '../../internal/state/GptConfig.js'

export default class Chat extends Command {
    static description = 'Start an interactive chat session with the default assistant'

    async run(): Promise<void> {
        try {
            const config = await loadGptConfig(this.config.configDir)
            const assistantId = config.defaultAssistantId

            if (!assistantId) {
                this.log(chalk.red('No default assistant configured. Please set a default assistant first.'))
                return
            }

            const openai = new OpenAI({ apiKey: config.openAIKey })
            const thread = await openai.beta.threads.create()
            const threadId = thread.id

            this.log(chalk.blue('Interactive chat session started. Type ') + chalk.yellow('\\q') + chalk.blue(' to exit.'))

            let chatActive = true
            while (chatActive) {
                const userInput = await input({ message: chalk.green('You:') })

                if (userInput.trim() !== '\\q') {
                    const spinner = ora('Assistant is typing...').start()
                    let textContent = chalk.red('Assistant: (No response)')

                    try {
                        const message = await openai.beta.threads.messages.create(threadId, {
                            role: 'user',
                            content: userInput
                        })

                        const run = await openai.beta.threads.runs.create(threadId, {
                            assistant_id: assistantId
                        })

                        let runStatus = await openai.beta.threads.runs.retrieve(threadId, run.id)
                        while (runStatus.status !== 'completed') {
                            if (runStatus.status === 'failed') {
                                spinner.fail(chalk.red('Assistant run failed. Please try again.'))
                                chatActive = false
                                break
                            }
                            await new Promise(resolve => setTimeout(resolve, 1000))
                            runStatus = await openai.beta.threads.runs.retrieve(threadId, run.id)
                        }

                        spinner.stop()

                        const messages = await openai.beta.threads.messages.list(threadId)
                        const lastMessage = messages.data.find(m => m.role === 'assistant')

                        textContent = lastMessage && Array.isArray(lastMessage.content)
                                      ? lastMessage.content
                                                   .map(part => ('text' in part && typeof part.text === 'object' && 'value' in part.text ? part.text.value : ''))
                                                   .join('\n')
                                      : textContent

                    } catch (error: any) {
                        spinner.fail(chalk.red(`Error: ${error.message}`))
                        chatActive = false
                    }

                    this.log(chalk.cyan('Assistant: \n') + chalk.white(textContent))
                } else {
                    chatActive = false
                }
            }

            this.log(chalk.blue('Chat session ended.'))
        } catch (error: any) {
            this.log(chalk.red(`Error starting chat: ${error.message}`))
        }
    }
}
