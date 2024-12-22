import {createStateManager} from './IStateManager.js'

const CONFIG_KEY = 'config'

export class Config {
    openAIKey!: string;
    defaultAssistantId!: string;
}

export async function loadConfig(dataDir: string): Promise<Config> {
    const stateManager = createStateManager(dataDir)
    if (await stateManager.containsState(CONFIG_KEY)) {
        return await stateManager.load<Config>(CONFIG_KEY)
    } else {
        return new Config()
    }
}

export async function saveConfig(dataDir: string, config: Config): Promise<void> {
    const stateManager = createStateManager(dataDir)
    await stateManager.save(CONFIG_KEY, config)
}
