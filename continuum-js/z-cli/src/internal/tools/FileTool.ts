import fs from 'fs/promises'
import path from 'path'
import chalk from 'chalk'
import { GrokTool, toolRegistry, GrokModelResponse } from './GrokTool.js'

interface FileEntry {
    name: string
    path: string
    body: string
}

export class FileTool implements GrokTool {
    name = 'file'
    description = 'Requests responses as JSON with a summary and files array, then writes them to the filesystem within the current directory'
    private summary: string | null = null
    private files: FileEntry[] = []

    constructor() {}

    async preprocessPrompt(prompt: string): Promise<string> {
        return `${prompt}\n\nReturn *only* a JSON object with no additional text before or after. The JSON must contain:
- A 'summary' field with a brief summary of your response as a string.
- A 'files' array where each entry is an object with:
  - 'name': a descriptive file name (e.g., 'script.js' or 'readme.md'),
  - 'path': a relative path (e.g., './src' or './docs'),
  - 'body': the file content as a string.
Ensure the files are relevant to the request and use appropriate extensions for the content type. Example: {"summary":"Summary text","files":[{"name":"example.md","path":"./docs","body":"Content"}]}`
    }

    private async writeFile(file: FileEntry): Promise<void> {
        const cwd = process.cwd()
        const fullPath = path.resolve(cwd, file.path, file.name) // Resolve to absolute path
        if (!fullPath.startsWith(cwd)) {
            console.warn(`Rejected file '${file.name}' at '${file.path}': Path attempts to escape current directory`)
            return
        }
        await fs.mkdir(path.dirname(fullPath), { recursive: true })
        await fs.writeFile(fullPath, file.body, 'utf-8')
        console.log(chalk.white(`Writing file: ${fullPath}`))
    }

    async postprocessResponse(response: GrokModelResponse): Promise<string> {
        try {
            const json = JSON.parse(response.message)
            if (!json.summary || !Array.isArray(json.files)) {
                return `Error: Invalid response format. Expected JSON with 'summary' and 'files'. Received: ${JSON.stringify(json)}`
            }
            this.summary = json.summary + '\n'
            process.stdout.write(chalk.white(this.summary))
            this.files = json.files.filter((file: FileEntry) => file.name && file.path && file.body)
            for (const file of this.files) {
                await this.writeFile(file)
            }
            console.log(chalk.white('Done! Check your directory.'))
            return this.summary || ''
        } catch (error) {
            return `Error processing file tool response: ${(error as Error).message}\nRaw response: ${JSON.stringify(response)}`
        }
    }
}

toolRegistry['file'] = new FileTool()