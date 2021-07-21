import { RouteConfig } from 'vue-router'

const adminRoutes: RouteConfig[] = [
    {
        path: '/structures-admin',
        component: () => import(/* webpackChunkName: "frontendLayout" */'@/frontends/main/layouts/FrontendLayout.vue'),
        meta: {
            isFrontend: true,
            icon: 'fa-cubes',
            title: 'Admin'
        },
        children: [
            {
                path: '/structures-admin-home', component: () => import('@/frontends/main/pages/Index.vue'),
                meta: {
                    icon: 'fa-home', // font awesome icon
                    title: 'Home Page'
                }
            },
            {
                path: '/structures-admin-traits', component: () => import('@/frontends/structures-admin/pages/structures/traits/Traits.vue'),
                meta: {
                    icon: 'fa-tools',
                    title: 'Traits'
                }
            },
            {
                path: '/structures-admin', component: () => import('@/frontends/structures-admin/pages/structures/structures/Structures.vue'),
                meta: {
                    icon: 'fa-toolbox',
                    title: 'Structures'
                }
            },
            {
                path: '/structures-admin-items', component: () => import('@/frontends/structures-admin/pages/structures/items/StructureList.vue'),
                meta: {
                    icon: 'fas fa-boxes',
                    title: 'Items'
                }
            },
            {
                path: '/structure-items/:structureId', component: () => import('@/frontends/structures-admin/pages/structures/items/GenericItem.vue'), props: true,
                meta: {
                    noShow: true,
                    title: 'Items'
                }
            }
        ]
    }
]

export default adminRoutes
