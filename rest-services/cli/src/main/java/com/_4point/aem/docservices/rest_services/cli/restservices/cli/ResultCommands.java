package com._4point.aem.docservices.rest_services.cli.restservices.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import com._4point.aem.docservices.rest_services.cli.restservices.cli.Results.Result;

@ShellComponent
public class ResultCommands {

	private static final AtomicReference<Results> mostRecentResults = new AtomicReference<>();
	
	public static void setResults(Results latestResults) {
		mostRecentResults.set(latestResults);
	}
	
	private static Results getResults() {
		return mostRecentResults.get();
	}

	@ShellMethod("List the results of the last operation.")
	public String resultsList() {
		Results results = getResults();
		if (results == null) return "No results to report.";
		
		return results.display().stream().collect(Collectors.joining("\n"));
	}
	
	@ShellMethod("Save the results of the last operation to your local filesystem.")
	public String resultsSave(@ShellOption(value={"-o", "--output"}, defaultValue="") Path saveLocationParameter) {
		try {
			Result primary = getResults().primary();
			Path saveLocation =  !saveLocationParameter.toString().isBlank() ? saveLocationParameter : primary.filename().orElseThrow();
			
			Files.write(saveLocation, primary.data());
			
			return "Saved to " + saveLocation.toString() + ".";
		} catch (NoSuchElementException e) {
			return "Save location must be specified. Did not save results.";
		} catch (IOException e) {
			String msg = e.getMessage();
			return "Unexpected error occurred while saving " + (msg != null ? msg : "") + " (" + e.getClass().getName() + "). Did not save results.";
		}
	}

	@ShellMethod("Display the results of the last operation.")
	public String resultsDisplay() {
		List<String> resultsList = getResults().display();
		String display = IntStream.range(0, resultsList.size())									// Iterate over the individual results
				 				  .mapToObj(i->String.format("%d %s", i+1, resultsList.get(i)))	// Format each result with result number in front
				 				  .collect(Collectors.joining("\n"));							// Concat into multi-line String
		
		return display;
	}

}
