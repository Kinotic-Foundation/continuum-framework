import { RouteConfig } from 'vue-router'

const mainRoutes: RouteConfig[] = [
    {
        path: '/continuum',
        meta: {
            isFrontend: true,
            icon: 'fa-terminal',
            title: 'Continuum'
        },
        component: () => import(/* webpackChunkName: "frontendLayout" */'@/frontends/continuum/layouts/FrontendLayout.vue'),
        children: [
            {
                path: '/continuum-home', component: () => import('@/frontends/main/pages/Home.vue'),
                meta: {
                    icon: 'fa-home',
                    title: 'Home'
                }
            }
        ]
    }
]

export default mainRoutes
