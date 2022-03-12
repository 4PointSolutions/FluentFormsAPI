package com._4point.aem.docservices.rest_services.cli.restservices.cli.generatePdf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import com._4point.aem.docservices.rest_services.cli.restservices.cli.ResultCommands;
import com._4point.aem.docservices.rest_services.cli.restservices.cli.Results;
import com._4point.aem.docservices.rest_services.cli.restservices.cli.Results.Result;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.generatePDF.CreatePDFResult;
import com._4point.aem.fluentforms.api.generatePDF.GeneratePDFService;
import com._4point.aem.fluentforms.api.generatePDF.GeneratePDFService.GeneratePDFServiceException;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.generatePDF.GeneratePDFServiceImpl;

@ShellComponent
public class GeneratePdfServiceCommands {
	private static final Logger log = LoggerFactory.getLogger(GeneratePdfServiceCommands.class);
	private static final DocumentFactory docFactory = SimpleDocumentFactoryImpl.getFactory();
	private final GeneratePDFService generatePdfService;

	GeneratePdfServiceCommands(GeneratePdfServiceAdapterFactory factory) {
		this.generatePdfService = new GeneratePDFServiceImpl(factory.getAdapter());
	}

	@ShellMethod("Generate a PDF using a template from the local system.")
	public String createPdf(
			@ShellOption({"-d", "--document"}) Path documentPath, 
			@ShellOption(value={"-o", "--output"}, defaultValue="") Path output
			) {
		try {
			String fileExtension = getFileExtension(documentPath);
			Document document = docFactory.create(documentPath);
			CreatePDFResult result = this.generatePdfService.createPDF()
															.executeOn(document, fileExtension);
			
			byte[] resultBytes = getAllBytes(result.getCreatedDocument());
			if (!output.toString().isBlank()) {
				Files.write(output, resultBytes);
			}
			ResultCommands.setResults(
					Results.build("create-pdf-output", 
								  Result.ofPdf(resultBytes),
								  b->b.addSecondary("create-pdf-log", Result.ofText(getText(result.getLogDocument())))
								 )
					);
			return "Document converted to pdf";
		} catch (GeneratePDFServiceException e) {
			String msg = "Conversion to PDF failed (" + e.getMessage() + ").";
			log.error(msg, e);
			return msg;
		} catch (IOException e) {
			String msg = "Failed to write to file '" + output + "' (" + e.getMessage() + ").";
			log.error(msg, e);
			return msg;
		}
	}

	private String getFileExtension(Path filename) {
		return Optional.of(filename.getFileName().toString())
				   	   .filter(f -> f.contains("."))
				   	   .map(f -> f.substring(f.lastIndexOf(".") + 1))
				   	   .orElseThrow(()->new IllegalArgumentException("Document does not have extension. (" + filename.toString() + ")."));
	}
	
	private static byte[] getAllBytes(Document doc) {
		try {
			return doc.getInputStream().readAllBytes();
		} catch (IOException e) {
			throw new IllegalStateException("Error while reading result pdf", e);
		}
	}
	
	private static String getText(Document doc) {
		try {
			return new BufferedReader(new InputStreamReader(doc.getInputStream())).lines().collect(Collectors.joining("\n"));
		} catch (IOException e) {
			throw new IllegalStateException("Error while reading result log", e);
		}
	}
}
