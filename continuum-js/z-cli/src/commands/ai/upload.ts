import { Command, Args, Flags } from '@oclif/core'
import { OpenAI } from 'openai'
import fs from 'fs/promises'
import { createReadStream } from 'fs'
import { glob } from 'glob'
import cliProgress from 'cli-progress'
import { loadConfig } from '../../internal/state/Config.js'

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
            const config = await loadConfig(this.config.configDir)
            const assistantId = config.defaultAssistantId

            if (!assistantId) {
                this.error('No default assistant configured. Please set a default assistant first.')
            }

            // Initialize the OpenAI API client using config key
            const openai = new OpenAI({ apiKey: config.openAIKey })

            // Resolve files based on the glob pattern
            const files = await glob(pattern, { nodir: true, absolute: true })

            if (files.length === 0) {
                this.error('No files matched the given pattern.')
            }

            if (dryRun) {
                this.log('Dry run: The following files would be uploaded:')
                files.forEach(file => this.log(file))
                return
            }

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
                } catch {
                    this.warn(`File does not exist: ${filePath}`)
                    continue
                }

                const fileStream = createReadStream(filePath)

                const file = await openai.files.create({
                                                           file: fileStream,
                                                           purpose: 'assistants'
                                                       })

                uploadedFileIds.push(file.id)
                progressBar.update(i + 1)
            }

            progressBar.stop()

            // Create a vector store and add uploaded files
            const vectorStore = await openai.beta.vectorStores.create({
                                                                          file_ids: uploadedFileIds
                                                                      })

            // Update assistant with vector store ID
            await openai.beta.assistants.update(assistantId, {
                tool_resources: {
                    file_search: {
                        vector_store_ids: [vectorStore.id]
                    }
                }
            })

            this.log('All files uploaded and assistant updated successfully')
        } catch (error: any) {
            this.error('Error uploading files or updating assistant:', { message: error.message })
        }
    }
}
