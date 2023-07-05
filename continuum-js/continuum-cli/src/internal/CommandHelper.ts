import inquirer from "inquirer"
import {execa} from "execa"

/**
 * Creates a new front end project using either React of Vue depending on the user's choice
 */
export async function createFrontEnd(name: string){
  const {framework} = await inquirer.prompt([
    {
      type: 'list',
      name: 'framework',
      message: 'Which framework would you like to use?',
      choices: ['React', 'Vue']
    }
  ])
  if(framework === 'React'){
    await createReact(name)
  }else{
    await createVue(name)
  }
}

/**
 * Creates a new React project using create-react-app
 */
async function createReact(name: string){
  await execa('npx', ['create-react-app', name], { stdio: 'inherit' })
}

/**
 * Creates a new Vue project using vue-cli with npx
 */
async function createVue(name: string){
  await execa('npx', ['@vue/cli', 'create', name], { stdio: 'inherit' })
}
