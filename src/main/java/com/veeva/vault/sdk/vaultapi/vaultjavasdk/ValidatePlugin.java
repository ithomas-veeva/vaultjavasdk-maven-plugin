package com.veeva.vault.sdk.vaultapi.vaultjavasdk;

import com.veeva.vault.sdk.vaultapi.vaultjavasdk.utilities.ErrorHandler;
import com.veeva.vault.sdk.vaultapi.vaultjavasdk.utilities.PackageManager;
import com.veeva.vault.vapil.api.model.response.ValidatePackageResponse;
import org.apache.log4j.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Goal that validates the last modified VPK in the "deployment/packages" directory against the validation API endpoint.
 * 
 */

@Mojo( name = "validate", requiresProject = false)
public class ValidatePlugin extends BaseMojo {

	private static final Logger logger = Logger.getLogger(ValidatePlugin.class);


	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();
		//Initializes an Authentication API connection.
		try {
			if (vaultClient.validateSession()) {
				//Validates the defined VPK against the specified vault.
				logger.debug("Session is valid");
				if (!packageName.equals("")) {
					PackageManager.setPackagePath(packageName);
				}
				
				if (PackageManager.getPackagePath() != null) {
					ValidatePackageResponse response = PackageManager.validatePackage(vaultClient, PackageManager.getPackagePath());

					if (response.isSuccessful()) {
						logger.debug("Validation successful");
					} else {
						ErrorHandler.logErrors(response);
					}

				}
				else {
			        logger.error("Cannot validate package. There is no VPK in '<PROJECT_DIRECTORY>/deployment/packages/'.");
				}			
			} else {
				logger.error("Not a valid session. Check the login details in the pom file.");
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
