package com._4point.aem.fluentforms.impl.generatePDF;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.generatePDF.CreatePDFResult;
import com._4point.aem.fluentforms.api.generatePDF.GeneratePDFService.GeneratePDFServiceException;
import com._4point.aem.fluentforms.impl.AdobeDocumentFactoryImpl;
import com.adobe.pdfg.exception.ConversionException;
import com.adobe.pdfg.exception.FileFormatNotSupportedException;
import com.adobe.pdfg.exception.InvalidParameterException;

public class AdobeGeneratePDFServiceAdapter implements TraditionalGeneratePDFService {

	private static final Logger log = LoggerFactory.getLogger(AdobeGeneratePDFServiceAdapter.class);
	private final com.adobe.pdfg.service.api.GeneratePDFService adobeGeneratePDFService;
	private final DocumentFactory documentFactory;

	public AdobeGeneratePDFServiceAdapter(com.adobe.pdfg.service.api.GeneratePDFService adobeGeneratePDFService,
			DocumentFactory documentFactory) {
		super();
		this.adobeGeneratePDFService = Objects.requireNonNull(adobeGeneratePDFService,
				"adobeDocAssemblerService cannot be null.");
		this.documentFactory = Objects.requireNonNull(documentFactory, "Document Factory cannot be null.");
	}

	public AdobeGeneratePDFServiceAdapter(com.adobe.pdfg.service.api.GeneratePDFService adobeGeneratePDFService) {
		super();
		this.adobeGeneratePDFService = Objects.requireNonNull(adobeGeneratePDFService,
				"adobeDocAssemblerService cannot be null.");
		this.documentFactory = DocumentFactory.getDefault();
	}

	@Override
	public CreatePDFResult createPDF2(Document inputDoc, String inputFileExtension, String fileTypeSettings,
			String pdfSettings, String securitySettings, Document settingsDoc, Document xmpDoc)
					throws GeneratePDFServiceException {
		com.adobe.pdfg.result.CreatePDFResult createPDFResult;
		try {
			log.info("Converting document with extension " +inputFileExtension +" to pdf");
			createPDFResult = adobeGeneratePDFService.createPDF2(AdobeDocumentFactoryImpl.getAdobeDocument(inputDoc),
					inputFileExtension, fileTypeSettings, pdfSettings, securitySettings,
					AdobeDocumentFactoryImpl.getAdobeDocument(settingsDoc),
					AdobeDocumentFactoryImpl.getAdobeDocument(xmpDoc));
		} catch (ConversionException | InvalidParameterException | FileFormatNotSupportedException e) {
			throw new GeneratePDFServiceException("Error while converting document to pdf ", e);
		}
		return convertAdobeCreatePDFResultToCreatePDFResult(createPDFResult);
	}

	private CreatePDFResult convertAdobeCreatePDFResultToCreatePDFResult(
			com.adobe.pdfg.result.CreatePDFResult createPDFResult) {
		log.info("Converting adobeCreatePDFResult to createdPDFResult");
		CreatePDFResultImpl createPDFResultImpl = new CreatePDFResultImpl();
		createPDFResultImpl.setGetCreatedDocument(documentFactory.create(createPDFResult.getCreatedDocument()));
		createPDFResultImpl.setGetLogDocument(documentFactory.create(createPDFResult.getLogDocument()));
		return createPDFResultImpl;
	}

}
