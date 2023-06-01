package com._4point.aem.fluentforms.sampleapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com._4point.aem.fluentforms.api.output.OutputService;

@SpringBootApplication
public class SimpleSampleApplication implements CommandLineRunner {

	@Autowired
	OutputService outputService;
	
	public static void main(String[] args) {
		SpringApplication.run(SimpleSampleApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// TODO:  Build this out to invoke FluentForms.
		System.out.println("Application ran successfully!");
	}
}
