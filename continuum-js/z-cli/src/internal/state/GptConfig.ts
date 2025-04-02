import {createStateManager} from './IStateManager.js'

const CONFIG_KEY = 'config'

export class GptConfig {
    openAIKey!: string;
    defaultAssistantId!: string;
}

export async function loadGptConfig(dataDir: string): Promise<GptConfig> {
    const stateManager = createStateManager(dataDir)
    if (await stateManager.containsState(CONFIG_KEY)) {
        return await stateManager.load<GptConfig>(CONFIG_KEY)
    } else {
        return new GptConfig()
    }
}

export async function saveGptConfig(dataDir: string, config: GptConfig): Promise<void> {
    const stateManager = createStateManager(dataDir)
    await stateManager.save(CONFIG_KEY, config)
}
