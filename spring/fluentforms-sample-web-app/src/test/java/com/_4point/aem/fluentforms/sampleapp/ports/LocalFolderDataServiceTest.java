package com._4point.aem.fluentforms.sampleapp.ports;

import static com._4point.testing.matchers.javalang.ExceptionMatchers.exceptionMsgContainsAll;
import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com._4point.aem.fluentforms.sampleapp.domain.DataService.DataServiceException;

class LocalFolderDataServiceTest extends AbstractDataServiceTest {
	
	@TempDir 
	static Path testFolder;

	public LocalFolderDataServiceTest() {
		super(new LocalFolderDataService(testFolder), new String[] {testFolder.toString(), "Error while reading file" }, new String[] {testFolder.toString(), "Error while writing file" });
	}
}
