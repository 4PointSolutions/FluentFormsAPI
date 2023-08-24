///usr/bin/env jbang "$0" "$@" ; exit $?
//REPOS mavencentral,github=https://maven.pkg.github.com/4PointSolutions/*
//DEPS info.picocli:picocli:4.6.3
//DEPS com._4point.aem:fluentforms.core:0.0.3-SNAPSHOT  com._4point.aem.docservices:rest-services.client:0.0.3-SNAPSHOT
//JAVA 11+

/*
 * 	This script uses the 4PointSolutions/FluentFormsAPI GitHub package repository.  GitHub requires a user to authenticate in order
 * 	to access a package repository.  In order for this script to work, you need to have your personal credentials configured
 * 	in your local settings.xml file (found in you $HOME/.m2 directory).
 * 	
 *  Your settings.xml should look something like this:
 *  	<?xml version="1.0"?>
 *  	<settings xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://maven.apache.org/SETTINGS/1.0.0">
 *  		<servers>
 *  			<server>
 *  				<id>github</id>
 *  				<username>Your GitHub Username goes here</username>
 *  				<password>Your Personal Access Token goes here</password>
 *  			</server>
 *  		</servers>
 *  	</settings>
 * 
 */
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.Callable;

import com._4point.aem.docservices.rest_services.client.output.RestServicesOutputServiceAdapter;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.UsageContext;
import com._4point.aem.fluentforms.impl.output.OutputServiceImpl;
import com.adobe.fd.output.api.AcrobatVersion;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "invoke_output", mixinStandardHelpOptions = true, version = "invoke_output 0.1",
        description = "invoke_output invokes AEM OutputService via FluentForms")
class invoke_aem implements Callable<Integer> {

    @Option(names = {"-f", "--form"}, description = "A filepath to the XDP.", required = true)
    private Path xdpLocation;

    @Option(names = {"-d", "--data"}, description = "A filepath to the XML data file. (Optional)")
    private Path xmlLocation;

    @Option(names = {"-o", "--output"}, description = "A filepath to the output file.", required = true)
    private Path outLocation;

    public static void main(String... args) {
        int exitCode = new CommandLine(new invoke_aem()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
    	
		var adapter = RestServicesOutputServiceAdapter.builder()
				.machineName("172.18.110.35")
				.port(4502)
				.basicAuthentication("admin", "admin")
				.useSsl(false)
				.build();

		var underTest = new OutputServiceImpl(adapter, UsageContext.CLIENT_SIDE);
		
		
		var builder = underTest.generatePDFOutput()
				.setAcrobatVersion(AcrobatVersion.Acrobat_10_1)
				.setEmbedFonts(true)
				// Less used parameters are commented out which leaves them at default.
//				.setLinearizedPDF(true)
//				.setLocale(Locale.CANADA_FRENCH)
//				.setRetainPDFFormState(true)
//				.setRetainUnsignedSignatureFields(true)
				.setTaggedPDF(true);

		if (Files.exists(xdpLocation)) {
			builder = builder.setContentRoot(xdpLocation.toAbsolutePath().getParent());
		}
		
		Document pdfResult =  xmlLocation == null ? 
				builder.executeOn(xdpLocation.getFileName()) :
				builder.executeOn(xdpLocation.getFileName(), SimpleDocumentFactoryImpl.getFactory().create(xmlLocation.toFile()));

		System.out.println("Writing output to " + outLocation);
		Files.copy(pdfResult.getInputStream(), outLocation, StandardCopyOption.REPLACE_EXISTING);
		
        return 0;
    }
}
