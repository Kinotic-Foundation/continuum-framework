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

    public async mounted() {
        await this.eventBus.connect('ws://localhost:58503/v1', 'super', 'w3mak3th1sr0ck1nr0ll')
    }

    public beforeDestroy() {
      this.eventBus.disconnect()
    }

  }

</script>
