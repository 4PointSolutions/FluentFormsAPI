package com._4point.aem.docservices.rest_services.cli.restservices.cli;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.shell.Shell;
import org.springframework.shell.result.DefaultResultHandler;

@SpringBootTest(properties = { "spring.shell.interactive.enabled=false", "spring.shell.script.enabled=false" })
class ApplicationTests {

	@Autowired
	private Shell shell;

	@Autowired
	private DefaultResultHandler resultHandler;

	@Test
	void contextLoads() {
		Object help = shell.evaluate(() -> "help");
		assertNotNull(help);

		resultHandler.handleResult(help);
		assertThat(help.toString(), containsString("add-output"));
	}

}
