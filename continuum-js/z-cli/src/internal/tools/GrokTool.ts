export interface GrokModelResponse {
    responseId: string
    message: string // JSON string for FileTool, plain text otherwise
    sender: 'ASSISTANT' | 'HUMAN'
    createTime: string
    parentResponseId: string
    manual: boolean
    partial: boolean
    shared: boolean
    query: string
    queryType: string
    webSearchResults: any[]
    xpostIds: string[]
    xposts: any[]
    generatedImageUrls: string[]
    imageAttachments: any[]
    fileAttachments: any[]
    cardAttachmentsJson: any[]
    fileUris: string[]
    fileAttachmentsMetadata: any[]
    isControl: boolean
    steps: any[]
    imageEditUris: string[]
    mediaTypes: string[]
    webpageUrls: string[]
    metadata: { deepsearchPreset: string }
}

export interface GrokTool {
    name: string
    description: string
    preprocessPrompt(prompt: string): Promise<string>
    postprocessResponse(response: GrokModelResponse): Promise<string>
}

export const toolRegistry: { [key: string]: GrokTool } = {}