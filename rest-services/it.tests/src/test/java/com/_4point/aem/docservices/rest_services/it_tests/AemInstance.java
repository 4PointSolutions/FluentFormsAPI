package com._4point.aem.docservices.rest_services.it_tests;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.awaitility.Awaitility;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public enum AemInstance {
	AEM_1(TestUtils.USE_TESTCONTAINERS);	// Change parameter to false to disable TestContainers and use local AEM instance..
	
	// These tests require an AEM container image with AEM forms installed.  Since AEM is proprietary, it is not possible to obtain this
	// through public images.  By default, this uses a private image hosted in the 4PointSolutions-PS GitHub organization.  If you are not
	// part of that prg, you will have to supply your own image.
	private static final String AEM_IMAGE_NAME = "ghcr.io/4pointsolutions-ps/aem:aem65sp21";
	private final GenericContainer<?> aemContainer;
	private final AtomicBoolean preparedForTests = new AtomicBoolean(false);

	private AemInstance(boolean useTestContainers) {
		this(useTestContainers ? new GenericContainer<>(DockerImageName.parse(AEM_IMAGE_NAME))
																	   .withReuse(true)
																	   .withExposedPorts(4502)
							   : null);
	}

	private AemInstance(GenericContainer<?> aemContainer) {
		this.aemContainer = aemContainer;
		if (aemContainer != null) {
			aemContainer.start();

		}
	}

	public void prepareForTests() {
		if (!preparedForTests.get()) {
			Integer mappedPort = aemPort();
			
			startAem(mappedPort);
			//	deploySampleFiles(mappedPort);
			
			preparedForTests.set(true);
			System.out.println("Starting tests...");
		}		
	}

	// Make sure AEM is up before running the tests.
	private static void startAem(Integer mappedPort) {
		System.out.println(String.format("Checking if AEM is available on port %d.", mappedPort));
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
		      .uri(URI.create(String.format("http://localhost:%d/", mappedPort)))
			  .header("Authorization", encodeBasic(TestUtils.TEST_USER, TestUtils.TEST_USER_PASSWORD))
			  .GET()
		      .build();

		if (!aemIsUp(client, request)) {
			System.out.println(String.format("Waiting for AEM to become available."));
			Awaitility.await()
					  .atMost(5, TimeUnit.MINUTES)
					  .pollDelay(50, TimeUnit.SECONDS)
					  .pollInterval(10, TimeUnit.SECONDS)
					  .until(()->aemIsUp(client, request));
		}
		System.out.println("AEM is available.");
	}

	public static void deploySampleFiles(Integer mappedPort) {
		System.out.println("Deploying sample files...");

		SamplesDeployer deployer = new SamplesDeployer("localhost", mappedPort, TestUtils.TEST_USER, TestUtils.TEST_USER_PASSWORD);
		
		deployer.deployXdp(Paths.get("src", "test", "resources", "SampleForm.xdp"), "sample-forms");
		deployer.deployAf(Paths.get("src", "test", "resources", "sample00002test.zip"));
//		deployer.deployAf(Paths.get("src", "test", "resources", "SampleForm.xdp"), "sample-forms");
		System.out.println("Sample files deployed.");
	}

	public String aemHost() {
		return aemContainer != null ? "localhost" : TestUtils.TEST_MACHINE_NAME;
	}
	public Integer aemPort() {
		return aemContainer != null ? aemContainer.getMappedPort(4502) : Integer.parseInt(TestUtils.TEST_MACHINE_PORT_STR) ;
	}

	private static boolean aemIsUp(HttpClient restClient, HttpRequest request) {
		try {
			HttpResponse<Void> result = restClient.send(request, BodyHandlers.discarding());
			int statusCode = result.statusCode();
			System.out.println(String.format("AEM returned status code %d.", statusCode));
			return statusCode >= 200 && statusCode <= 399;	// Wait for successful status code.
		} catch (InterruptedException e) {
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private static String encodeBasic(String username, String password) {
	    return "Basic "+ Base64
	        .getEncoder()
	        .encodeToString((username+":"+password).getBytes());
	}
}
