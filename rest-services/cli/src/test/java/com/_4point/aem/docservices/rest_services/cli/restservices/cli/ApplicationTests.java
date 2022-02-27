package com._4point.aem.docservices.rest_services.cli.restservices.cli;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Tag;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.shell.Shell;
import org.springframework.shell.result.DefaultResultHandler;

@Tag("Integration")
@SpringBootTest(properties = { "spring.shell.interactive.enabled=false", "spring.shell.script.enabled=false" })
class ApplicationTests {

	@Autowired
	private Shell shell;

	@Autowired
	private DefaultResultHandler resultHandler;

	private String runShellCommand(String command) {
		Object cmd = shell.evaluate(()->command);
		assertNotNull(cmd);
		resultHandler.handleResult(cmd);
		return cmd.toString();
	}
	
	@Test
	void testContextLoads() {
		assertThat(runShellCommand("help"), allOf(
				containsString("generate-pdfoutput"),
				containsString("generate-pdfoutput-remote")
				));
	}

	@Test
	void testGeneratePdfOutput() {
		assertThat(runShellCommand("generate-pdfoutput --template src/test/resources/sampleFiles/SampleForm.xdp --data src/test/resources/sampleFiles/SampleForm_data.xml"), containsStringIgnoringCase("Document generated"));
	}

	@Test
	void testGeneratePdfOutputRemote() {
		assertThat(runShellCommand("generate-pdfoutput-remote"), containsStringIgnoringCase("Document generated"));
	}
}
