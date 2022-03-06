package com._4point.aem.docservices.rest_services.cli.restservices.cli.output;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import com._4point.aem.docservices.rest_services.cli.restservices.cli.ResultCommands;
import com._4point.aem.docservices.rest_services.cli.restservices.cli.Results;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.api.output.OutputService;
import com._4point.aem.fluentforms.api.output.OutputService.OutputServiceException;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.UsageContext;
import com._4point.aem.fluentforms.impl.output.OutputServiceImpl;

@ShellComponent
public class OutputServiceCommands {
	private static final Logger log = LoggerFactory.getLogger(OutputServiceCommands.class);
	private static final DocumentFactory docFactory = SimpleDocumentFactoryImpl.getFactory(); 
	private final OutputService outputService; 
	
	OutputServiceCommands(OutputServiceAdapterFactory factory) {
		this.outputService = new OutputServiceImpl(factory.getAdapter(), UsageContext.CLIENT_SIDE);
	}

	@ShellMethod("Generate a PDF using a template from the local system.")
    public String generatePDFOutput(
    		@ShellOption({"-t", "--template"}) Path template, 
    		@ShellOption({"-d", "--data"}) Path data, 
    		@ShellOption(value={"-o", "--output"}, defaultValue="") Path output
    		) {
		try {
			Document result = outputService.generatePDFOutput()
						 .executeOn(docFactory.create(template), docFactory.create(data));
			if (!output.toString().isBlank()) {
				result.getInputStream().transferTo(Files.newOutputStream(output));
			}
			ResultCommands.setResults(Results.ofPdf("generate-pdf-output", result.getInputStream().readAllBytes()));
			return "Document generated";
		} catch (OutputServiceException e) {
			String msg = "Document generation failed (" + e.getMessage() + ").";
			log.error(msg, e);
			return msg;
		} catch (IOException e) {
			String msg = "Failed to write to file '" + output + "' (" + e.getMessage() + ").";
			log.error(msg, e);
			return msg;
		}
    }
	
	@ShellMethod("Generate a PDF using a templer on the aem server.")
    public String generatePDFOutputRemote(
    		@ShellOption({"-t", "--template"}) PathOrUrl template, 
    		@ShellOption({"-d", "--data"}) Path data, 
    		@ShellOption(value={"-o", "--output"}, defaultValue="") Path output
    		) {
		try {
			Document result = outputService.generatePDFOutput()
						 .executeOn(template, docFactory.create(data));
			if (!output.toString().isBlank()) {
				result.getInputStream().transferTo(Files.newOutputStream(output));
			}
			ResultCommands.setResults(Results.ofPdf("generate-pdf-output-remote", result.getInputStream().readAllBytes()));
			return "Document generated";
		} catch (OutputServiceException e) {
			String msg = "Document generation failed (" + e.getMessage() + ").";
			log.error(msg, e);
			return msg;
		} catch (IOException e) {
			String msg = "Failed to write to file '" + output + "' (" + e.getMessage() + ").";
			log.error(msg, e);
			return msg;
		}
    }
	
}
