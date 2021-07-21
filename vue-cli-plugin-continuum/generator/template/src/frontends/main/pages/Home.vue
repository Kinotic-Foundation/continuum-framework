<template>
    <v-container fluid fill-height>
        <v-row justify-center>
            <v-col>
                <v-card>
                    <div>
                        Some Content
                    </div>
                    <v-btn color="primary" text @click="submit">Submit</v-btn>
                </v-card>
            </v-col>
        </v-row>
    </v-container>
</template>

<script lang="ts">
    import { Component, Prop, Vue } from 'vue-property-decorator'

    @Component({
    components: { }
})
export default class Home extends Vue {

    @Prop({ default: '' })
    public msg!: string

    constructor() {
        super()
    }

    // Lifecycle hooks
    public mounted() {
    }

    public beforeDestroy() {
    }

}
</script>

<style scoped>

</style>
