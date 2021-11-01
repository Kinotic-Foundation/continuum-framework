<template>
  <router-view />
</template>

<script lang="ts">
  import Vue from 'vue'
  import { Component } from 'vue-property-decorator'
  import { inject } from 'inversify-props'
  import { IEventBus } from 'continuum-js'


  @Component({
    components: { }
  })
  export default class Main extends Vue {

    @inject()
    public eventBus!: IEventBus

    public beforeMount() {
    }


    public async mounted() {
      // FIXME: remove once auth page is created
      let hasQuery: number = window.location.hash.indexOf('?')
      let connected: boolean = false
      if(hasQuery !== -1){
        let query: string = window.location.hash.substr(hasQuery+1)
        let pairs: string[] = query.split('&')
        if(pairs.length > 0){
          for(let pair of pairs){
            if(pair.startsWith('host=')){
              connected=true
              let host: string = pair.split('=')[1]
              this.eventBus.connect('ws://'+host+':58503/v1', 'super', 'w3mak3th1sr0ck1nr0ll')
            }
          }
        }
      }
      if(!connected){
        //this.eventBus.connect('ws://192.168.0.5:58503/v1', 'super', 'w3mak3th1sr0ck1nr0ll')
        await this.eventBus.connect('ws://localhost:58503/v1', 'super', 'w3mak3th1sr0ck1nr0ll')
      }
    }

    public beforeDestroy() {
      this.eventBus.disconnect()
    }

  }

</script>
