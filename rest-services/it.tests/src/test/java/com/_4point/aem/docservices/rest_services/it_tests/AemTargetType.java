package com._4point.aem.docservices.rest_services.it_tests;

import java.nio.file.Path;

/**
 * Enum representing the different target types for AEM instances in the
 * integration tests. Each enum value corresponds to a specific environment
 * where AEM is expected to run, along with the path to the sample files used in
 * the tests.
 */
public enum AemTargetType {
	LOCAL(Path.of("..", "test_containers", "ff_it_files").toAbsolutePath()),			// Running on local machine (assumes that the port is TEST_MACHINE_PORT)	
	REMOTE_WINDOWS(Path.of("/Adobe", "ff_it_files")), // Running on remote Windows machine (assumes that machine name is TEST_MACHINE_NAME and port is TEST_MACHINE_PORT) 
	REMOTE_LINUX(Path.of("/opt", "adobe", "ff_it_files")), 	// Running on remote Linux machine (assumes that machine name is TEST_MACHINE_NAME and port is TEST_MACHINE_PORT)
	TESTCONTAINERS(Path.of("/opt", "adobe", "ff_it_files")); // Running on local testcontainers image (gets port from TestContainers)
	
	private final Path samplesPath;		// Location where sample files are stored for this AEM target type.

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

