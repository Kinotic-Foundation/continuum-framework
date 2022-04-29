import 'reflect-metadata'
import Vue from 'vue'
import vuetify from './plugins/vuetify'
import './registerServiceWorker'
import './frontends/continuum'
import {router} from '@/frontends/router'

// Make sure services get autowired early during App entry!
import '@/frontends/services'
import '@/frontends/states'

// now load app specific entry points
import Main from '@/Main.vue'

// Vue.config.productionTip = false

new Vue({
  router,
  vuetify,
  render: (h) => h(Main)
}).$mount('#app')
