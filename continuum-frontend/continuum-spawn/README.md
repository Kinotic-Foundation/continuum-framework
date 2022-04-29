# Continuum Spawn
This is a safe templating library built on top of liquid js. Operators and expressions are parsed to AST and no eval or new Function are used.

Spawn allows entire directory structures to be templated allowing for rapid building and maintenance of starter / boilerplate projects.

## Local Development
- [Yalc](https://github.com/whitecolor/yalc) will need to be installed locally
- Then ```yalc publish```
- Then you can run ```yarn run watch``` to make sure updates and HMR work on dependent projects
- Then in project that depends on this one you can run ```yalc link continuum-spawn```

