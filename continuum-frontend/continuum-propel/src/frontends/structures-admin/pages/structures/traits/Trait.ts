
export class Trait {

    public id: string
    public name: string
    public describeTrait: string
    public schema: string
    public esSchema: string

    public created: number
    public updated: number

    public required: boolean // should the GUI require a field to be filled out when looking at the item
    public includeInLabel: boolean
    public includeInQRCode: boolean
    public modifiable: boolean // should this field be modifiable outside the system
    public unique: boolean // should be a unique field in the index, so no others should exist
    public operational: boolean // field that says we do not really add to the schema but provide some type of process

    constructor(id: string,
                name: string,
                describeTrait: string,
                schema: string,
                esSchema: string,
                created: number,
                updated: number,
                required: boolean,
                includeInLabel: boolean,
                includeInQRCode: boolean,
                modifiable: boolean,
                unique: boolean,
                operational: boolean) {
        this.id = id
        this.name = name
        this.describeTrait = describeTrait
        this.schema = schema
        this.esSchema = esSchema
        this.created = created
        this.updated = updated
        this.required = required
        this.includeInLabel = includeInLabel
        this.includeInQRCode = includeInQRCode
        this.modifiable = modifiable
        this.unique = unique
        this.operational = operational
    }

}
