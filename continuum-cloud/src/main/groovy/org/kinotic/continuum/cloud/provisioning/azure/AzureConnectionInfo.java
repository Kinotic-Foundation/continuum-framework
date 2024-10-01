package org.kinotic.continuum.cloud.provisioning.azure;

import com.azure.core.credential.TokenCredential;
import com.azure.core.management.profile.AzureProfile;

/**
 * Created by Navíd Mitchell 🤪 on 9/26/24.
 */
public record AzureConnectionInfo(AzureProfile profile, TokenCredential tokenCredential) {

}
