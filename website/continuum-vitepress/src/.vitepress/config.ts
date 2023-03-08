import { defineConfig } from 'vitepress'

// https://vitepress.vuejs.org/reference/site-config
export default defineConfig({
  title: "Continuum",
  description: "Insanely versatile IoT and Microservice framework",
  themeConfig: {
    logo: '/images/icon.png',
    // https://vitepress.vuejs.org/reference/default-theme-config
    nav: [
      { text: 'Guide', link: '/introduction/overview' },
      { text: 'Reference', link: '/reference/index' }
    ],

    sidebar: [
      {
        text: 'Introduction',
        items: [
          { text: 'What is Continuum?', link: '/introduction/overview' },
          { text: 'Getting Started', link: '/introduction/getting-started' }
        ]
      },
      {
        text: 'Guide',
        items: [
          { text: 'Command Line', link: '/guide/cli-overview' },
          { text: 'Services', link: '/guide/services' }
        ]
      }
    ],

    socialLinks: [
      { icon: 'github', link: ' https://github.com/Kinotic-Foundation/continuum-framework' }
    ],
    footer: {
      message: 'Released under the Apache License.',
      copyright: 'Copyright © 2018-present Kinotic Foundation'
    }
  }
})
