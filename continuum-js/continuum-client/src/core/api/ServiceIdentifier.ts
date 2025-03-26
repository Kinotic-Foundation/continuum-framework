export class ServiceIdentifier {

    public namespace: string
    public name: string
    public scope?: string
    public version?: string


    constructor(namespace: string, name: string) {
        this.namespace = namespace
        this.name = name
    }
}
