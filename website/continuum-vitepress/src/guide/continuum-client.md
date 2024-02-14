---
outline: deep
---

# Continuum Client
The Continuum client allows you to connect to a Continuum cluster and interact with the microservices. 
There are multiple flavors of the client depending on the language you are using.



## Continuum Client for Javascript

Usage in Node Js:

```javascript

// this only needs to be done once in your application
import { Continuum } from '@kinotic/continuum-client'
import { WebSocket } from 'ws'

Object.assign(global, { WebSocket})

Continuum.connect('ws://127.0.0.1:58503/v1', 'admin', 'structures')


```
