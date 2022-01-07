'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var inversifyProps = require('inversify-props');
var rxjs = require('rxjs');
var operators = require('rxjs/operators');
var rxStomp = require('@stomp/rx-stomp');
var typescriptOptional = require('typescript-optional');
var angular2Uuid = require('angular2-uuid');

/*! *****************************************************************************
Copyright (c) Microsoft Corporation.

Permission to use, copy, modify, and/or distribute this software for any
purpose with or without fee is hereby granted.

THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH
REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY
AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM
LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
PERFORMANCE OF THIS SOFTWARE.
***************************************************************************** */

function __decorate(decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
}

function __param(paramIndex, decorator) {
    return function (target, key) { decorator(target, key, paramIndex); }
}

function __metadata(metadataKey, metadataValue) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(metadataKey, metadataValue);
}

function __values(o) {
    var s = typeof Symbol === "function" && Symbol.iterator, m = s && o[s], i = 0;
    if (m) return m.call(o);
    if (o && typeof o.length === "number") return {
        next: function () {
            if (o && i >= o.length) o = void 0;
            return { value: o && o[i++], done: !o };
        }
    };
    throw new TypeError(s ? "Object is not iterable." : "Symbol.iterator is not defined.");
}

function __read(o, n) {
    var m = typeof Symbol === "function" && o[Symbol.iterator];
    if (!m) return o;
    var i = m.call(o), r, ar = [], e;
    try {
        while ((n === void 0 || n-- > 0) && !(r = i.next()).done) ar.push(r.value);
    }
    catch (error) { e = { error: error }; }
    finally {
        try {
            if (r && !r.done && (m = i["return"])) m.call(i);
        }
        finally { if (e) throw e.error; }
    }
    return ar;
}

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
var CrudServiceProxy = /** @class */ (function () {
    function CrudServiceProxy(serviceProxy) {
        this.serviceProxy = serviceProxy;
    }
    CrudServiceProxy.prototype.count = function () {
        return this.serviceProxy.invoke('count');
    };
    CrudServiceProxy.prototype.create = function (entity) {
        return this.serviceProxy.invoke('create', [entity]);
    };
    CrudServiceProxy.prototype.deleteByIdentity = function (identity) {
        return this.serviceProxy.invoke('deleteByIdentity', [identity]);
    };
    CrudServiceProxy.prototype.findAll = function (pageable) {
        return this.serviceProxy.invoke('findAll', [pageable]);
    };
    CrudServiceProxy.prototype.findByIdentity = function (identity) {
        return this.serviceProxy.invoke('findByIdentity', [identity]);
    };
    CrudServiceProxy.prototype.save = function (entity) {
        return this.serviceProxy.invoke('save', [entity]);
    };
    CrudServiceProxy.prototype.findByIdNotIn = function (ids, page) {
        return this.serviceProxy.invoke('findByIdNotIn', [ids, page]);
    };
    CrudServiceProxy.prototype.search = function (searchText, pageable) {
        return this.serviceProxy.invoke('search', [searchText, pageable]);
    };
    CrudServiceProxy = __decorate([
        inversifyProps.injectable(),
        __metadata("design:paramtypes", [Object])
    ], CrudServiceProxy);
    return CrudServiceProxy;
}());

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
var SearchServiceProxy = /** @class */ (function () {
    function SearchServiceProxy(serviceProxy) {
        this.serviceProxy = serviceProxy;
    }
    SearchServiceProxy.prototype.findAll = function (pageable) {
        return this.serviceProxy.invoke('findAll', [pageable]);
    };
    SearchServiceProxy.prototype.search = function (searchText, pageable) {
        return this.serviceProxy.invoke('search', [searchText, pageable]);
    };
    SearchServiceProxy = __decorate([
        inversifyProps.injectable(),
        __metadata("design:paramtypes", [Object])
    ], SearchServiceProxy);
    return SearchServiceProxy;
}());

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
var DataSourceUtils = /** @class */ (function () {
    function DataSourceUtils() {
    }
    DataSourceUtils.instanceOfEditableDataSource = function (datasource) {
        return 'create' in datasource;
    };
    return DataSourceUtils;
}());

