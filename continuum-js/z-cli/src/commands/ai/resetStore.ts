import { Command } from '@oclif/core'
import { OpenAI } from 'openai'
import chalk from 'chalk'
import cliProgress from 'cli-progress'
import { loadConfig } from '../../internal/state/Config.js'

export default class ResetStore extends Command {
    static description = 'Reset the vector store for the current assistant by deleting all files and removing the store.'

    async run(): Promise<void> {
        try {
            const config = await loadConfig(this.config.configDir)
            const assistantId = config.defaultAssistantId

            if (!assistantId) {
                this.log(chalk.red('No default assistant configured. Please set a default assistant first.'))
                return
            }

            const openai = new OpenAI({ apiKey: config.openAIKey })

            const assistant = await openai.beta.assistants.retrieve(assistantId)
            const vectorStoreIds = assistant.tool_resources?.file_search?.vector_store_ids || []

            if (vectorStoreIds.length === 0) {
                this.log(chalk.yellow('No vector store found for the assistant.'))
                return
            }

            const vectorStoreId = vectorStoreIds[0]

            // Fetch all files attached to the vector store
            const files = await openai.beta.vectorStores.files.list(vectorStoreId)
            const fileIds = files.data.map(file => file.id)

            if (fileIds.length === 0) {
                this.log(chalk.yellow('No files found in the vector store.'))
            } else {
                // Setup progress bar for file deletion
                const progressBar = new cliProgress.SingleBar({
                                                                  format: 'Deleting files {bar} {percentage}% | {value}/{total}',
                                                              }, cliProgress.Presets.shades_classic)

                progressBar.start(fileIds.length, 0)

                // Batch delete files from the vector store and permanently delete them
                for (let i = 0; i < fileIds.length; i++) {
                    await openai.beta.vectorStores.files.del(vectorStoreId, fileIds[i])
                    await openai.files.del(fileIds[i])
                    progressBar.update(i + 1)
                }

                progressBar.stop()
            }

            // Delete the vector store
            await openai.beta.vectorStores.del(vectorStoreId)

            // Update assistant to remove reference to vector store
            await openai.beta.assistants.update(assistantId, {
                tool_resources: {
                    file_search: {
                        vector_store_ids: []
                    }
                }
            })

            this.log(chalk.green('Vector store and files reset successfully.'))
        } catch (error: any) {
            this.log(chalk.red(`Error resetting vector store: ${error.message}`))
        }
    }
}
