import {input, select} from '@inquirer/prompts'
import {Flags} from '@oclif/core'
import chalk from 'chalk'
import fs from 'fs/promises'
import {glob} from 'glob'
import path from 'path'
import {Page} from 'puppeteer'
import {ConfigGrok, loadGrokConfig, saveGrokConfig} from '../../internal/state/ConfigGrok.js'
import {GrokTool, toolRegistry} from '../../internal/tools/GrokTool.js'
import {ChatStreamer} from '../../internal/utils/ChatStreamer.js'
import {BaseGrokCommand} from './BaseGrokCommand.js'
import '../../internal/tools/FileTool.js'

export default class Chat extends BaseGrokCommand {
    static description = 'Start an interactive chat session with Grok 3'

    static flags = {
        conversation: Flags.string({
                                       char: 'c',
                                       description: 'Use a specific conversation ID (overrides active ID)',
                                       name: 'conversation-id'
                                   }),
        files: Flags.string({
                                char: 'f',
                                description: 'Glob pattern of files to upload (e.g., "./docs/*.txt")',
                                name: 'files'
                            }),
        visible: Flags.boolean({
                                   char: 'v',
                                   description: 'Run with visible browser (non-headless mode)',
                                   name: 'visible',
                                   default: false
                               }),
        tools: Flags.string({
                                char: 't',
                                description: 'Tool to enable (e.g., "file"); only one allowed',
                                name: 'tools'
                            })
    }

    private page!: Page
    private conversationId!: string
    private enabledTool: GrokTool | null = null
    private filePaths: string[] = []
    private fileIds: string[] = []
    private chatStreamer!: ChatStreamer
    private initialParentResponseId: string = ''
    private grokConfig!: ConfigGrok

    async run(): Promise<void> {
        const { flags } = await this.parse(Chat)
        await this.initialize(flags)

        try {
            await this.setupChatPage(flags.visible)
            await this.uploadInitialFiles(flags.files)

            this.log(chalk.blue('Interactive chat started. Type ') + chalk.yellow(':') + chalk.blue(' for menu, ') + chalk.yellow('\\q') + chalk.blue(' to exit'))
            await this.chatLoop(flags.visible)
        } catch (error) {
            this.log(chalk.red(`Error: ${(error as Error).message}`))
        } finally {
            await this.cleanup()
        }
    }

    private async initialize(flags: any): Promise<void> {
        this.grokConfig = await loadGrokConfig(this.config.configDir)
        this.conversationId = flags.conversation || this.grokConfig.activeConversationId || crypto.randomUUID()
        if (this.conversationId !== this.grokConfig.activeConversationId) {
            this.grokConfig.activeConversationId = this.conversationId
            await saveGrokConfig(this.config.configDir, this.grokConfig)
            this.log(chalk.yellow(`Active conversation set to: ${this.conversationId}`))
        }

        if (flags.tools) {
            const toolName = flags.tools.split(',').map((t: string) => t.trim())[0]
            const tool = toolRegistry[toolName]
            if (tool) {
                this.enabledTool = tool
                this.log(chalk.blue(`Enabled tool: ${tool.name} - ${tool.description}`))
            } else {
                this.log(chalk.red(`Unknown tool: ${toolName}`))
            }
        }
    }

    private async setupChatPage(visible: boolean): Promise<void> {
        this.page = await this.setupBrowser(`https://grok.com/chat/${this.conversationId}`, undefined, !visible)

        const historyResponse = await this.page.evaluate(async (url) => {
            const response = await fetch(url, {
                method: 'GET',
                headers: {
                    'Accept': '*/*',
                    'Origin': 'https://grok.com',
                    'Referer': 'https://grok.com/'
                }
            })
            return await response.json()
        }, `https://grok.com/rest/app-chat/conversations/${this.conversationId}/response-node`)

        if (historyResponse.responseNodes && historyResponse.responseNodes.length > 0) {
            const assistantResponses = historyResponse.responseNodes
                                                      .filter((node: any) => node.sender === 'ASSISTANT')
            if (assistantResponses.length > 0) {
                const lastAssistantResponse = assistantResponses[assistantResponses.length - 1]
                this.initialParentResponseId = lastAssistantResponse.responseId
                this.log(chalk.gray(`Loaded conversation context with parentResponseId: ${this.initialParentResponseId}`))
            }
        }

        this.chatStreamer = new ChatStreamer(this.page, this.conversationId, this.initialParentResponseId)
        try {
            await this.page.waitForSelector('textarea[aria-label="Ask Grok anything"]', { timeout: 30000 })
        } catch (error) {
            this.log(chalk.red('Timed out waiting for chat UI'))
            const content = await this.page.content()
            this.log(chalk.yellow('Page content after timeout:'), content)
            throw new Error('Failed to load chat UI')
        }
    }

    private async uploadInitialFiles(filesFlag?: string): Promise<void> {
        if (filesFlag) {
            this.filePaths = await glob(filesFlag)
            if (this.filePaths.length > 0) {
                await this.uploadFiles(this.filePaths)
            } else {
                this.log(chalk.yellow(`No files matched glob pattern: ${filesFlag}`))
            }
        }
    }