/*
 * Copyright 2008-2019 the original author or authors.
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
/**
 * Abstract interface for pagination information.
 *
 * Adapted from the Spring Data Commons Package
 *
 * @author Oliver Gierke
 * @author Navid Mitchell
 */
var Pageable = /** @class */ (function () {
    function Pageable() {
        /**
         * Returns the page to be returned.
         */
        this.pageNumber = 0;
        /**
         * Returns the number of items to be returned.
         */
        this.pageSize = 0;
        /**
         * Returns the sorting parameters.
         */
        this.sort = null;
    }
    return Pageable;
}());

/*
 * Copyright 2008-2019 the original author or authors.
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
/**
 * A page is a sublist of a list of objects. It allows gain information about the position of it in the containing
 * entire list.
 *
 * Adapted from the Spring Data Commons Package
 *
 * @param <T>
 * @author Oliver Gierke
 * @author Navid Mitchell
 */
var Page = /** @class */ (function () {
    function Page() {
        /**
         * Returns the size of the {@link Page}.
         */
        this.size = 0;
        /**
         * Returns the total amount of elements.
         */
        this.totalElements = 0;
        /**
         * Returns the page content as {@link Array}.
         */
        this.content = [];
    }
    return Page;
}());

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
(function (SearchComparator) {
    SearchComparator["EQUALS"] = "=";
    SearchComparator["NOT"] = "!";
    SearchComparator["GREATER_THAN"] = ">";
    SearchComparator["GREATER_THAN_OR_EQUALS"] = ">=";
    SearchComparator["LESS_THAN"] = "<";
    SearchComparator["LESS_THAN_OR_EQUALS"] = "<=";
    SearchComparator["LIKE"] = "~";
})(exports.SearchComparator || (exports.SearchComparator = {}));
var SearchCriteria = /** @class */ (function () {
    function SearchCriteria(key, value, searchComparator) {
        this.key = key;
        this.value = value;
        this.searchComparator = searchComparator;
    }
    return SearchCriteria;
}());

/*
 * Copyright 2008-2019 the original author or authors.
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
(function (Direction) {
    Direction["ASC"] = "ASC";
    Direction["DESC"] = "DESC";
})(exports.Direction || (exports.Direction = {}));
(function (NullHandling) {
    /**
     * Lets the data store decide what to do with nulls.
     */
    NullHandling["NATIVE"] = "NATIVE";
    /**
     * A hint to the used data store to order entries with null values before non null entries.
     */
    NullHandling["NULLS_FIRST"] = "NULLS_FIRST";
    /**
     * A hint to the used data store to order entries with null values after non null entries.
     */
    NullHandling["NULLS_LAST"] = "NULLS_LAST";
})(exports.NullHandling || (exports.NullHandling = {}));
var Order = /** @class */ (function () {
    function Order(property, direction) {
        this.direction = exports.Direction.ASC;
        this.nullHandling = exports.NullHandling.NATIVE;
        this.property = property;
        if (direction !== null) {
            this.direction = direction;
        }
    }
    /**
     * Returns whether sorting for this property shall be ascending.
     */
    Order.prototype.isAscending = function () {
        return this.direction === exports.Direction.ASC;
    };
    /**
     * Returns whether sorting for this property shall be descending.
     */
    Order.prototype.isDescending = function () {
        return this.direction === exports.Direction.DESC;
    };
    return Order;
}());
/**
 * Sort option for queries. You have to provide at least a list of properties to sort for that must not include
 * {@literal null} or empty strings. The direction defaults to {@link Sort#DEFAULT_DIRECTION}.
 *
 * Adapted from the Spring Data Commons Package
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 * @author Mark Paluch
 * @author Navid Mitchell
 */
