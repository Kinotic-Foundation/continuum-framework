import {JSONParser} from '@streamparser/json-node'
import chalk from 'chalk'
import ora from 'ora'
import {Page} from 'puppeteer'
import {Writable} from 'stream'
import {GrokModelResponse, GrokTool} from '../tools/GrokTool.js'

export class ChatStreamer {
    private lastResponseId: string

    constructor(private page: Page, private conversationId: string, initialParentResponseId: string = '') {
        this.lastResponseId = initialParentResponseId
    }

    async streamChat(userInput: string, fileIds: string[], tool: GrokTool | null, verbose: boolean = false): Promise<string> {
        const spinner = ora('Grok is typing...').start()
        try {
            let processedPrompt = userInput
            if (tool) {
                processedPrompt = await tool.preprocessPrompt(processedPrompt)
            }

            const payload = {
                temporary: false,
                modelName: 'grok-3',
                message: processedPrompt,
                fileAttachments: fileIds.slice(0, 10),
                imageAttachments: [],
                disableSearch: false,
                enableImageGeneration: true,
                returnImageBytes: false,
                returnRawGrokInXaiRequest: false,
                enableImageStreaming: true,
                imageGenerationCount: 2,
                forceConcise: false,
                toolOverrides: {},
                enableSideBySide: true,
                sendFinalMetadata: true,
                isReasoning: false,
                webpageUrls: [],
                disableTextFollowUps: true,
                parentResponseId: this.lastResponseId
            }

            let fullResponse = ''
            let spinnerStopped = false
            let modelResponse: GrokModelResponse | null = null

            const responsePromise = new Promise<string>((resolve) => {
                const timeout = setTimeout(() => resolve('Error: Response timeout after 5 minutes'), 300000)

                const parser = new JSONParser({
                                                  paths: ['$.result', '$.error'],
                                                  keepStack: false,
                                                  separator: ''
                                              })

                const chunkWriter = new Writable({
                                                     write(chunk, encoding, callback) {
                                                         const text = chunk.toString()
                                                         if (text.startsWith('STREAM_CHUNK:')) {
                                                             const jsonChunk = text.replace('STREAM_CHUNK:', '')
                                                             parser.write(jsonChunk)
                                                         } else if (text === 'STREAM_END') {
                                                             parser.end()
                                                         } else if (text.startsWith('STREAM_ERROR:')) {
                                                             const errorMsg = text.replace('STREAM_ERROR:', '')
                                                             console.log(chalk.red(`Error: ${errorMsg}`))
                                                             resolve('')
                                                         }
                                                         callback()
                                                     }
                                                 })

                parser.on('data', ({ value }) => {
                    if (value && 'code' in value && 'message' in value) {
                        console.log(chalk.red(`Error: ${JSON.stringify(value)}`))
                        resolve('')
                        return
                    }

                    if (value?.token && typeof value.token === 'string' && value.token !== '' && !tool) {
                        if (!spinnerStopped) {
                            spinner.stop()
                            spinnerStopped = true
                        }
                        fullResponse += value.token
                        if (tool && verbose) {
                            process.stdout.write(chalk.gray(value.token))
                        } else if (!tool) {
                            process.stdout.write(chalk.white(value.token))
                        }
                    } else if (value?.modelResponse) {
                        if (!spinnerStopped) {
                            spinner.stop()
                            spinnerStopped = true
                        }
                        fullResponse = value.modelResponse.message
                        this.lastResponseId = value.modelResponse.responseId
                        modelResponse = value.modelResponse
                        if (!tool) {
                            process.stdout.write(chalk.white(fullResponse))
                        }
                    }
                })

                parser.on('error', (error: Error) => {
                    console.log(chalk.yellow(`Stream parse error: ${error.message}`))
                })

                parser.on('end', () => {
                    clearTimeout(timeout)
                    this.page.off('console', handleConsole)
                    resolve(fullResponse)
                })

                const handleConsole = (msg: any) => {
                    chunkWriter.write(msg.text())
                }

                this.page.on('console', handleConsole)

                this.page.evaluate(async (url, body) => {
                    const response = await fetch(url, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                            'Accept': '*/*',
                            'Origin': 'https://grok.com',
                            'Referer': 'https://grok.com/'
                        },
                        body: JSON.stringify(body)
                    })
                    if (!response.body) {
                        console.log('STREAM_ERROR: No response body')
                        return
                    }
                    const reader = response.body.getReader()
                    const decoder = new TextDecoder()

                    while (true) {
                        const { done, value } = await reader.read()
                        if (done) {
                            console.log('STREAM_END')
                            break
                        }
                        const chunk = decoder.decode(value, { stream: true })
                        console.log('STREAM_CHUNK:' + chunk)
                    }
                }, `https://grok.com/rest/app-chat/conversations/${this.conversationId}/responses`, payload)
            })

            let finalOutput = await responsePromise
            if (tool && modelResponse) {
                finalOutput = await tool.postprocessResponse(modelResponse)
            }

            if (!spinnerStopped) spinner.stop()
            process.stdout.write('\n')
            return finalOutput
        } catch (error) {
            spinner.fail(chalk.red(`Chat error: ${(error as Error).message}`))
            return ''
        }
    }

    getLastResponseId(): string {
        return this.lastResponseId
    }
}