import { Command } from '@oclif/core'
import chalk from 'chalk'
import { OpenAI } from 'openai'
import { select, Separator } from '@inquirer/prompts'
import {loadGptConfig, saveGptConfig} from '../../internal/state/GptConfig.js'

export default class Assistants extends Command {
    static description = 'Select the default ChatGPT assistants to use'

    static override examples = [
        '<%= config.bin %> <%= command.id %>',
    ]

    async run(): Promise<void> {
        try {
            // Initialize the OpenAI API client
            const config = await loadGptConfig(this.config.configDir)
            const openai = new OpenAI({ apiKey: config.openAIKey })

            // Fetch the list of assistants
            const assistants = await openai.beta.assistants.list({ limit: 20 })

            if (!assistants || assistants.data.length === 0) {
                this.log('No assistants found.')
                return
            }

            // Prepare choices for inquirer
            const choices: any = assistants.data.map((assistant) => ({
                name: `${assistant.name} - ${assistant.description || 'No description'}`,
                value: assistant.id,
            }))

            // Add a separator and cancel option
            choices.push(new Separator())
            choices.push({ name: chalk.red('Cancel'), value: 'cancel' })

            // Prompt the user to select an assistant
            const selectedAssistantId: string = await select({
                                                         message: chalk.green('Select a ChatGPT Assistant:'),
                                                         choices,
                                                     })

            if (selectedAssistantId === 'cancel') {
                this.log(chalk.red('Operation cancelled.'))
                return
            }

            config.defaultAssistantId = selectedAssistantId
            await saveGptConfig(this.config.configDir, config)

            this.log(`Selected Assistant ID: ${chalk.green(selectedAssistantId)}`)

            // Fetch the selected assistant details
            const assistant = await openai.beta.assistants.retrieve(selectedAssistantId as string)
            this.log('Selected Assistant Details:', assistant)
        } catch (error: any) {
            this.error('Error fetching assistants:', { message: error.message })
        }
    }
}
