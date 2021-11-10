import {IServiceProxy, IServiceRegistry} from 'continuum-js'
import { Trait } from './Trait'
import { inject, injectable, container } from 'inversify-props'

/**
 * Service for testing of continuum proxy logic
 */
export interface ITraitManager {

    save(saveTrait: Trait): Promise<Trait>
    getTraitById(id: string): Promise<Trait>
    getTraitByName(name: string): Promise<Trait>
    getAll(numberPerPage: number, page: number, columnToSortBy: string, descending: boolean): Promise<any>
    getAllNameLike(nameLike: string, numberPerPage: number, page: number, columnToSortBy: string, descending: boolean): Promise<any>
    delete(traitId: string): Promise<void>

}

@injectable()
class TraitManager implements ITraitManager {

    private serviceProxy: IServiceProxy

    constructor(@inject() serviceRegistry: IServiceRegistry) {
        this.serviceProxy = serviceRegistry.serviceProxy('com.mthinx.inventorymanager.traits.ITraitManager')
    }

    public save(saveTrait: Trait): Promise<Trait> {
        return this.serviceProxy.invoke('save', [saveTrait])
    }

    public getTraitById(id: string): Promise<Trait> {
        return this.serviceProxy.invoke('getTraitById', [id])
    }

    public getTraitByName(name: string): Promise<Trait> {
        return this.serviceProxy.invoke('getTraitByName', [name])
    }

    public getAll(numberPerPage: number, page: number, columnToSortBy: string, descending: boolean): Promise<any> {
        return this.serviceProxy.invoke('getAll', [numberPerPage, page, columnToSortBy, descending])
    }

    public getAllNameLike(nameLike: string, numberPerPage: number, page: number, columnToSortBy: string, descending: boolean): Promise<any> {
        return this.serviceProxy.invoke('getAllNameLike', [nameLike, numberPerPage, page, columnToSortBy, descending])
    }

    public delete(traitId: string): Promise<void> {
        return this.serviceProxy.invoke('delete', [traitId])
    }

}

container.addSingleton<ITraitManager>(TraitManager)
