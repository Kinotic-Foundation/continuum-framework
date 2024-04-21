package org.kinotic.continuum.gatewayserver.clienttest;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by Navíd Mitchell 🤪 on 7/12/23.
 */
@Component
@Profile("clienttest")
public class DefaultTestService implements ITestService{

    private static final UUID TEST_UUID = UUID.randomUUID();

    @Override
    public String testMethodWithString(String value) {
        return "Hello "+ value;
    }

    @Override
    public UUID getTestUUID(){
        return TEST_UUID;
    }

}
