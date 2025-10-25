package com._4point.aem.fluentforms.sampleapp.resources;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.awaitility.Awaitility;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * This singleton defines the AEM instance that is being used for the integration tests.
 * 
 * Change the value of TestConstants.AEM_TARGET_TYPE in the TestConstants class to tell the tests what type of AEM instance we're testing against.
 */
public enum AemInstance {
	AEM_1(TestConstants.AEM_TARGET_TYPE); // Change parameter in TestContants to choose between TestContainers, local AEM instance, and remote AEM instance.
	
	private static final String LOCALHOST = "localhost";
	

	private final AemTargetType targetType;
	private final GenericContainer<?> aemContainer;
	private final AtomicBoolean preparedForTests = new AtomicBoolean(false);

	@SuppressWarnings("resource")
	private AemInstance(AemTargetType targetType) {
		this(targetType, targetType == AemTargetType.TESTCONTAINERS ? new GenericContainer<>(DockerImageName.parse(TestConstants.AEM_IMAGE_NAME))
																	   .withReuse(true)
																	   .withExposedPorts(4502)
							   : null);
	}

	private AemInstance(AemTargetType targetType, GenericContainer<?> aemContainer) {
		this.targetType = targetType;
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
			  .header("Authorization", encodeBasic(TestConstants.TEST_USER, TestConstants.TEST_USER_PASSWORD))
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

//	public static void deploySampleFiles(Integer mappedPort) {
//		System.out.println("Deploying sample files...");
//
//		SamplesDeployer deployer = new SamplesDeployer(LOCALHOST, mappedPort, TestConstants.TEST_USER, TestConstants.TEST_USER_PASSWORD);
//		
//		deployer.deployXdp(Paths.get("src", "test", "resources", "SampleForm.xdp"), "sample-forms");
//		deployer.deployAf(Paths.get("src", "test", "resources", "sample00002test.zip"));
//		// TODO: Create a deploy a sample AF that uses a JSON schema
////		deployer.deployAf(Paths.get("src", "test", "resources", "SampleForm.xdp"), "sample-forms");
//		System.out.println("Sample files deployed.");
//	}

	/**
	 * Host name where AEM is running
	 * 
	 * @return
	 */
	public String aemHost() {
		return switch (targetType) {
		case LOCAL, TESTCONTAINERS -> LOCALHOST;
		case REMOTE_WINDOWS, REMOTE_LINUX -> TestConstants.TEST_MACHINE_NAME;
		};
	}

	/**
	 * Port where AEM is running.
	 * 
	 * @return
	 */
	public Integer aemPort() {
		return switch (targetType) {
		case LOCAL, REMOTE_WINDOWS, REMOTE_LINUX -> Integer.parseInt(TestConstants.TEST_MACHINE_PORT_STR);
		case TESTCONTAINERS -> aemContainer.getMappedPort(4502);
		};
	}

	public Boolean isLinux() {
		return switch (targetType) {
		case LOCAL -> !System.getProperty("os.name").toLowerCase().contains("windows");	// Treat MAC as Linux
		case REMOTE_WINDOWS -> false;
		case REMOTE_LINUX, TESTCONTAINERS -> true;
		};
	}
	/**
	 * Is AEM running locally or remotely? 
	 * 
	 * If is is running in a container, then it is considered remote (because it does not have access to the local file system).
	 * If AEM is running locally, then local file paths can be used, otherwise remote file paths will need to be used for some tests.
	 * 
	 * @return
	 */
	public boolean isLocal() {
		return aemContainer == null && aemHost().equalsIgnoreCase(LOCALHOST);
	}

	public Path samplesPath(String filename) {
		return targetType.samplesPath(filename);
	}

	public Path samplesPath(Path filename) {
		return samplesPath(filename.toString());
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
	
	/**
	 * Enum representing the different target types for AEM instances in the
	 * integration tests. Each enum value corresponds to a specific environment
	 * where AEM is expected to run, along with the path to the sample files used in
	 * the tests.
	 */
	public enum AemTargetType {
		LOCAL(Path.of("src", "test", "resources", "SampleFiles").toAbsolutePath()),			// Running on local machine (assumes that the port is TEST_MACHINE_PORT)	
		REMOTE_WINDOWS(Path.of("/Adobe", "ff_it_files")), // Running on remote Windows machine (assumes that machine name is TEST_MACHINE_NAME and port is TEST_MACHINE_PORT) 
		REMOTE_LINUX(Path.of("/opt", "adobe", "ff_it_files")), 	// Running on remote Linux machine (assumes that machine name is TEST_MACHINE_NAME and port is TEST_MACHINE_PORT)
		TESTCONTAINERS(Path.of("/opt", "adobe", "ff_it_files")); // Running on local testcontainers image (gets port from TestContainers)
		
		private final Path samplesPath;		// Location where sample files are stored for this AEM target type.  
											// Needs to be absolute path since (in some cases) it will be passed to AEM.

		private AemTargetType(Path samplesPath) {
			this.samplesPath = samplesPath;
		}

		/**
		 * Returns the path to the samples directory for this AEM target type.
		 * 
		 * @return Path to the samples directory.
		 */
		public Path samplesPath(String filename) {
			return this.samplesPath.resolve(filename);
		}
	}
	
	/**
	 * Class used to initialize the Spring Boot context with all the parameters required to use an AEM instance.
	 */
	static class AemInstanceContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

		@Override
		public void initialize(ConfigurableApplicationContext applicationContext) {
			System.out.println("Setting context: %s:%d".formatted(AEM_1.aemHost(),AEM_1.aemPort()));
			TestPropertyValues
					.of(Map.of(
							"fluentforms.aem.host", AEM_1.aemHost(),
							"fluentforms.aem.port", AEM_1.aemPort().toString()
							))
					.applyTo(applicationContext);
		}
	}
}
