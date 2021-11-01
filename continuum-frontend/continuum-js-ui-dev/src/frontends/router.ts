import Vue from 'vue'
import Router, {RouterOptions} from 'vue-router'
import { IContinuumUI } from './continuum'
import mainRoutes from "@/frontends/main/routes";
import continuumRoutes from './continuum/routes'
import { container, cid } from 'inversify-props'

Vue.use(Router)

export const ConfiguredRouterOptions: RouterOptions = {
  routes: [
      { // This is where we have to configure the default route
            path: '/',
            redirect: '/continuum-home'
      },

      ...continuumRoutes, // provides 404 ect..

      ...mainRoutes,

      { // Not found must be at end
          path: '*',
          redirect: '/404'
      }
  ]

}

export const router = container.get<IContinuumUI>(cid.IContinuumUI).initialize(ConfiguredRouterOptions)
