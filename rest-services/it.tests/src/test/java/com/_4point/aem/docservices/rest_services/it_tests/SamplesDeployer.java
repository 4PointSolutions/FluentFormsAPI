package com._4point.aem.docservices.rest_services.it_tests;

import java.nio.file.Path;
import java.nio.file.Paths;

import com._4point.aem.package_manager.FormsAndDocumentsClient;
import com._4point.aem.package_manager.FormsAndDocumentsClientEx;

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
