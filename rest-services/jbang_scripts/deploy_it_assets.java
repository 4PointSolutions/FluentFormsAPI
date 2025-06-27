///usr/bin/env jbang "$0" "$@" ; exit $?
//REPOS mavencentral,github=https://maven.pkg.github.com/4PointSolutions/*
//DEPS info.picocli:picocli:4.7.6
//DEPS com._4point.aem:aem-package-manager-api:0.0.1-SNAPSHOT
//DEPS org.slf4j:slf4j-simple:2.0.17
//JAVA 21+

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.util.concurrent.Callable;

import com._4point.aem.package_manager.FormsAndDocumentsClient;
import com._4point.aem.package_manager.PackageManagerClient;

@Command(name = "deploy_it_assets", mixinStandardHelpOptions = true, version = "deploy_it_assets 0.1",
        description = "deploy_it_assets made with jbang")
class deploy_it_assets implements Callable<Integer> {
	private static final String AF_TEST_FORMS_GROUP = "fd/export";
	private static final String AEM_SERVER_NAME = "localhost";
	private static final Integer AEM_SERVER_PORT = 4502;
	private static final String AEM_SERVER_USER = "admin";
	private static final String AEM_SERVER_PASSWORD = "admin";
	private static final Path REST_SERVICES_PROJECT_DIR = Path.of("..");
	private static final Path SAMPLES_DIR = REST_SERVICES_PROJECT_DIR.resolve(Path.of("test_containers", "ff_it_files"));
	private static final Path AF_TEST_FORMS_PATH = SAMPLES_DIR.resolve("sample00002test.zip");
	private static final Path OF_TEST_FORMS_PATH = SAMPLES_DIR.resolve("SampleForm.xdp");

	
	// |   These are integration tests so, in order for these tests to run, there needs to be a running AEM instance locally on 
	// |   port 4502  (or non-locally if you're willing to modify the TEST_MACHINE and TEST_MACHINE_PORT values in TestUtils.java).
	// |
	// |   The testing instance must have the SampleForm.xdp uploaded into a directory named sample-forms that resides directly under the
	// |   FormsAndDocuments within the CRX repository.  Without this, all the crx-related tests will fail.
	// |
	// |   The testing instance must have the sample0002test.zip and sampleForm_JSON.zip packages uploaded amd installed.
	// |   These packages contain adaptive forms that reside directly under theFormsAndDocuments called sample0002test and sample-json-adaptive-form-1.  
	// |   Without these forms, all the adaptive forms tests will fail.
	// |
	// |	Protected mode must be turned off, per this: https://experienceleague.adobe.com/docs/experience-manager-65/forms/html5-forms/preview-xdp-forms-html.html?lang=en#disable-protected-mode
	// |
	// |   Also, the testing instance must have a ReaderExtensions credential installed under admin using an alias of "recred" in order for
	// |   all the SecureDocument/testReaderExtendPDF tests to pass.



    @Parameters(index = "0", description = "The greeting to print", defaultValue = "World!")
    private String greeting;

    public static void main(String... args) {
        int exitCode = new CommandLine(new deploy_it_assets()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception { // your business logic goes here...

    	System.out.println("ProjectDir = '" + REST_SERVICES_PROJECT_DIR.toRealPath().toAbsolutePath() + "'");
    	var pmClient = PackageManagerClient.builder()
				// By commenting the server name, port, user, and password lines out, the location defaults to locahost:4502 and admin/admin.
				.serverName(AEM_SERVER_NAME)
				.port(AEM_SERVER_PORT)
				.user(AEM_SERVER_USER)
				.password(AEM_SERVER_PASSWORD)
				// .password("admin")
				.logger(System.out::println)
				.buildEx();
    	
    	
		// Un-install the current package(s) on the AEM server instance before uploading and installing any new packages...
		pmClient.uninstallAndDeletePackages(pkg->AF_TEST_FORMS_GROUP.equals(pkg.group()));

		// Upload and install the Sample Adaptive Forms package...
		pmClient.uploadPackage(AF_TEST_FORMS_PATH);
		pmClient.installPackage(AF_TEST_FORMS_GROUP, "DownloadedFormsPackage_525101667060900.zip");

    	System.out.println("AF Installed!");

    	
    	var fadClient = FormsAndDocumentsClient.builder()
    						// By commenting the server name, port, user, and password lines out, the location defaults to locahost:4502 and admin/admin.
		  					.serverName(AEM_SERVER_NAME)
		  					.port(AEM_SERVER_PORT)
		  					.user(AEM_SERVER_USER)
		  					.password(AEM_SERVER_PASSWORD)
		  					//.password("admin")
		  					.logger(System.out::println)
		  					.buildEx();

    	fadClient.upload(OF_TEST_FORMS_PATH, "sample-forms");
    	
    	
    	System.out.println("Sample forms deployed!");
		
        return 0;
    }
}
