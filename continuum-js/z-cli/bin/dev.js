#!/usr/bin/env tsx
// eslint-disable-next-line node/shebang
import {execute} from '@oclif/core'

await execute({development: true, dir: import.meta.url})
