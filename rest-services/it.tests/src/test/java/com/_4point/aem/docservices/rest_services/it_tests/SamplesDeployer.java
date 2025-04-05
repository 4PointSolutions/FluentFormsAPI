package com._4point.aem.docservices.rest_services.it_tests;

import java.nio.file.Path;
import java.nio.file.Paths;

import com._4point.aem.package_manager.FormsAndDocumentsClient;
import com._4point.aem.package_manager.FormsAndDocumentsClientEx;

/**
 * Used for deploying sample files to AEM for testing purposes.  This includes the following files:
 *  - SampleForm.xdp (deployed to remote file system for testing PDF rendering using a file reference)
 *  - SampleForm.xdp (deployed to /content/dam/formsanddocuments/sample-forms for CRX testing0
 *  - Sample0002test.zip (deployed to /content/dam/formsanddocuments for adaptive forms testing)
 *  
 *  Possible future things:
 *  - Deploy ReaderExtensions credentials
 */
public class SamplesDeployer {
	private final FormsAndDocumentsClientEx client;
	
	public SamplesDeployer(String serverName, Integer port, String user, String password) {
		this(FormsAndDocumentsClient.builder()
				// By commenting the server name, port, user, and password lines out, the location defaults to locahost:4502 and admin/admin.
				  					.serverName(serverName)
				  					.port(port)
				  					.user(user)
				  					.password(password)
				  					//.password("admin")
				  					.logger(System.out::println)
				  					.buildEx()
			);
	}

	private SamplesDeployer(FormsAndDocumentsClientEx client) {
		this.client = client;
	}

	public void deployXdp(Path localFile, String remoteLocation) {
//		client.delete("sample-forms");
		client.upload(localFile, remoteLocation);
	}
	
	public void deployAf(Path localZipFile) {
//		client.delete("sample-forms");
		client.upload(localZipFile);
	}
	
}
