package com._4point.aem.fluentforms.sampleapp;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com._4point.aem.fluentforms.sampleapp.domain.DataService;
import com._4point.aem.fluentforms.sampleapp.ports.InMemoryDataService;
import com._4point.aem.fluentforms.sampleapp.ports.LocalFolderDataService;

@SpringBootApplication
public class FluentFormsSpringApplication {
	private final static Logger log = LoggerFactory.getLogger(FluentFormsSpringApplication.class);

	public FluentFormsSpringApplication(GitConfig gitConfig) {
    	gitConfig.logGitInformation();
	}

	public static void main(String[] args) {
		SpringApplication.run(FluentFormsSpringApplication.class, args);
	}

	@Bean
	public static DataService dataService(@Value("${mockclient.dataservice.folder:}") Path dataFolder) {
		if(dataFolder == null || dataFolder.toString().isEmpty()) {
			log.atInfo().log("Using InMemory Data Service.");
			return new InMemoryDataService();					// If folder not provided, use InMemory service
		} else {
			log.atInfo().addArgument(dataFolder).log("Using Local Folder Data Service to folder {}.");
			return new LocalFolderDataService(dataFolder);
		}
	}
}
