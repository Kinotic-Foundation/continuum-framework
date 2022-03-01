import fs from 'fs'
import {Dirent} from 'fs'
import fsP from 'fs/promises'
import makeDir from 'make-dir'
import path from 'path'
import { Liquid } from 'liquidjs'
import { settings as oclifSettings } from '@oclif/core'
import {SpawnConfig, GlobalsType, PropertySchemaType} from './SpawnConfig'
import inquirer from 'inquirer'
import {JSONSchema7} from 'json-schema'
import {spawnResolver, SpawnResolver} from './SpawnResolver'
import camelCase from 'lodash/camelCase'
import upperFirst from 'lodash/upperFirst'

export default class SpawnEngine {

  private engine: Liquid
  private spawnResolver: SpawnResolver

  constructor(spawnResolver: SpawnResolver) {
    this.spawnResolver = spawnResolver
    this.engine = new Liquid({
                               cache: true
                             })
    this.engine.registerFilter('packageToPath', v => v.replaceAll('.', '/'))
    this.engine.registerFilter('encodePackage', (v: string) => {
      v = v.replaceAll('-', "_")
      v = v.replaceAll('/\\.(\\d+)/', '._$1')
      return v
    })
    this.engine.registerFilter('camelCase', v => camelCase(v))
    this.engine.registerFilter('upperFirst', v => upperFirst(v))
  }

  // public async renderFile(source: string, destination: string, context?: object): Promise<void> {
  //   return this._renderFile(source, destination, true, context)
  // }

  /**
   * Renders the specified Spawn
   * A Spawn is a directory that contains templates, an optional spawn.json file, and template parameters in folder and filenames
   *
   * This is done by performing the following
   *
   * - Will recursively walk the spawn copying any files or directories encountered
   * -- If a template file is encountered it will be parsed and rendered prior to copying
   *
   * @param spawn the name of the spawn to parse and render. This is the name of the directory containing the spawn.json
   * @param destination the target directory where rendered data will be sent
   * @param context the values to be provided to the templates while rendering
   * @return a promise containing all the original values plus any added during rendering
   */
  public async renderSpawn(spawn: string, destination: string, context?: object): Promise<object|undefined> {
    if(!fs.existsSync(destination)){

      let source:string = await this.spawnResolver.resolveSpawn(spawn)
      let sources: string[] = [source]

      // process spawn config if found
      let currentConfig: string = path.resolve(source, 'spawn.json')
      if(fs.existsSync(currentConfig)){
        let spawns: SpawnConfig[] = []
        let currentSpawn: SpawnConfig = JSON.parse(fs.readFileSync(currentConfig,{encoding:'utf8'}))
        spawns.push(currentSpawn)

        // follow inheritance and build stack
        while (currentSpawn.inherits){
          let inheritDir = path.resolve(path.dirname(currentConfig), currentSpawn.inherits)
          currentConfig = path.resolve(inheritDir, 'spawn.json')

          this.logDebug(`Inheriting from ${currentConfig}\n`)

          if(!fs.existsSync(currentConfig)){
            throw new Error(`Inherited spawn ${currentConfig} does not exist`)
          }
          currentSpawn = JSON.parse(fs.readFileSync(currentConfig,{encoding:'utf8'}))

          spawns.push(currentSpawn)
          sources.push(inheritDir)
        }

        let globals: GlobalsType = {}
        let propertySchemas: PropertySchemaType = {}

        for(let spawn of spawns.reverse()){
          // merge all globals
          if(spawn.globals){
            globals = {...globals, ...spawn.globals}
          }

          // merge all property schemas
          if(spawn.propertySchema){
            propertySchemas = {...propertySchemas, ...spawn.propertySchema}
          }
        }

        // now merge globals with context, context taking precedence
        context = {...globals, ...context}

        // now prompt user for any needed properties not provided in the context
        context = await this.promptForMissingProperties(propertySchemas, context)
      }

      await makeDir(destination)

      for(let src of sources.reverse()){
        await this._renderDirectory(src, src, destination, context)
      }

      return context

    }else{
      throw new Error(`The target directory ${destination} already exists`)
    }
  }

