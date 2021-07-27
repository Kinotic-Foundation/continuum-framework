/*
 *
 * Copyright 2008-2021 Kinotic and the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kinotic.continuum.internal.core.api.service.invoker;

import com.kinotic.continuum.core.api.CRI;
import com.kinotic.continuum.core.api.Scheme;
import com.kinotic.continuum.core.api.event.Event;
import com.kinotic.continuum.core.api.event.EventBusService;
import com.kinotic.continuum.core.api.event.EventConstants;
import com.kinotic.continuum.core.api.event.ListenerStatus;
import com.kinotic.continuum.internal.utils.ContinuumUtil;
import com.kinotic.continuum.internal.util.EventUtils;
import io.vertx.core.Vertx;
import org.apache.commons.lang3.Validate;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.ReactiveAdapter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import reactor.core.Disposable;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Class handles invoking services that are published to the Continuum.
 *
 *
 * Created by Navid Mitchell on 2019-03-20.
 */
public class ServiceInvocationSupervisor {

    private static final Logger log = LoggerFactory.getLogger(ServiceInvocationSupervisor.class);

    private final AtomicBoolean active = new AtomicBoolean(false);

    private final CRI cri;
    private final ArgumentResolver argumentResolver;
    private final ReturnValueConverter returnValueConverter;
    private final ExceptionConverter exceptionConverter;
    private final EventBusService eventBusService;
    private final ReactiveAdapterRegistry reactiveAdapterRegistry;
    private final Vertx vertx;

    private Map<String, HandlerMethod> methodMap;

    private Disposable methodInvocationEventListenerDisposable;

    private ConcurrentHashMap<String, StreamSubscriber> activeStreamingResults = new ConcurrentHashMap<>();

    public ServiceInvocationSupervisor(CRI cri,
                                       Class<?> serviceInterface,
                                       Object instance,
                                       ArgumentResolver argumentResolver,
                                       ReturnValueConverter returnValueConverter,
                                       ExceptionConverter exceptionConverter,
                                       EventBusService eventBusService,
                                       ReactiveAdapterRegistry reactiveAdapterRegistry,
                                       Vertx vertx) {

        Validate.notNull(cri, "cri must not be null");
        Validate.notNull(serviceInterface, "serviceInterface must not be null");
        Validate.notNull(instance, "instance must not be null");
        Validate.notNull(argumentResolver, "argumentResolver must not be null");
        Validate.notNull(returnValueConverter, "returnValueConverter must not be null");
        Validate.notNull(exceptionConverter, "exceptionConverter must not be null");
        Validate.notNull(eventBusService, "eventBusService must not be null");
        Validate.notNull(reactiveAdapterRegistry, "reactiveAdapterRegistry must not be null");
        Validate.notNull(vertx, "vertx must not be null");

        this.cri = cri;
        this.argumentResolver = argumentResolver;
        this.returnValueConverter = returnValueConverter;
        this.exceptionConverter = exceptionConverter;
        this.eventBusService = eventBusService;
        this.reactiveAdapterRegistry = reactiveAdapterRegistry;
        this.vertx = vertx;

        this.methodMap = buildMethodMap(serviceInterface, instance);
    }

    public boolean isActive(){
        return active.get();
    }

    /**
     * Starts this {@link ServiceInvocationSupervisor}
     * @return a Mono that will succeed on Start and fail on an error
     */
    public Mono<Void> start(){
        return Mono.create(startSink -> {
            if(active.compareAndSet(false,true)){

                // begin listening on the event bus for Buffer's
                // use listenWithAck variant so remote sender will know that the data was received
                Mono<Flux<Event<byte[]>>> eventMono = eventBusService.listenWithAck(cri.baseResource());

                eventMono.subscribe(eventFlux -> {

                    methodInvocationEventListenerDisposable =
                        eventFlux.subscribe(this::processEvent, // will be called on new event to do some work
                                            // received error
                                            throwable -> log.error("Event listener error", throwable),
                                            // listener completed
                                            () -> {
                                                log.error("Should not happen! Event listener stopped for some reason!! Changing supervisor state to inactive");
                                                active.set(false);
                                            });

                    startSink.success();

                }, startSink::error);
            }else{
                startSink.error(new IllegalStateException("Service already started"));
            }
        });
    }

