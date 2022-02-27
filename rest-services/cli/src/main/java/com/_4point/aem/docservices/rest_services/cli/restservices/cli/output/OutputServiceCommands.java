package com._4point.aem.docservices.rest_services.cli.restservices.cli.output;

import java.io.FileNotFoundException;
import java.nio.file.Path;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

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
	private static final DocumentFactory docFactory = SimpleDocumentFactoryImpl.getFactory(); 
	private final OutputService outputService; 
	
	OutputServiceCommands(OutputServiceAdapterFactory factory) {
		this.outputService = new OutputServiceImpl(factory.getAdapter(), UsageContext.CLIENT_SIDE);
	}

	@ShellMethod("Generate a PDF using a template from the local system.")
    public String generatePDFOutput(Path template, Path data) {
		try {
			Document result = outputService.generatePDFOutput()
						 .executeOn(docFactory.create(template), docFactory.create(data));
			return "Document generated";
		} catch (OutputServiceException e) {
			return "Document generation failed (" + e.getMessage() + ").";
		}
    }
	
	@ShellMethod("Generate a PDF using a templer on the aem server.")
    public String generatePDFOutputRemote(PathOrUrl template, Path data) {
		try {
			Document result = outputService.generatePDFOutput()
						 .executeOn(template, docFactory.create(data));
			return "Document generated";
		} catch (OutputServiceException | FileNotFoundException e) {
			return "Document generation failed (" + e.getMessage() + ").";
		}
    }
	
}