  private async promptForMissingProperties(propertySchema: PropertySchemaType, context?: object): Promise<object>{
    if(!context){
      context = {}
    }
    let questions: any[] = []
    for(let key in propertySchema){

      if(!context.hasOwnProperty(key)) {
        let schema: JSONSchema7 = propertySchema[key]
        let question: any = {name: key}

        if (schema.description?.includes('{{')) {
          question.message = (answers: object) => {
            return this.engine.parseAndRenderSync(schema.description as string, answers)
          }
        } else {
          question.message = schema.description
        }

        if (typeof (schema.default) === 'string' && schema.default.includes('{{')) {
          question.default = (answers: object) => {
            return this.engine.parseAndRenderSync(schema.default as string, answers)
          }
        } else {
          question.default = schema.default
        }

        if (schema.type === 'boolean') {
          question.type = 'confirm'
        } else if (schema.enum) {
          question.type = 'list'
          question.choices = schema.enum
        } else if (schema.type === 'number') {
          question.type = 'number'
        } else {
          question.type = 'input'
        }

        questions.push(question)
      }
    }

    if(questions.length > 0) {
      console.log('Please provide the following...\n')
      context = await inquirer.prompt(questions, context) as object
    }

    return context
  }


  /**
   * Copies or renders the source to the destination parsing any templates encountered if desired
   *
   * @param source file to copy or parse and render
   * @param destination to save the source output to
   * @param errorIfExists if true will throw an error if the destination file already exists
   * @param context the values to be provided to the templates while rendering
   * @private
   */
  private async _renderFile(source: string, destination: string, errorIfExists: boolean, context?: object): Promise<void> {
    if(destination.includes('{{')){
      destination = await this.engine.parseAndRender(destination, context)
    }

    let overwritingFile: boolean = false
    // small optimization to only check existence if we care about it...
    if((errorIfExists || oclifSettings.debug) && fs.existsSync(destination)) {
      if (errorIfExists) {
        throw new Error(`The target file ${destination} already exists`)
      }
      overwritingFile = true
    }

    let readStream: NodeJS.ReadableStream

    if(destination.endsWith('.liquid')){
      destination = destination.substring(0, destination.length - 7)
      readStream = await this.engine.renderFileToNodeStream(source, context)
    }else{
      readStream = fs.createReadStream(source)
    }

    this.logDebug(`${overwritingFile ? 'Overwriting' : 'Writing'} File\n${source}\nto\n${destination}\n`)

    await makeDir(path.dirname(destination))

    let writeStream = fs.createWriteStream(destination)
    readStream.pipe(writeStream)
  }


  private async _renderDirectory(baseFrom: string, from: string, baseTo: string, context?: object): Promise<void> {
    let files: Dirent[] = await fsP.readdir(from, {withFileTypes: true})

    for(let file of files){

      let filePath: string = path.resolve(from, file.name)
      let to: string = filePath.replace(baseFrom, baseTo)

      if(file.isFile()){
        if(!this.shouldIgnore(file.name)) {

          // we do not need to check if file exists since entire directory was checked initially
          await this._renderFile(filePath, to, false, context)

        }else{
          this.logDebug(`Skipping File\n${filePath}\n`)
        }
      }else{
        await this._renderDirectory(baseFrom, filePath, baseTo, context)
      }
    }
  }

  private logDebug(message?: any, ...optionalParams: any[]): void{
    if(oclifSettings.debug) {
      console.debug(message, ...optionalParams)
    }
  }

  private shouldIgnore(fileName: string): boolean{
    const filesToSkip: string[] = ['.DS_Store', 'spawn.json']
    let ret: boolean = false
    if(filesToSkip.includes(fileName)){
      ret = true
    }
    return ret
  }

}

export const spawnEngine = new SpawnEngine(spawnResolver)