    /**
     * Stops this {@link ServiceInvocationSupervisor}
     * @return a Mono that will succeed on Stop and fail on an error
     */
    public Mono<Void> stop(){
        return Mono.create(stopSink -> {
            if (active.compareAndSet(true, false)) {
                if(methodInvocationEventListenerDisposable != null){
                    methodInvocationEventListenerDisposable.dispose();
                }

                for(Map.Entry<String, StreamSubscriber> streamSubscribers : activeStreamingResults.entrySet()){
                    streamSubscribers.getValue().cancel();
                }

                stopSink.success();
            }else{
                stopSink.error(new IllegalStateException("Service already stopped"));
            }
        });
    }

    private void processEvent(Event<byte[]> incomingEvent){
        if(log.isTraceEnabled()){
            log.trace("Service Invocation requested for "+incomingEvent.cri());
        }

        if(exceptionConverter.supports(incomingEvent)) {
            try {

                // Ensure all headers needed after processing are available
                Validate.notBlank(incomingEvent.cri().path(), "The methodId must not be blank");
                Assert.hasText(incomingEvent.metadata().get(EventConstants.REPLY_TO_HEADER), "A reply-to header must be provided");

                // Ensure there is an argument resolver that can handle the incoming data
                if (argumentResolver.supports(incomingEvent)) {

                        // Resolve arguments based on handler method and incoming data
                        HandlerMethod handlerMethod = methodMap.get(incomingEvent.cri().path());
                        Assert.notNull(handlerMethod,
                                       "No method could be resolved for methodId " + incomingEvent.cri().path());

                        if (!returnValueConverter.supports(incomingEvent,
                                                           handlerMethod.getReturnType().getParameterType())) {
                            throw new IllegalStateException("No compatible ReturnValueConverter found");
                        }

                        Object[] arguments = argumentResolver.resolveArguments(incomingEvent, handlerMethod);

                        // separate try catch since we do not want to log invocation errors
                        Object result = null;
                        boolean error = false;
                        try {
                            // Invoke the method and then handle the result
                            result = handlerMethod.invoke(arguments);
                        } catch (Exception e) {
                            error = true;
                            handleException(incomingEvent, e);
                        }

                        if (!error) {
                            processMethodInvocationResult(incomingEvent, handlerMethod, result);
                        }

                } else {
                    throw new IllegalStateException("No compatible ArgumentResolver found");
                }

            } catch (Exception e) {
                if(log.isDebugEnabled()){
                    log.debug("Exception occurred processing service request\n" + EventUtils.toString(incomingEvent, true), e);
                }
                handleException(incomingEvent, e);
            }
        }else{ // no exception converter found we will not execute message since we can not deal with an exception
            log.error("No exception converter found incoming message will be ignored");
        }
    }

    private void processMethodInvocationResult(Event<byte[]> incomingEvent, HandlerMethod handlerMethod, Object result){
        // Check if result is reactive if so we only complete once result is complete
        ReactiveAdapter reactiveAdapter = reactiveAdapterRegistry.getAdapter(null, result);
        if(reactiveAdapter == null){

            convertAndSend(incomingEvent, handlerMethod, result);

        }else{

            if(!reactiveAdapter.isMultiValue()){

                Mono<?> mono = Mono.from(reactiveAdapter.toPublisher(result));
                mono.doOnSuccess((Consumer<Object>) o -> convertAndSend(incomingEvent, handlerMethod, o))
                    .subscribe(v -> {}, t -> {
                        if(log.isDebugEnabled()){
                            log.debug("Exception occurred processing service request\n" + EventUtils.toString(incomingEvent, true), t);
                        }
                        handleException(incomingEvent, t);
                    }); // We use an empty consumer this is handled with doOnSuccess, this is done so we get a single "signal" instead of onNext, onComplete type logic..

            }else{

                // All long running results require a CORRELATION_ID_HEADER to be able to coordinate with the requester
                if(!incomingEvent.metadata().contains(EventConstants.CORRELATION_ID_HEADER)){
                    throw new IllegalArgumentException("Streaming results require a CORRELATION_ID_HEADER to be set");
                }

                String correlationId = incomingEvent.metadata().get(EventConstants.CORRELATION_ID_HEADER);
                activeStreamingResults.computeIfAbsent(correlationId, s -> {
                    //  FIXME: logic error here clients like the js client will stay alive during multiple requests even though previous request was invalidated indirectly
                    Flux<?> flux = Flux.from(reactiveAdapter.toPublisher(result));

                    CRI replyCRI = CRI.create(incomingEvent.metadata().get(EventConstants.REPLY_TO_HEADER));
                    Flux<ListenerStatus> replyListenerStatus = eventBusService.monitorListenerStatus(replyCRI.baseResource());

                    StreamSubscriber streamSubscriber = new StreamSubscriber(incomingEvent, handlerMethod, replyListenerStatus);
                    flux.subscribe(streamSubscriber);
                    return streamSubscriber;
                });
            }
        }
    }

