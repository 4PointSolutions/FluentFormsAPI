package com._4point.aem.fluentforms.sampleapp;

import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com._4point.aem.fluentforms.sampleapp.domain.DataService;
import com._4point.aem.fluentforms.sampleapp.ports.InMemoryDataService;
import com._4point.aem.fluentforms.sampleapp.ports.LocalFolderDataService;

@SpringBootApplication
public class FluentFormsSpringApplication {

	public FluentFormsSpringApplication(GitConfig gitConfig) {
    	gitConfig.logGitInformation();
	}

	public static void main(String[] args) {
		SpringApplication.run(FluentFormsSpringApplication.class, args);
	}

//	@Bean
//	public static DataService dataService(@Value("${sampleapp.dataservice.folder}") Path dataFolder) {
//		return new LocalFolderDataService(dataFolder);
//	}
	
	@Bean
	public static DataService dataService() {
		return new InMemoryDataService();
	}
}
