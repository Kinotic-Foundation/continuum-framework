// Imports for configuring Vuetify
import Vue from 'vue'
import Vuetify from 'vuetify'
import vuetify from './vuetify' // <== important
import addons from '@storybook/addons';

// configure Vue to use Vuetify
Vue.use(Vuetify)

export const parameters = {
    actions: { argTypesRegex: "^on[A-Z].*" },
}

// get an instance to the communication channel for the manager and preview
const channel = addons.getChannel()
// switch body class for story along with interface theme
channel.on('DARK_MODE', isDark => {
    if (isDark) {
        vuetify.framework.theme.dark = true
    } else {
        vuetify.framework.theme.dark = false
    }
})

export const decorators = [
    (story, context) => {
        // wrap the passed component within the passed context
        const wrapped = story(context)
        // extend Vue to use Vuetify around the wrapped component
        return Vue.extend({
            vuetify,
            components: { wrapped },
            template: `
                <v-app>
                  <v-main>
                    <wrapped />
                  </v-main>
                </v-app>
              `
        })
    },
]