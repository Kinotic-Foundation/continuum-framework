import { defineConfig } from 'vitepress'

// https://vitepress.vuejs.org/reference/site-config
export default defineConfig({
  title: "Continuum",
  description: "Insanely versatile IoT and Microservice framework",
  themeConfig: {
    logo: '/images/icon.png',
    // https://vitepress.vuejs.org/reference/default-theme-config

    nav: nav(),

    sidebar: {
      '/guide/': sidebarGuide(),
      '/reference/': sidebarReference()
    },

    socialLinks: [
      { icon: 'github', link: ' https://github.com/Kinotic-Foundation/continuum-framework' }
    ],
    footer: {
      message: 'Released under the Apache License.',
      copyright: 'Copyright © 2018-present Kinotic Foundation'
    }
  }
})

function nav() {
  return [
    { text: 'Guide', link: '/guide/overview', activeMatch: '/guide/' },
    {
      text: 'Reference',
      link: '/reference/continuum-config',
      activeMatch: '/reference/'
    }
  ]
}

function sidebarGuide() {
  return [
    {
      text: 'Introduction',
      items: [
        { text: 'What is Continuum?', link: '/guide/overview' },
        { text: 'Getting Started', link: '/guide/getting-started' }
      ]
    },
    {
      text: 'Details',
      items: [
        { text: 'Command Line', link: '/guide/cli-overview' },
        { text: 'Services', link: '/guide/services' }
      ]
    }
  ]
}

function sidebarReference() {
  return [
    {
      text: 'Reference',
      items: [
        { text: 'Continuum Config', link: '/reference/continuum-config' }
      ]
    },
    {
      text: 'API',
      items: [
        { text: 'Javadoc', link: '/reference/javadoc' }
      ]
    }
  ]
}
