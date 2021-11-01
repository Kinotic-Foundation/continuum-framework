import Glitch from '@/frontends/continuum/components/Glitch.vue';

export default {
    title: 'Components/Glitch',
    component: Glitch,
};

//👇 We create a “template” of how args map to rendering
const Template = (args, { argTypes }) => ({
    components: { Glitch },
    props: Object.keys(argTypes),
    template: '<glitch v-bind="$props" v-on="$props" />',
});


// try one with overridden props
export const Message = Template.bind({})
Message.args = { message: 'We Like Stonks!' }

Message.parameters = {
      docs: {
        description: {
              story: 'The `message` prop value is the text that will be displayed'
            }
      }
};