var Sort = /** @class */ (function () {
    function Sort() {
        this.orders = [];
    }
    return Sort;
}());

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
(function (EventConstants) {
    EventConstants["CONTENT_TYPE_HEADER"] = "content-type";
    EventConstants["CONTENT_LENGTH_HEADER"] = "content-length";
    EventConstants["REPLY_TO_HEADER"] = "reply-to";
    /**
     * Header provided by the sever on connection to represent the users session id
     */
    EventConstants["SESSION_HEADER"] = "session";
    /**
     * Header provided by the sever on connection to represent the servers session key
     */
    EventConstants["SESSION_KEY_HEADER"] = "sessionKey";
    /**
     * Correlates a response with a given request
     * Headers that start with __ will always be persisted between messages
     */
    EventConstants["CORRELATION_ID_HEADER"] = "__correlation-id";
    /**
     * Denotes that something caused an error. Will contain a brief message about the error
     */
    EventConstants["ERROR_HEADER"] = "error";
    /**
     * Denotes the completion of an event stream. The value typically will contain the reason for completion.
     */
    EventConstants["COMPLETE_HEADER"] = "complete";
    /**
     * Denotes the event is a control plane event. These are used for internal coordination.
     */
    EventConstants["CONTROL_HEADER"] = "control";
    /**
     * Stream is complete no further values will be sent.
     */
    EventConstants["CONTROL_VALUE_COMPLETE"] = "complete";
    EventConstants["CONTROL_VALUE_CANCEL"] = "cancel";
    EventConstants["CONTROL_VALUE_SUSPEND"] = "suspend";
    EventConstants["CONTROL_VALUE_RESUME"] = "resume";
    EventConstants["SERVICE_DESTINATION_PREFIX"] = "srv://";
    EventConstants["STREAM_DESTINATION_PREFIX"] = "stream://";
    EventConstants["CONTENT_JSON"] = "application/json";
    EventConstants["CONTENT_TEXT"] = "text/plain";
})(exports.EventConstants || (exports.EventConstants = {}));

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
/**
 * Default IEvent implementation
 */
