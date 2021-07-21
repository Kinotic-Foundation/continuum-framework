<template>
    <v-container fluid fill-height>
        <v-layout justify-center>
            <v-flex text-xs-center mr-3 >

                <v-data-table
                        :height="computedHeight"
                        :max-height="computedHeight"
                        :headers="headers"
                        :items="items"
                        :server-items-length="options.totalItems"
                        :options.sync="options"
                        :loading="loading"
                        dense
                        item-key="structure.id"
                        loading-text="Loading... Please wait"
                        class="elevation-1"
                        @page-count="pageCount = $event"
                        @click:row="selectStructure"
                        :fixed-header=true
                        :footer-props="{
                          showFirstLastPage: true,
                          firstIcon: 'mdi-arrow-collapse-left',
                          lastIcon: 'mdi-arrow-collapse-right',
                          prevIcon: 'mdi-minus',
                          nextIcon: 'mdi-plus',
                          'items-per-page-options':options.rowsPerPageItems
                        }" >

                    <template v-slot:item.id="{ item }">
                        {{ item.structure.id }}
                    </template>
                    <template v-slot:item.description="{ item }">
                        {{ item.structure.description }}
                    </template>
                    <template v-slot:item.created="{ item }">
                        {{ formatDate(item.structure.created) }}
                    </template>
                    <template v-slot:item.updated="{ item }">
                        {{ formatDate(item.structure.updated) }}
                    </template>
                    <template v-slot:item.publishedTimestamp="{ item }">
                        {{ formatDate(item.structure.publishedTimestamp) }}
                    </template>

                    <template v-slot:no-data >
                        <div class="py-12" v-if="search.length === 0" >
                            <v-btn color="primary" @click="getAll" v-show="!loading">No Data - Push To Search Again</v-btn>
                        </div>
                    </template>

                    <template v-slot:top>
                        <v-toolbar flat color="white" >
                            <v-toolbar-title>Structure List Selection</v-toolbar-title>
                            <v-divider
                                    class="mx-4"
                                    inset
                                    vertical>
                            </v-divider>
                            <v-alert dense
                                     outlined
                                     type="error"
                                     style="margin: 0 auto; text-align: center"
                                     v-show="serverErrors.length > 0">
                                {{ serverErrors }}
                            </v-alert>
                            <v-spacer></v-spacer>
                            <v-text-field autofocus
                                          clearable
                                          dense
                                          hide-details
                                          single-line
                                          v-model="search"
                                          label="Name Search"
                                          @keydown.space.prevent>
                            </v-text-field>
                        </v-toolbar>
                    </template>
                </v-data-table>

            </v-flex>
        </v-layout>
    </v-container>

</template>

<script lang="ts">
    import { Component, Vue, Watch } from 'vue-property-decorator'
    import {IItemManager, IStructureManager} from "@/frontends/structures-admin/services"
    import draggable from 'vuedraggable'
    import {StructureHolder} from "@/frontends/structures-admin/pages/structures/structures/StructureHolder"
    import { inject } from 'inversify-props'

    @Component({
        components: { draggable },
        props: { }
    })
    export default class Traits extends Vue {

        @inject()
        private structureManager!: IStructureManager
        @inject()
        private itemManager!: IItemManager

        private computedHeight: number = (window.innerHeight - 225)

        public items: StructureHolder[] = []
        public loading: boolean = true
        public finishedInitialLoad: boolean = false

        public serverErrors: string = ""

        public search: string = ""
        public searchTimeoutHandle: any = {}
        public searchTimeoutInterval: number = 1000
        public searchTimeoutStartTime: number = 0

        public options: any = {
            mustSort: true,
            sortDesc: [true],
            page: 1,
            totalItems: 0,
            itemsPerPage: 10,
            sortBy: ["id"],
            rowsPerPageItems: [5,10,25,50,75,100,-1]
        }

        public headers: any = [
            { text: 'Id',align: 'left',value: 'id'},
            { text: 'Description', value: 'description', sortable: false },
            { text: 'Created', value: 'created' },
            { text: 'Last Updated', value: 'updated' },
            { text: 'Published On', value: 'publishedTimestamp' },
        ]

        constructor() {
            super()
        }


        // Lifecycle hook
        public mounted() {
            this.getAll()
        }

        public beforeDestroy() {
        }

        @Watch('options')
        public watchPagination(value: any, oldValue: any){
            if(value.sortBy.length === 0){
                value.sortBy = ["id"]
                value.sortDesc = [true]
            }
            this.options = value
            if(this.finishedInitialLoad && (this.search !== null && this.search.length !== 0)){
                this.getAll()
            }
        }

        @Watch('search')
        public watchSearch(value: any, oldValue: any){
            if(oldValue.length >= 1 && (value === null || value.length === 0)){
                // new value is cleared and we had an old value
                this.getAll()
            }else if(value.length >= 2){
                this.serverErrors = ""
                // start to search when we have a couple characters.
                if(this.searchTimeoutStartTime === 0){
                    this.searchTimeoutStartTime = new Date().getTime()
                }else if((new Date().getTime() - this.searchTimeoutStartTime) < this.searchTimeoutInterval){
                    clearTimeout(this.searchTimeoutHandle)
                }

                this.searchTimeoutHandle = setTimeout(() => {
                    this.loading = true
                    this.structureManager.getAllPublishedAndIdLike(this.search.trim()+"*", this.options.itemsPerPage, this.options.page-1, this.options.sortBy[0], this.options.sortDesc[0]).then((returnedItems: any) => {
                        this.loading = false
                        this.options.totalItems = returnedItems.totalElements
                        this.items.length = 0 // reset the list
                        this.items = returnedItems.content
                    })
                    .catch((error: any) => {
                        this.loading = false
                        console.log(error.stack)
                        this.serverErrors = error.message
                    })
                }, this.searchTimeoutInterval)
            }
        }

        public formatDate(timeInMills: number){
            let ret: string = ""
            if(timeInMills !== 0){
                let [date, time] = new Date(timeInMills).toLocaleString('en-US', {hour12: false}).split(', ')
                ret = date + " " + time
            }
            return ret
        }

        public selectStructure(structureHolder: StructureHolder){
            this.$router.push({ path: `/structure-items/${structureHolder.structure.id}` })
        }

        public getAll() {
            this.loading = true
            this.serverErrors = ""
            this.structureManager.getAllPublished(this.options.itemsPerPage, this.options.page-1, this.options.sortBy[0], this.options.sortDesc[0]).then((returnedItems: any) => {
                this.loading = false
                this.options.totalItems = returnedItems.totalElements
                this.items.length = 0 // reset the list
                this.items = returnedItems.content

                if(!this.finishedInitialLoad){
                    setTimeout(() => {
                        this.finishedInitialLoad = true
                    }, 500)
                }

            })
            .catch((error: any) => {
                this.loading = false
                console.log(error.stack)
                this.serverErrors = error.message

                if(!this.finishedInitialLoad){
                    setTimeout(() => {
                        this.finishedInitialLoad = true
                    }, 500)
                }

            })
        }
    }
</script>

<style scoped>


</style>