    private void convertAndSend(Event<byte[]> incomingEvent, HandlerMethod handlerMethod, Object result) {
        Event<byte[]> resultEvent = returnValueConverter.convert(incomingEvent,
                                                                 handlerMethod.getReturnType()
                                                                              .getParameterType(),
                                                                 result);
        eventBusService.send(resultEvent);
    }

    private void sendCompletionEvent(Event<byte[]> incomingEvent){
        Event<byte[]> completionEvent = EventUtils.createReplyEvent(incomingEvent,
                                                                    Map.of(EventConstants.CONTROL_HEADER, EventConstants.CONTROL_VALUE_COMPLETE),
                                                                    null);
        eventBusService.send(completionEvent);
    }

    private Map<String, HandlerMethod> buildMethodMap(Class<?> serviceInterface, Object instance) {
        final HashMap<String, HandlerMethod> ret = new HashMap<>();

        ReflectionUtils.doWithMethods(serviceInterface, method -> {
            Method specificMethod = AopUtils.selectInvocableMethod(method, instance.getClass());
            String methodName = specificMethod.getName();
            if(ret.containsKey(methodName)){
                // in some cases such as with default methods we may actually get the same method multiple times check for that.
                if(!ret.get(methodName).getMethod().equals(specificMethod)){
                    log.warn(serviceInterface.getName() + " has overloaded method " + methodName + " overloading is not supported. \n "+specificMethod.toGenericString()+" will be ignored");
                }
            }else{
                HandlerMethod handlerMethod = new HandlerMethod(instance, specificMethod);
                ret.put(methodName,  handlerMethod);
            }
        }, ReflectionUtils.USER_DECLARED_METHODS);

        return ret;
    }

    private void handleException(Event<byte[]> event, Throwable e) {
        try {
            Event<byte[]> convertedEvent = exceptionConverter.convert(event, e);
            eventBusService.send(convertedEvent);
        } catch (Exception ex) {
            log.error("Error occurred when calling exception converter",e);
        }
    }

    /**
     * This subscriber will handle processing for any {@link org.reactivestreams.Publisher} returned by a method invocation
     * It may be acted on by the remote end by sending control requests to this supervisor
     */
    private class StreamSubscriber extends BaseSubscriber<Object> {

        private final Event<byte[]> incomingEvent;
        private final HandlerMethod handlerMethod;
        private final Flux<ListenerStatus> replyListenerStatus;
        private ReplyListenerStatusSubscriber replyListenerStatusSubscriber;
        private Disposable controlPlaneListener = null;

        public StreamSubscriber(Event<byte[]> incomingEvent,
                                HandlerMethod handlerMethod,
                                Flux<ListenerStatus> replyListenerStatus) {
            this.incomingEvent = incomingEvent;
            this.handlerMethod = handlerMethod;
            this.replyListenerStatus = replyListenerStatus;
        }

