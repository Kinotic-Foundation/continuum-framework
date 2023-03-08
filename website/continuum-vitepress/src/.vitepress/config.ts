import { defineConfig } from 'vitepress'

// https://vitepress.vuejs.org/reference/site-config
export default defineConfig({
  title: "Continuum",
  description: "Insanely versatile IoT and Microservice framework",
  themeConfig: {
    logo: '/logo.png',
    // https://vitepress.vuejs.org/reference/default-theme-config
    nav: [
      { text: 'Guide', link: '/introduction/overview' },
      { text: 'Reference', link: '/reference/index' }
    ],

    sidebar: [
      {
        text: 'Introduction',
        items: [
          { text: 'What is Continuum', link: '/introduction/overview' },
          { text: 'Getting Started', link: '/introduction/getting-started' }
        ]
      }
    ],

    socialLinks: [
      { icon: 'github', link: ' https://github.com/Kinotic-Foundation/continuum-framework' }
    ]
  }
})
