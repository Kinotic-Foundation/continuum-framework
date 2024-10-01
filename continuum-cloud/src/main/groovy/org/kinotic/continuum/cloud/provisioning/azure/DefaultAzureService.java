package org.kinotic.continuum.cloud.provisioning.azure;

import com.azure.core.management.Region;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.containerregistry.models.Registry;
import org.kinotic.continuum.grind.api.JobDefinition;
import org.kinotic.continuum.grind.api.Tasks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

/**
 * Created by Navíd Mitchell 🤪 on 9/26/24.
 */
@Component
public class DefaultAzureService {

    public JobDefinition createAksJob(AzureConnectionInfo azureConnectionInfo,
                                      Region region,
                                      String resourcePrefix,
                                      int nodeCount) {

        String rgName = resourcePrefix + "-rg";
        String acrName = resourcePrefix + "acr";
        String aksName = resourcePrefix + "-aks";

        return JobDefinition
                .create("Creating Aks cluster "+aksName)
                .taskStoreResult(Tasks.fromCallable("Create Azure Resource Manager",
                                                    () -> AzureResourceManager.authenticate(azureConnectionInfo.tokenCredential(),
                                                                                            azureConnectionInfo.profile())
                                                                              .withDefaultSubscription()))
                .task(Tasks.fromRunnable("Create Resource Group " + rgName,
                                         new Runnable() {
                    @Autowired
                    private AzureResourceManager resourceManager;

                    @Override
                    public void run() {
                        resourceManager.resourceGroups()
                                       .define(rgName)
                                       .withRegion(region)
                                       .create();
                    }
                }))
                .taskStoreResult(Tasks.fromCallable("Create Azure Container Registry " + acrName,
                                                    new Callable<Registry>() {
                                                        @Autowired
                                                        private AzureResourceManager resourceManager;

                                                        @Override
                                                        public Registry call() {
                                                            return resourceManager.containerRegistries()
                                                                                  .define(acrName)
                                                                                  .withRegion(region)
                                                                                  .withExistingResourceGroup(rgName)
                                                                                  .withBasicSku()
                                                                                  .create();
                                                        }
                                                    }))
                .task(Tasks.fromExec("Create Azure Kubernetes " + aksName,
                                     "az",
                                     "aks", "create",
                                     "-g", rgName,
                                     "-n", aksName,
                                     "--attach-acr", acrName,
                                     "--enable-managed-identity",
                                     "--enable-blob-driver",
                                     "--enable-addons","azure-keyvault-secrets-provider",
                                     "--generate-ssh-keys",
                                     "--node-count", String.valueOf(nodeCount),
                                     "--node-vm-size", "Standard_DS3_v2",
                                     "--verbose"));
    }

}
