import { action } from '@storybook/addon-actions'
import Confirm from '@/frontends/continuum/components/Confirm.vue'

export default {
    title: 'Components/Confirm',
    component: Confirm,
};

export const DeleteDialog = (args, { argTypes }) => ({
    components: { Confirm },
    template: `
              <v-container fluid>
                <v-btn @click="openDialog">Delete Now</v-btn>
                <confirm ref="confirm"></confirm>
              </v-container>`,

    props: Object.keys(argTypes),

    methods: {
        openDialog: function (event) {
            this.$refs.confirm.open('Delete', 'Are you sure?', { color: 'red' }).then((confirm) => {
                action(confirm ? 'deleteConfirmed' : 'deleteCanceled')()
            })
        }
    }
});
