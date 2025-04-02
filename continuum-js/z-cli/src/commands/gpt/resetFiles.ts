import { Command } from '@oclif/core'
import { OpenAI } from 'openai'
import chalk from 'chalk'
import cliProgress from 'cli-progress'
import { loadGptConfig } from '../../internal/state/GptConfig.js'

/**
 * ResetFiles command to delete all files listed in the OpenAI file management API.
 */
export default class ResetFiles extends Command {
    static description = 'Delete all files listed in the OpenAI file management API.'

    async run(): Promise<void> {
        try {
            // Load configuration
            const config = await loadGptConfig(this.config.configDir)

            // Initialize OpenAI client
            const openai = new OpenAI({ apiKey: config.openAIKey })

            // Retrieve all files
            const files = await openai.files.list()
            const fileIds = files.data.map(file => file.id)

            if (fileIds.length === 0) {
                this.log(chalk.yellow('No files found in OpenAI storage.'))
                return
            }

            // Setup progress bar
            const progressBar = new cliProgress.SingleBar({
                                                              format: 'Deleting files {bar} {percentage}% | {value}/{total}',
                                                          }, cliProgress.Presets.shades_classic)

            progressBar.start(fileIds.length, 0)

            // Delete each file
            for (let i = 0; i < fileIds.length; i++) {
                await openai.files.del(fileIds[i])
                progressBar.update(i + 1)
            }

            progressBar.stop()
            this.log(chalk.green('All files deleted successfully.'))

        } catch (error: any) {
            this.log(chalk.red(`Error deleting files: ${error.message}`))
        }
    }
}
