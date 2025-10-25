package com._4point.aem.fluentforms.sampleapp.resources;

import java.util.EnumSet;
import java.util.Set;

import com._4point.aem.fluentforms.sampleapp.resources.AemInstance.AemTargetType;

public class TestConstants {

	public static final String TEST_MACHINE_NAME = "localhost"; //"172.22.132.85";
//	public static final AemServerType TEST_MACHINE_AEM_TYPE = AemServerType.StandardType.OSGI;
	public static final int TEST_MACHINE_PORT = 4502;
	public static final String TEST_MACHINE_PORT_STR = Integer.toString(TEST_MACHINE_PORT);
	public static final String TEST_USER = "admin";
	public static final String TEST_USER_PASSWORD = "admin";

	// The TESTCONTAINERS configuration requires an AEM container image with AEM forms installed.  Since AEM is proprietary, it is not possible to obtain this
	// through public images.  By default, this uses a private image hosted in the 4PointSolutions-PS GitHub organization.  If you are not
	// part of that prg, you will have to supply your own image.
//	public static final String AEM_IMAGE_NAME = "ghcr.io/4pointsolutions-ps/aem:aem65sp21";
	public static final String AEM_IMAGE_NAME = "aem_lts_it_tests:aem65ltssp1_it_tests";

	// The following Set controls whether the integration tests are run with WireMock, TestContainers or both.
	// This is normally set to just WIREMOCK since that is what is required for the GitHub actions CI pipeline,
	// but it may be set to other values when testing locally. 
	private static Set<IntegrationTestType> TESTS_TO_RUN = EnumSet.of(IntegrationTestType.WIREMOCK);

	// If the local environment has docker installed and has a local AEM container image available, then the
	// AEM_IMAGE_NAME can be modified, and the following line uncommented to run both WireMock and an AEM instance
	// tests.
//	 private static Set<IntegrationTestType> TESTS_TO_RUN = EnumSet.of(IntegrationTestType.WIREMOCK, IntegrationTestType.AEM_INSTANCE);
	
	
	private enum IntegrationTestType { WIREMOCK, AEM_INSTANCE };
	
	public static boolean runWiremockTests() { return TESTS_TO_RUN.contains(IntegrationTestType.WIREMOCK); }
	public static boolean runAemInstanceTests() { return TESTS_TO_RUN.contains(IntegrationTestType.AEM_INSTANCE); }
	
	// Set this to indicate the type of machine that AEM is running on:
	//  The integration tests will run against an AEM instance running in a Docker container otherwise they will run against a local AEM instance.
	public static final AemTargetType AEM_TARGET_TYPE = AemTargetType.TESTCONTAINERS; 

}
