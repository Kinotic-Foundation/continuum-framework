import { Continuum } from '../src'

describe('Continuum', () => {
    it('should connect', async () => {
        await Continuum.connect('ws://localhost:58503/v1', 'test', 'test')
    })



    it('should disconnect', async () => {
        await Continuum.disconnect()
    })
})