    private async uploadFiles(paths: string[]): Promise<void> {
        for (const filePath of paths) {
            try {
                const contentBuffer = await fs.readFile(filePath) // Read as raw Buffer
                const content = contentBuffer.toString('base64') // Encode to base64
                const fileName = path.basename(filePath)
                const uploadResponse = await this.page.evaluate(async (fileData) => {
                    const response = await fetch('/rest/app-chat/upload-file', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                            'Accept': '*/*',
                            'Origin': 'https://grok.com',
                            'Referer': 'https://grok.com/'
                        },
                        body: JSON.stringify(fileData)
                    })
                    return await response.json()
                }, { fileName, fileMimeType: '', content }) // Send base64 content

                const fileId = uploadResponse.fileMetadataId
                if (fileId) {
                    this.fileIds.push(fileId)
                    this.log(chalk.green(`Uploaded file: ${fileName} (ID: ${fileId})`))
                } else {
                    this.log(chalk.red(`Failed to get file ID for ${fileName}. Response: ${JSON.stringify(uploadResponse)}`))
                }
            } catch (error) {
                this.log(chalk.red(`Upload error for ${filePath}: ${(error as Error).message}`))
            }
        }
    }

    private async chatLoop(verbose: boolean): Promise<void> {
        let chatActive = true
        while (chatActive) {
            const userInput = await input({ message: chalk.green('You:') })

            if (userInput.trim() === '\\q') {
                chatActive = false
            } else if (userInput.trim() === ':') {
                await this.showMenu()
            } else {
                await this.chatStreamer.streamChat(userInput, this.fileIds, this.enabledTool, verbose)
            }
        }
        this.log(chalk.blue('Chat session ended'))
    }

    private async showMenu(): Promise<void> {
        const choices = [
            { name: 'Toggle Tool', value: 'tool' },
            { name: 'Upload Files', value: 'files' },
            { name: 'Save Point in Time', value: 'save' },
            { name: 'Load Point in Time', value: 'load' },
            { name: 'Back to Chat', value: 'back' }
        ]
        const action = await select({
                                        message: chalk.blue('Chat Menu:'),
                                        choices
                                    })

        switch (action) {
            case 'tool':
                await this.toggleTool()
                break
            case 'files':
                await this.uploadMoreFiles()
                break
            case 'save':
                await this.savePointInTime()
                break
            case 'load':
                await this.loadPointInTime()
                break
            case 'back':
                break
        }
    }

    private async toggleTool(): Promise<void> {
        console.log(chalk.blue(`Available tools in registry: ${JSON.stringify(Object.keys(toolRegistry))}`))
        const toolChoices = Object.values(toolRegistry).map(tool => ({
            name: `${tool.name} - ${tool.description}`,
            value: tool.name,
            checked: this.enabledTool?.name === tool.name
        }))
        toolChoices.unshift({ name: 'None', value: 'none', checked: !this.enabledTool })
        const selectedToolName = await select({
                                                  message: chalk.blue('Select active tool:'),
                                                  choices: toolChoices
                                              })
        this.enabledTool = selectedToolName === 'none' ? null : toolRegistry[selectedToolName]
        this.log(chalk.green(`Active tool: ${this.enabledTool ? this.enabledTool.name : 'none'}`))
    }

    private async uploadMoreFiles(): Promise<void> {
        const pattern = await input({ message: chalk.blue('Enter glob pattern for files to upload (e.g., "./docs/*.txt"):') })
        if (pattern) {
            const newFiles = await glob(pattern)
            if (newFiles.length > 0) {
                await this.uploadFiles(newFiles)
                this.filePaths.push(...newFiles)
            } else {
                this.log(chalk.yellow(`No files matched pattern: ${pattern}`))
            }
        }
    }

    private async savePointInTime(): Promise<void> {
        const name = await input({ message: chalk.blue('Enter name for this point in time:') })
        if (name) {
            const lastResponseId = this.chatStreamer.getLastResponseId()
            if (lastResponseId) {
                const label = `${this.conversationId}-${name}`
                const key = label
                // Ensure savedPoints is initialized
                if (!this.grokConfig.savedPoints) {
                    this.grokConfig.savedPoints = {}
                }
                this.grokConfig.savedPoints[key] = { label, previousResponseId: lastResponseId }
                await saveGrokConfig(this.config.configDir, this.grokConfig)
                this.log(chalk.green(`Saved point '${label}' with responseId: ${lastResponseId}`))
            } else {
                this.log(chalk.yellow('No response ID available to save'))
            }
        } else {
            this.log(chalk.red('Name cannot be empty'))
        }
    }

    private async loadPointInTime(): Promise<void> {
        if (Object.keys(this.grokConfig.savedPoints).length === 0) {
            this.log(chalk.yellow('No saved points available'))
            return
        }

        const choices = Object.entries(this.grokConfig.savedPoints).map(([key, point]) => ({
            name: `${point.label} (responseId: ${point.previousResponseId})`,
            value: key
        }))
        const selectedKey = await select({
                                             message: chalk.blue('Select a point in time to load:'),
                                             choices
                                         })

        const selectedPoint = this.grokConfig.savedPoints[selectedKey]
        this.conversationId = crypto.randomUUID()
        this.chatStreamer = new ChatStreamer(this.page, this.conversationId, selectedPoint.previousResponseId)
        this.fileIds = []
        this.grokConfig.activeConversationId = this.conversationId
        await saveGrokConfig(this.config.configDir, this.grokConfig)
        this.log(chalk.green(`Loaded point '${selectedPoint.label}' with responseId: ${selectedPoint.previousResponseId} in new conversation: ${this.conversationId}`))
    }

}