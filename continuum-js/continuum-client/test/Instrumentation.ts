import { NodeSDK } from '@opentelemetry/sdk-node';
import { ConsoleSpanExporter } from '@opentelemetry/sdk-trace-node'
import {
    PeriodicExportingMetricReader,
    ConsoleMetricExporter,
} from '@opentelemetry/sdk-metrics'
import { Resource } from '@opentelemetry/resources'
import {
    ATTR_SERVICE_NAME,
    ATTR_SERVICE_VERSION,
} from '@opentelemetry/semantic-conventions'

const sdk = new NodeSDK({
                            resource: new Resource({
                                                       [ATTR_SERVICE_NAME]: 'ContinuumTests'
                                                   }),
                            traceExporter: new ConsoleSpanExporter(),
                            metricReader: new PeriodicExportingMetricReader({
                                                                                exporter: new ConsoleMetricExporter(),
                                                                            }),
                        });

sdk.start();
