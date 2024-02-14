package org.kinotic.continuum.gatewayserver.clienttest;

import org.kinotic.continuum.api.annotations.Proxy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Created by Navíd Mitchell 🤪 on 7/12/23.
 */
@Component
@Profile("clienttest")
public class DefaultTestService implements ITestService{

    @Override
    public String testMethodWithString(String value) {
        return "Hello "+ value;
    }
}
