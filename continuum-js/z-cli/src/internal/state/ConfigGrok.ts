import { createStateManager } from './IStateManager.js'

const CONFIG_KEY = 'grok-config'

export interface SavedPoint {
    label: string
    previousResponseId: string
}

export class ConfigGrok {
    grokCookie?: string
    activeConversationId?: string
    savedPoints: { [key: string]: SavedPoint } = {}
}

export async function loadGrokConfig(dataDir: string): Promise<ConfigGrok> {
    const stateManager = createStateManager(dataDir)
    if (await stateManager.containsState(CONFIG_KEY)) {
        const loadedConfig = await stateManager.load<ConfigGrok>(CONFIG_KEY)
        // Merge loaded config with defaults to ensure savedPoints exists
        return { ...new ConfigGrok(), ...loadedConfig }
    } else {
        return new ConfigGrok()
    }
}

export async function saveGrokConfig(dataDir: string, config: ConfigGrok): Promise<void> {
    const stateManager = createStateManager(dataDir)
    await stateManager.save(CONFIG_KEY, config)
}