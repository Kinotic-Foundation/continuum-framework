import {runCommand} from '@oclif/test'
import {expect} from 'chai'

describe('test', () => {
  it('runs test cmd', async () => {
    const {stdout} = await runCommand('test')
    expect(stdout).to.contain('hello world')
  })

  it('runs test --name oclif', async () => {
    const {stdout} = await runCommand('test --name oclif')
    expect(stdout).to.contain('hello oclif')
  })
})
