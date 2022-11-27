import { defineUserConfig } from 'vuepress'
import type { DefaultThemeOptions } from 'vuepress'

export default defineUserConfig<DefaultThemeOptions>({
                                                         // site config
                                                         lang: 'en-US',
                                                         title: 'Continuum',
                                                         description: 'Continuum Insanely versatile IoT and Microservice framework',

                                                         // theme and its config
                                                         theme: '@vuepress/theme-default',
                                                         themeConfig: {
                                                             logo: '/images/logo.png',
                                                            // logoDark: '/images/logoDark.png',
                                                             repo: 'Kinotic-Foundation/continuum-framework',
                                                         },
                                                     })