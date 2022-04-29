module.exports = (api, options, rootOptions) => {
    api.extendPackage({
        dependencies: {
            '@mdi/js': '^5.9.55',
            '@vue/composition-api': '^1.0.0-rc.6',
            'continuum-js': 'link:.yalc/continuum-js',
            'inversify': '5.0.5',
            'inversify-props': '1.4.9',
            'reflect-metadata': '^0.1.13',
            'roboto-fontface': '*',
            'vue-class-component': '^7.2.5',
            'vue-property-decorator': '^9.1.2',
            'vuetify': '^2.6.1'
        },
        devDependencies: {
            '@fortawesome/fontawesome-free': '^5.12.1',
            'sass': '^1.32.0',
            'sass-loader': '^10.0.3',
            'vue-template-compiler': '^2.6.11',
            'vuetify-loader': '^1.7.0'
        },
        workspaces: [
            "src/frontends/*"
        ]
    });

    api.render('./template', {
        ...options,
    });
}