        @Override
        protected void hookOnSubscribe(Subscription subscription) {

            replyListenerStatusSubscriber = new ReplyListenerStatusSubscriber(this);
            replyListenerStatus.subscribe(replyListenerStatusSubscriber);

            String encodedSender = ContinuumUtil.safeEncodeURI(incomingEvent.metadata().get(EventConstants.SENDER_HEADER));
            String correlationId = incomingEvent.metadata().get(EventConstants.CORRELATION_ID_HEADER);

            controlPlaneListener = eventBusService.listen(CRI.create(Scheme.SERVICE, encodedSender, correlationId).raw())
                                                  .subscribe(this::processControlEvent,
                                                      throwable -> {
                                                          log.error("Control plane listener signaled an exception. Terminating Stream!", throwable);
                                                          this.cancel();
                                                      }, () ->{
                                                          log.error("Control plane listener signaled completion. Terminating Stream!");
                                                          this.cancel();
                                                      });
            super.hookOnSubscribe(subscription);
        }


        private void processControlEvent(Event<byte[]> incomingEvent){
            String control = incomingEvent.metadata().get(EventConstants.CONTROL_HEADER);
            if(log.isTraceEnabled()){
                log.trace("Processing control event "+control);
            }
            switch (control) {
                case EventConstants.CONTROL_VALUE_CANCEL:
                    this.cancel();
                    break;
                case EventConstants.CONTROL_VALUE_SUSPEND:
                    this.request(0);
                    break;
                case EventConstants.CONTROL_VALUE_RESUME:
                    this.requestUnbounded();
                    break;
                default:
                    log.error("Unknown control header value " + control);
            }
        }

        @Override
        protected void hookOnNext(Object value) {
            if(log.isTraceEnabled()){
                log.trace("Next stream value " + value);
            }
            convertAndSend(incomingEvent, handlerMethod, value);
        }

        @Override
        protected void hookOnComplete() {
            log.trace("Stream Complete");
            sendCompletionEvent(incomingEvent);
        }

        @Override
        protected void hookOnError(Throwable throwable) {
            if(log.isTraceEnabled()){
                log.trace("Stream Error",throwable);
            }
            handleException(incomingEvent, throwable);
        }

        @Override
        protected void hookFinally(SignalType type) {
            log.trace("Stream Cleanup Now");

            if(controlPlaneListener != null){
                controlPlaneListener.dispose();
            }

            replyListenerStatusSubscriber.cancel();

            String correlationId = incomingEvent.metadata().get(EventConstants.CORRELATION_ID_HEADER);
            // we must do this in a background thread since if the flux is created like Flux.just this will be executed in the same thread as the invocation
            // and hence inside the activeStreamingResults.computeIfAbsent block
            vertx.executeBlocking(p -> {
                activeStreamingResults.remove(correlationId);
                p.complete();
            }, null);
        }

    }

    /**
     * This subscriber handles monitoring the remote ends subscription for reply events.
     * If it detects that the remote ends subscription for reply events is removed it will terminate the {@link StreamSubscriber}
     */
    private static class ReplyListenerStatusSubscriber extends BaseSubscriber<ListenerStatus> {
        private final StreamSubscriber streamSubscription;

        public ReplyListenerStatusSubscriber(StreamSubscriber streamSubscription) {
            this.streamSubscription = streamSubscription;
        }

        @Override
        protected void hookOnNext(ListenerStatus status) {
            if(log.isTraceEnabled()){
                log.trace("Received ListenerStatus "+status);
            }
            // TODO: handle resume restart type logic
            if(status == ListenerStatus.INACTIVE){
                if(!streamSubscription.isDisposed()) {
                    log.trace("No more listeners active terminating streaming result.");
                    streamSubscription.cancel();
                    // ReplyListenerStatusSubscriber will be canceled by the streamSubscription
                }
            }
        }

        @Override
        protected void hookOnComplete() {
            // This condition should not occur under normal operation
            log.error("Reply Listener Monitor completed for some reason! Terminating streaming result.");
            streamSubscription.cancel();
        }

        @Override
        protected void hookOnError(Throwable throwable) {
            // This condition should not occur under normal operation
            log.error("Reply Listener Monitor threw an exception. Terminating streaming result.", throwable);
            streamSubscription.cancel();
        }
    }

}
