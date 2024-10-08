// WARNING: Only import this into tests


import {ConsoleSpanExporter, SimpleSpanProcessor} from '@opentelemetry/sdk-trace-node'
import {
    ATTR_SERVICE_NAME
} from '@opentelemetry/semantic-conventions'
import { Resource } from '@opentelemetry/resources'


// import {
//     PeriodicExportingMetricReader,
//     ConsoleMetricExporter,
// } from '@opentelemetry/sdk-metrics'
// import { NodeSDK } from '@opentelemetry/sdk-node'
// import {
//     ATTR_SERVICE_NAME
// } from '@opentelemetry/semantic-conventions'
//
// export const otelSdk = new NodeSDK({
//                             resource: new Resource({
//                                                        [ATTR_SERVICE_NAME]: 'ContinuumTests'
//                                                    }),
//                             traceExporter: new ConsoleSpanExporter(),
//                             metricReader: new PeriodicExportingMetricReader({
//                                                                                 exporter: new ConsoleMetricExporter(),
//                                                                             }),
//                         })
//
// otelSdk.start()

import { NodeTracerProvider } from '@opentelemetry/sdk-trace-node'
export const otelTracerProvider = new NodeTracerProvider({
                                            resource: new Resource({
                                                                       [ATTR_SERVICE_NAME]: 'ContinuumTests'
                                                                   }),
                                        })

otelTracerProvider.addSpanProcessor(new SimpleSpanProcessor(new ConsoleSpanExporter()))
otelTracerProvider.register()
