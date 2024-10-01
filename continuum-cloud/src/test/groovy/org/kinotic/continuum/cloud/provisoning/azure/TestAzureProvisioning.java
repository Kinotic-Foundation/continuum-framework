package org.kinotic.continuum.cloud.provisoning.azure;

import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.Region;
import com.azure.core.management.profile.AzureProfile;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kinotic.continuum.cloud.provisioning.azure.AzureConnectionInfo;
import org.kinotic.continuum.cloud.provisioning.azure.DefaultAzureService;
import org.kinotic.continuum.grind.api.JobDefinition;
import org.kinotic.continuum.grind.api.JobService;
import org.kinotic.continuum.grind.api.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;

/**
 * Created by Navíd Mitchell 🤪 on 9/26/24.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class TestAzureProvisioning {

    private static final Logger log = LoggerFactory.getLogger(TestAzureProvisioning.class);


    @Autowired
    private JobService jobService;

    @Autowired
    private DefaultAzureService azureService;

    @Test
    public void testSimple(){

        DefaultAzureCredential defaultCredential = new DefaultAzureCredentialBuilder().build();
        AzureProfile azureProfile = new AzureProfile(AzureEnvironment.AZURE);
        AzureConnectionInfo azureConnectionInfo = new AzureConnectionInfo(azureProfile, defaultCredential);

        JobDefinition definition = azureService.createAksJob(azureConnectionInfo, Region.US_CENTRAL, "mindstest", 1);

        Flux<Result<?>> jobResult = jobService.assemble(definition);

        jobResult.doOnNext(result -> log.debug(result.toString())).blockLast();
    }
}
