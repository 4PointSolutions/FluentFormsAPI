package com._4point.aem.docservices.rest_services.it_tests;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class AbstractAemContainerTest {
	private static final boolean useTestContainers = true;

	@SuppressWarnings("resource")
	@AutoClose
	static GenericContainer<?> aemContainer = useTestContainers ? new GenericContainer<>(DockerImageName.parse("ghcr.io/4pointsolutions-ps/aem:aem65sp21"))
																										.withExposedPorts(4502)
																: null
			;

	static {
		   aemContainer.start();
	}

	@BeforeAll
	static void setup() {
		// Make sure AEM is up before running the tests.
		Integer mappedPort = aemPort();
		System.out.println(String.format("Waiting for AEM to be available on port %d.", mappedPort));
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
		      .uri(URI.create(String.format("http://localhost:%d/", mappedPort)))
			  .header("Authorization", encodeBasic(TestUtils.TEST_USER, TestUtils.TEST_USER_PASSWORD))
			  .GET()
		      .build();

		Awaitility.await()
				  .atMost(5, TimeUnit.MINUTES)
				  .pollDelay(50, TimeUnit.SECONDS)
				  .pollInterval(10, TimeUnit.SECONDS)
				  .until(()->aemIsUp(client, request));
		System.out.println("AEM is available, starting tests...");
	}

	protected static String aemHost() {
		return useTestContainers ? "localhost" : TestUtils.TEST_MACHINE_NAME;
	}
	
	protected static Integer aemPort() {
		return useTestContainers ? aemContainer.getMappedPort(4502) : Integer.parseInt(TestUtils.TEST_MACHINE_PORT_STR) ;
	}

	private static boolean aemIsUp(HttpClient restClient, HttpRequest request) {
		try {
			HttpResponse<Void> result = restClient.send(request, BodyHandlers.discarding());
			int statusCode = result.statusCode();
			System.out.println(String.format("AEM returned status code %d.", statusCode));
			return statusCode >= 200 || statusCode <= 399;	// Wait for successful status code.
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
