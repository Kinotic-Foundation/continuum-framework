// src/internal/state/ConfigGrok.ts
import { createStateManager } from './IStateManager.js';

const CONFIG_KEY = 'grok-config';

export class ConfigGrok {
    grokCookie?: string; // Cookie for Grok authentication
    activeConversationId?: string; // Current active conversation ID
}

export async function loadGrokConfig(dataDir: string): Promise<ConfigGrok> {
    const stateManager = createStateManager(dataDir);
    if (await stateManager.containsState(CONFIG_KEY)) {
        return await stateManager.load<ConfigGrok>(CONFIG_KEY);
    } else {
        return new ConfigGrok();
    }
}

export async function saveGrokConfig(dataDir: string, config: ConfigGrok): Promise<void> {
    const stateManager = createStateManager(dataDir);
    await stateManager.save(CONFIG_KEY, config);
}