var Event = /** @class */ (function () {
    function Event(cri, headers, data) {
        this.cri = cri;
        if (headers !== undefined) {
            this.headers = headers;
        }
        else {
            this.headers = new Map();
        }
        this.data = typescriptOptional.Optional.ofNullable(data);
    }
    Event.prototype.getHeader = function (key) {
        return this.headers.get(key);
    };
    Event.prototype.hasHeader = function (key) {
        return this.headers.has(key);
    };
    Event.prototype.setHeader = function (key, value) {
        this.headers.set(key, value);
    };
    Event.prototype.removeHeader = function (key) {
        return this.headers.delete(key);
    };
    Event.prototype.setDataString = function (data) {
        var uint8Array = new TextEncoder().encode(data);
        this.data = typescriptOptional.Optional.ofNonNull(uint8Array);
    };
    Event.prototype.getDataString = function () {
        var ret = '';
        this.data.ifPresent(function (value) { return ret = new TextDecoder().decode(value); });
        return ret;
    };
    return Event;
}());
var EventBus = /** @class */ (function () {
    function EventBus() {
        this.connected = false;
        this.encodedIdentity = null;
        this.replyToCri = null;
        this.requestRepliesObservable = null;
        this.requestRepliesSubscription = null;
        this.stompClient = new rxStomp.RxStomp();
    }
    EventBus.prototype.connect = function (url, identity, secret) {
        var _this = this;
        return new Promise(function (resolve, reject) {
            if (!_this.connected) {
                _this.encodedIdentity = encodeURIComponent(identity);
                _this.replyToCri = exports.EventConstants.SERVICE_DESTINATION_PREFIX + _this.encodedIdentity + ':' + angular2Uuid.UUID.UUID() + '@continuum.js.EventBus/replyHandler';
                _this.connected = true;
                var connectHeaders_1 = {
                    login: identity,
                    passcode: secret
                };
                _this.stompClient.configure({
                    brokerURL: url,
                    connectHeaders: connectHeaders_1,
                    heartbeatIncoming: 120000,
                    heartbeatOutgoing: 30000,
                    reconnectDelay: 30000
                });
                var errorSubscription_1 = _this.stompClient.stompErrors$.subscribe(function (value) {
                    errorSubscription_1.unsubscribe();
                    var message = value.headers[exports.EventConstants.ERROR_HEADER];
                    _this.connected = false;
                    reject(message);
                });
                var connectedSubscription_1 = _this.stompClient.connected$.subscribe(function () {
                    connectedSubscription_1.unsubscribe();
                    resolve();
                });
                _this.stompClient.serverHeaders$.subscribe(function (value) {
                    var session = value[exports.EventConstants.SESSION_HEADER];
                    if (session != null) {
                        delete connectHeaders_1.login;
                        delete connectHeaders_1.passcode;
                        connectHeaders_1.session = session;
                    }
                });
                _this.stompClient.activate();
            }
            else {
                reject('Stomp connection already active');
            }
        });
    };
    EventBus.prototype.disconnect = function () {
        if (this.connected) {
            this.connected = false;
            if (this.requestRepliesObservable != null) {
                if (this.requestRepliesSubscription != null) {
                    this.requestRepliesSubscription.unsubscribe();
                    this.requestRepliesSubscription = null;
                }
                this.requestRepliesObservable = null;
            }
            this.stompClient.deactivate();
        }
    };
    EventBus.prototype.send = function (event) {
        var e_1, _a;
        var headers = {};
        try {
            for (var _b = __values(event.headers.entries()), _c = _b.next(); !_c.done; _c = _b.next()) {
                var _d = __read(_c.value, 2), key = _d[0], value = _d[1];
                headers[key] = value;
            }
        }
        catch (e_1_1) { e_1 = { error: e_1_1 }; }
        finally {
            try {
                if (_c && !_c.done && (_a = _b.return)) _a.call(_b);
            }
            finally { if (e_1) throw e_1.error; }
        }
        // send data over stomp
        this.stompClient.publish({
            destination: event.cri,
            headers: headers,
            binaryBody: event.data.orUndefined()
        });
    };
    EventBus.prototype.request = function (event) {
        return this.requestStream(event, false).pipe(operators.first()).toPromise();
    };
    EventBus.prototype.requestStream = function (event, sendControlEvents) {
        var _this = this;
        if (sendControlEvents === void 0) { sendControlEvents = true; }
        if (!this.connected) {
            return rxjs.throwError(new Error('You must call connect on the event bus before sending any request'));
        }
        else {
            return new rxjs.Observable(function (subscriber) {
                if (_this.requestRepliesObservable == null) {
                    _this.requestRepliesObservable = _this._observe(_this.replyToCri).pipe(operators.multicast(new rxjs.Subject()));
                    _this.requestRepliesSubscription = _this.requestRepliesObservable.connect();
                }
                var serverSignaledCompletion = false;
                var correlationId = angular2Uuid.UUID.UUID();
                var defaultMessagesSubscription = _this.requestRepliesObservable
                    .pipe(operators.filter(function (value, index) {
                    return value.headers.get(exports.EventConstants.CORRELATION_ID_HEADER) === correlationId;
                })).subscribe({
                    next: function (value) {
                        if (value.hasHeader(exports.EventConstants.CONTROL_HEADER)) {
                            if (value.headers.get(exports.EventConstants.CONTROL_HEADER) === 'complete') {
                                serverSignaledCompletion = true;
                                subscriber.complete();
                            }
                            else {
                                throw new Error('Control Header ' + value.headers.get(exports.EventConstants.CONTROL_HEADER) + ' is not supported');
                            }
                        }
                        else if (value.hasHeader(exports.EventConstants.ERROR_HEADER)) {
                            // TODO: add custom error type that contains error detail as well if provided by server, this would be the event body
                            serverSignaledCompletion = true;
                            subscriber.error(new Error(value.getHeader(exports.EventConstants.ERROR_HEADER)));
                        }
                        else {
                            subscriber.next(value);
                        }
                    },
                    error: function (err) {
                        subscriber.error(err);
                    },
                    complete: function () {
                        subscriber.complete();
                    }
                });
                subscriber.add(defaultMessagesSubscription);
                event.setHeader(exports.EventConstants.REPLY_TO_HEADER, _this.replyToCri);
                event.setHeader(exports.EventConstants.CORRELATION_ID_HEADER, correlationId);
                _this.send(event);
                return function () {
                    if (sendControlEvents && !serverSignaledCompletion) {
                        var controlDestination = exports.EventConstants.SERVICE_DESTINATION_PREFIX + _this.encodedIdentity + '@' + correlationId;
                        var controlEvent = new Event(controlDestination);
                        controlEvent.setHeader(exports.EventConstants.CONTROL_HEADER, exports.EventConstants.CONTROL_VALUE_CANCEL);
                        _this.send(controlEvent);
                    }
                };
            });
        }
    };
    EventBus.prototype.observe = function (cri) {
        return this._observe(cri);
    };
    /**
     * This is internal impl of observe that creates a cold observable.
     * The public variants transform this to some type of hot observable depending on the need
     * @param cri to observe
     * @return the cold {@link Observable<IEvent>} for the given destination
     */
    EventBus.prototype._observe = function (cri) {
        return this.stompClient
            .watch(cri)
            .pipe(operators.map(function (message) {
            var e_2, _a;
            // We translate all IMessage objects to IEvent objects
            var headers = new Map();
            try {
                for (var _b = __values(Object.keys(message.headers)), _c = _b.next(); !_c.done; _c = _b.next()) {
                    var prop = _c.value;
                    headers.set(prop, message.headers[prop]);
                }
            }
            catch (e_2_1) { e_2 = { error: e_2_1 }; }
            finally {
                try {
                    if (_c && !_c.done && (_a = _b.return)) _a.call(_b);
                }
                finally { if (e_2) throw e_2.error; }
            }
            return new Event(cri, headers, message.binaryBody);
        }));
    };
    EventBus = __decorate([
        inversifyProps.injectable(),
        __metadata("design:paramtypes", [])
    ], EventBus);
    return EventBus;
}());
inversifyProps.container.addSingleton(EventBus);

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
var JsonEventFactory = /** @class */ (function () {
    function JsonEventFactory() {
    }
    JsonEventFactory.prototype.create = function (cri, args) {
        var event = new Event(cri);
        event.setHeader(exports.EventConstants.CONTENT_TYPE_HEADER, exports.EventConstants.CONTENT_JSON);
        if (args != null) {
            event.setDataString(JSON.stringify(args));
        }
        return event;
    };
    return JsonEventFactory;
}());
var TextEventFactory = /** @class */ (function () {
    function TextEventFactory() {
    }
    TextEventFactory.prototype.create = function (cri, args) {
        var e_1, _a;
        var event = new Event(cri);
        event.setHeader(exports.EventConstants.CONTENT_TYPE_HEADER, exports.EventConstants.CONTENT_TEXT);
        if (args != null) {
            var data = '';
            var i = 0;
            try {
                for (var args_1 = __values(args), args_1_1 = args_1.next(); !args_1_1.done; args_1_1 = args_1.next()) {
                    var arg = args_1_1.value;
                    if (i > 0) {
                        data = data + '\n';
                    }
                    data = data + arg;
                    i++;
                }
            }
            catch (e_1_1) { e_1 = { error: e_1_1 }; }
            finally {
                try {
                    if (args_1_1 && !args_1_1.done && (_a = args_1.return)) _a.call(args_1);
                }
                finally { if (e_1) throw e_1.error; }
            }
            if (data.length > 0) {
                event.setDataString(data);
            }
        }
        return event;
    };
    return TextEventFactory;
}());
var ServiceRegistry = /** @class */ (function () {
    function ServiceRegistry(eventBus) {
        this.eventBus = eventBus;
    }
    ServiceRegistry.prototype.serviceProxy = function (serviceIdentifier) {
        return new ServiceProxy(serviceIdentifier, this.eventBus);
    };
    ServiceRegistry = __decorate([
        inversifyProps.injectable(),
        __param(0, inversifyProps.inject()),
        __metadata("design:paramtypes", [Object])
    ], ServiceRegistry);
    return ServiceRegistry;
}());
var defaultEventFactory = new JsonEventFactory();
/**
 * For internal use only should not be instantiated directly
 */
