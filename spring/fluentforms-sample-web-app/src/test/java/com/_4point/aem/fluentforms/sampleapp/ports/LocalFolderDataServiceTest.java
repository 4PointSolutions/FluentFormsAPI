package com._4point.aem.fluentforms.sampleapp.ports;

import java.nio.file.Path;

import org.junit.jupiter.api.io.TempDir;

class LocalFolderDataServiceTest extends AbstractDataServiceTest {
	
	@TempDir 
	static Path testFolder;

	public LocalFolderDataServiceTest() {
		super(new LocalFolderDataService(testFolder), new String[] {testFolder.toString(), "Error while reading file" }, new String[] {testFolder.toString(), "Error while writing file" });
	}
}
