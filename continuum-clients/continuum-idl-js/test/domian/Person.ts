import {C3Decorators} from "./C3Decorators";

enum AddressType {
    COMMERCIAL,
    RESIDENTIAL
}

export class Address {
    @C3Decorators.deprecatedProperty
    // @ts-ignore
    private streetNumber: number
    private streetName: string
    private cityName: string
    private zipCode: number
    private type: AddressType
}
export class Person {
    @C3Decorators.id
    @C3Decorators.notNull
    // @ts-ignore
    private id: string;
    private firstName: string;
    private lastName: string;
    @C3Decorators.notNull
    // @ts-ignore
    private address: Address;
    private age: number;
    private favoriteFoods: Array<string>;
}