var ServiceProxy = /** @class */ (function () {
    function ServiceProxy(serviceIdentifier, eventBus) {
        if (typeof serviceIdentifier === 'undefined' || serviceIdentifier.length === 0) {
            throw new Error('The serviceIdentifier provided must contain a value');
        }
        this.serviceIdentifier = serviceIdentifier;
        this.eventBus = eventBus;
    }
    ServiceProxy.prototype.invoke = function (methodIdentifier, args, scope, eventFactory) {
        return this.__invokeStream(false, methodIdentifier, args, scope, eventFactory).pipe(operators.first()).toPromise();
    };
    ServiceProxy.prototype.invokeStream = function (methodIdentifier, args, scope, eventFactory) {
        return this.__invokeStream(true, methodIdentifier, args, scope, eventFactory);
    };
    ServiceProxy.prototype.__invokeStream = function (sendControlEvents, methodIdentifier, args, scope, eventFactory) {
        var cri = exports.EventConstants.SERVICE_DESTINATION_PREFIX + (scope != null ? scope + '@' : '') + this.serviceIdentifier + '/' + methodIdentifier;
        var eventFactoryToUse = (eventFactory != null ? eventFactory : defaultEventFactory);
        var event = eventFactoryToUse.create(cri, args);
        return this.eventBus.requestStream(event, sendControlEvents)
            .pipe(operators.map(function (value) {
            var contentType = value.getHeader(exports.EventConstants.CONTENT_TYPE_HEADER);
            if (contentType !== undefined) {
                if (contentType === 'application/json') {
                    return JSON.parse(value.getDataString());
                }
                else if (contentType === 'text/plain') {
                    return value.getDataString();
                }
                else {
                    throw new Error('Content Type ' + contentType + ' is unknown');
                }
            }
            else {
                return null;
            }
        }));
    };
    return ServiceProxy;
}());
inversifyProps.container.addSingleton(ServiceRegistry);

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
(function (StreamOperation) {
    StreamOperation["EXISTING"] = "EXISTING";
    StreamOperation["UPDATE"] = "UPDATE";
    StreamOperation["REMOVE"] = "REMOVE";
})(exports.StreamOperation || (exports.StreamOperation = {}));
/**
 * Holder for domain objects that will be returned as a stream of changes to a data set
 *
 * Created by Navid Mitchell on 6/3/20
 */
