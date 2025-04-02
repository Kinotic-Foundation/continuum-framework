// src/commands/grok/config.ts
import { Command } from '@oclif/core';
import { input } from '@inquirer/prompts';
import chalk from 'chalk';
import { loadGrokConfig, saveGrokConfig, ConfigGrok } from '../../internal/state/ConfigGrok.js';

export default class Config extends Command {
    static description = 'Configures the Grok CLI with a cookie';

    static examples = ['z grok:config'];

    public async run(): Promise<void> {
        const config = await loadGrokConfig(this.config.configDir);

        const grokCookie = await input({
                                           message: 'Enter your Grok cookie (from grok.com browser session)',
                                           default: config.grokCookie || '',
                                       });

        if (grokCookie) {
            config.grokCookie = grokCookie;
            await saveGrokConfig(this.config.configDir, config);
            this.log(chalk.blue('Grok') + chalk.green(' Configured'));
        } else {
            this.log(chalk.red('No cookie provided. Configuration unchanged.'));
        }
    }
}