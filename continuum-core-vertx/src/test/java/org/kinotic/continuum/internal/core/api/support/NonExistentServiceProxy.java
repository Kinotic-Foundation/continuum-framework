package org.kinotic.continuum.internal.core.api.support;

import org.kinotic.continuum.api.annotations.Proxy;
import reactor.core.publisher.Mono;

/**
 * Created by Navíd Mitchell 🤪 on 5/12/22.
 */
@Proxy(namespace = "com.namespace",
       name = "NonExistentService",
       version = "0.1.0")
public interface NonExistentServiceProxy {

    Mono<Void> probablyNotHome();

}