var StreamData = /** @class */ (function () {
    function StreamData(streamOperation, identity, value) {
        this.streamOperation = streamOperation;
        this.identity = identity;
        this.value = value;
    }
    StreamData.prototype.isSet = function () {
        return this.value !== null && this.value !== undefined;
    };
    return StreamData;
}());

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
/**
 * Default implementation of {@link ICrudServiceProxyFactory}
 */
var CrudServiceProxyFactory = /** @class */ (function () {
    function CrudServiceProxyFactory(serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }
    CrudServiceProxyFactory.prototype.crudServiceProxy = function (serviceIdentifier) {
        if (typeof serviceIdentifier === 'undefined' || serviceIdentifier.length === 0) {
            throw new Error('The serviceIdentifier provided must contain a value');
        }
        return new CrudServiceProxy(this.serviceRegistry.serviceProxy(serviceIdentifier));
    };
    CrudServiceProxyFactory.prototype.searchServiceProxy = function (serviceIdentifier) {
        if (typeof serviceIdentifier === 'undefined' || serviceIdentifier.length === 0) {
            throw new Error('The serviceIdentifier provided must contain a value');
        }
        return new SearchServiceProxy(this.serviceRegistry.serviceProxy(serviceIdentifier));
    };
    CrudServiceProxyFactory = __decorate([
        inversifyProps.injectable(),
        __param(0, inversifyProps.inject()),
        __metadata("design:paramtypes", [Object])
    ], CrudServiceProxyFactory);
    return CrudServiceProxyFactory;
}());
inversifyProps.container.addSingleton(CrudServiceProxyFactory);

exports.CrudServiceProxy = CrudServiceProxy;
exports.DataSourceUtils = DataSourceUtils;
exports.Event = Event;
exports.EventBus = EventBus;
exports.JsonEventFactory = JsonEventFactory;
exports.Order = Order;
exports.Page = Page;
exports.Pageable = Pageable;
exports.SearchCriteria = SearchCriteria;
exports.SearchServiceProxy = SearchServiceProxy;
exports.ServiceRegistry = ServiceRegistry;
exports.Sort = Sort;
exports.StreamData = StreamData;
exports.TextEventFactory = TextEventFactory;
