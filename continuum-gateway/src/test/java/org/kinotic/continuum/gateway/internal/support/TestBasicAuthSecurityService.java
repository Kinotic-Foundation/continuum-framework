package org.kinotic.continuum.gateway.internal.support;

import org.kinotic.continuum.core.api.security.DefaultParticipant;
import org.kinotic.continuum.core.api.security.MetadataConstants;
import org.kinotic.continuum.core.api.security.Participant;
import org.kinotic.continuum.core.api.security.SecurityService;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Created by Navíd Mitchell 🤪on 6/19/23.
 */
@Component
public class TestBasicAuthSecurityService implements SecurityService {

    @Override
    public CompletableFuture<Participant> authenticate(Map<String, String> authenticationInfo) {
        // Header looks something like
        // "Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ=="
        String authorizationHeader = authenticationInfo.get("authorization");
        if (authorizationHeader != null) {
            String[] parts = authorizationHeader.split(" ");
            if (parts.length == 2 && "Basic".equalsIgnoreCase(parts[0])) {
                String credentials = new String(Base64.getDecoder().decode(parts[1]), StandardCharsets.UTF_8);
                String[] creds = credentials.split(":", 2);
                if (creds.length == 2) {
                    if (creds[0].equals("guest") && creds[1].equals("guest")) {

                        return CompletableFuture.completedFuture(new DefaultParticipant("kinotic",
                                                                                        "guest",
                                                                                        Map.of(MetadataConstants.TYPE_KEY,
                                                                                               "user"),
                                                                                        List.of("ADMIN")));
                    }
                }
            }
        }
        return CompletableFuture.failedFuture(new IllegalArgumentException("Unauthorized"));
    }
}
