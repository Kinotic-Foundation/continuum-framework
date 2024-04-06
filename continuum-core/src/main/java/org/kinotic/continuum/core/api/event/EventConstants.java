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

package org.kinotic.continuum.core.api.event;

import org.kinotic.continuum.api.ServerInfo;
import org.kinotic.continuum.api.security.ConnectedInfo;

/**
 *
 * Created by Navid Mitchell on 2018-12-11.
 */
public class EventConstants {

    public static final String CRI_HEADER = "cri";

    public static final String SENDER_HEADER = "sender";

    public static final String CONTENT_TYPE_HEADER = "content-type";

    public static final String CONTENT_LENGTH_HEADER = "content-length";

    public static final String REPLY_TO_HEADER = "reply-to";

    /**
     * Header provided by the sever on connection to represent the users session id
     */
    public static final String SESSION_HEADER = "session";

    /**
     * Header provided by the server on connection to provide the {@link ConnectedInfo}
     */
    public static final String CONNECTED_INFO_HEADER = "connected-info";

    /**
     * Header provided by the server on connection to provide the {@link ServerInfo} as a json string
     */
    public static final String SERVER_INFO_HEADER = "server-info";

    /**
     * Correlates a response with a given request
     * Headers that start with __ will always be persisted between messages
     */
    public static final String CORRELATION_ID_HEADER = "__correlation-id";

    /**
     * Denotes that something caused an error. Will contain a brief message about the error.
     */
    public static final String ERROR_HEADER = "error";

    /**
     * Denotes the event is a control plane event. These are used for internal coordination.
     */
    public static final String CONTROL_HEADER = "control";

    /**
     * Stream is complete no further values will be sent.
     */
    public static final String CONTROL_VALUE_COMPLETE = "complete";

    public static final String CONTROL_VALUE_CANCEL = "cancel";

    public static final String CONTROL_VALUE_SUSPEND = "suspend";

    public static final String CONTROL_VALUE_RESUME = "resume";


    public static final String SERVICE_DESTINATION_SCHEME = "srv";

    public static final String STREAM_DESTINATION_SCHEME = "stream";

    /**
     * Event data format that is pretty much a stomp frame.
     * The difference being the Destination is in place of the COMMAND portion. And there is no Destination header. Everything else is the same.
     */
    public static final byte RAW_EVENT_FORMAT_STOMPISH = 0x01;

    /**
     * Event data format that can be used for raw UTF-8 data
     */
    public static final byte RAW_EVENT_FORMAT_UTF8 = 0x02;

}
