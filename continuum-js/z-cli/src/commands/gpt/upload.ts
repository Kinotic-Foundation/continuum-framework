import { Command, Args, Flags } from '@oclif/core'
import { OpenAI } from 'openai'
import fs from 'fs/promises'
import { createReadStream } from 'fs'
import { glob } from 'glob'
import cliProgress from 'cli-progress'
import chalk from 'chalk'
import { loadGptConfig } from '../../internal/state/GptConfig.js'

export default class Upload extends Command {
    static description = 'Upload files to a ChatGPT assistant for the file_search tool'

    static override args = {
        pattern: Args.string({
                                 description: `File path or glob pattern to upload. Examples:
  - './data/*.txt' (all .txt files in the data directory)
  - './data/**/*.json' (all .json files recursively in the data directory)
  - '/absolute/path/to/file.csv' (specific file by absolute path)`,
                                 required: true
                             })
    }

    static override flags = {
        dryRun: Flags.boolean({
                                  description: 'Print the files that will be uploaded without actually uploading them',
                                  default: false
                              })
    }

    async run(): Promise<void> {
        try {
            const { args, flags } = await this.parse(Upload)
            const pattern = args.pattern
            const dryRun = flags.dryRun

            // Load configuration
            const config = await loadGptConfig(this.config.configDir)
            const assistantId = config.defaultAssistantId

            if (!assistantId) {
                this.log(chalk.red('No default assistant configured. Please set a default assistant first.'))
                return
            }

            // Initialize the OpenAI API client
            const openai = new OpenAI({ apiKey: config.openAIKey })

            // Resolve files based on the glob pattern
            const files = await glob(pattern, { nodir: true, absolute: true })

            if (files.length === 0) {
                this.log(chalk.red('No files matched the given pattern.'))
                return
            }

            if (dryRun) {
                this.log(chalk.yellow('Dry run: The following files would be uploaded:'))
                files.forEach(file => this.log(file))
                return
            }
            const uploadedFileIds = await this.uploadFiles(files, openai)

            if (uploadedFileIds.length === 0) {
                this.log(chalk.red('No files were uploaded successfully.'))
                return
            }

            // Retrieve existing vector store ID if available
            const assistant = await openai.beta.assistants.retrieve(assistantId)
            const existingVectorStoreIds = assistant.tool_resources?.file_search?.vector_store_ids || []

            if (existingVectorStoreIds.length > 0) {
                // Update the existing vector store with new file IDs using fileBatches
                const vectorStoreId = existingVectorStoreIds[0]
                await openai.beta.vectorStores.fileBatches.create(vectorStoreId, {
                    file_ids: uploadedFileIds
                })
            } else {
                // Create a new vector store if none exists
                const vectorStore = await openai.beta.vectorStores.create({
                                                                              file_ids: uploadedFileIds
                                                                          })

                // Update assistant with the new vector store ID
                await openai.beta.assistants.update(assistantId, {
                    tool_resources: {
                        file_search: {
                            vector_store_ids: [vectorStore.id]
                        }
                    }
                })
            }

            this.log(chalk.green('All files uploaded and assistant updated successfully.'))
        } catch (error: any) {
            this.log(chalk.red(`Error uploading files or updating assistant: ${error.message}`))
        }
    }

    private async uploadFiles(files: string[], openai: OpenAI) {
        // Setup progress bar
        const progressBar = new cliProgress.SingleBar({
                                                          format: 'Uploading {bar} {percentage}% | {value}/{total} files',
                                                      }, cliProgress.Presets.shades_classic)

        progressBar.start(files.length, 0)

        const uploadedFileIds: string[] = []

        for (let i = 0; i < files.length; i++) {
            const filePath = files[i]

            try {
                await fs.access(filePath)
                const fileStream = createReadStream(filePath)

                const file = await openai.files.create({
                                                           file   : fileStream,
                                                           purpose: 'assistants'
                                                       })

                uploadedFileIds.push(file.id)
                progressBar.update(i + 1)
            } catch (error: any) {
                this.log(chalk.red(`\nFailed to upload ${filePath}: ${error.message}`))
            }
        }

        progressBar.stop()
        return uploadedFileIds
    }
}
