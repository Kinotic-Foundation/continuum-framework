import { Command } from '@oclif/core'
import { OpenAI } from 'openai'
import readline from 'readline'
import chalk from 'chalk'
import ora from 'ora'
import { loadConfig } from '../../internal/state/Config.js'

export default class Chat extends Command {
    static description = 'Start an interactive chat session with the default assistant'

    async run(): Promise<void> {
        try {
            const config = await loadConfig(this.config.configDir)
            const assistantId = config.defaultAssistantId

            if (!assistantId) {
                this.log(chalk.red('No default assistant configured. Please set a default assistant first.'))
                return
            }

            const openai = new OpenAI({ apiKey: config.openAIKey })

            const thread = await openai.beta.threads.create()
            const threadId = thread.id

            const rl = readline.createInterface({
                                                    input: process.stdin,
                                                    output: process.stdout
                                                })

            this.log(chalk.blue('Interactive chat session started. Type ') + chalk.yellow('\q') + chalk.blue(' to exit.'))

            const promptUser = async () => {
                rl.question(chalk.green('You: '), async (input) => {
                    if (input.trim() === '\q') {
                        rl.close()
                        this.log(chalk.blue('Chat session ended.'))
                        return
                    }

                    const spinner = ora('Assistant is typing...').start()

                    try {
                        await openai.beta.threads.messages.create(threadId, {
                            role: 'user',
                            content: input
                        })

                        const run = await openai.beta.threads.runs.create(threadId, {
                            assistant_id: assistantId
                        })

                        let runStatus = await openai.beta.threads.runs.retrieve(threadId, run.id)
                        while (runStatus.status !== 'completed') {
                            if (runStatus.status === 'failed') {
                                spinner.fail(chalk.red('Assistant run failed.'))
                                rl.close()
                                return
                            }
                            await new Promise(resolve => setTimeout(resolve, 1000))
                            runStatus = await openai.beta.threads.runs.retrieve(threadId, run.id)
                        }

                        spinner.stop()

                        const messages = await openai.beta.threads.messages.list(threadId)
                        const lastMessage = messages.data.find(m => m.role === 'assistant')

                        if (lastMessage && Array.isArray(lastMessage.content)) {
                            const textContent = lastMessage.content
                                                           .map(part => ('text' in part && typeof part.text === 'object' && 'value' in part.text ? part.text.value : ''))
                                                           .join('\n')
                            this.log(chalk.cyan('Assistant: ') + chalk.white(textContent))
                        } else {
                            this.log(chalk.red('Assistant: (No response)'))
                        }

                        await promptUser()
                    } catch (error: any) {
                        spinner.fail(chalk.red(`Error communicating with assistant: ${error.message}`))
                    }
                })
            }
            await promptUser()
        } catch (error: any) {
            this.log(chalk.red(`Error starting chat session: ${error.message}`))
        }
    }
}
