package com._4point.aem.fluentforms.impl.output;

import static com._4point.aem.fluentforms.impl.BuilderUtils.setIfNotNull;

import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.forms.PDFFormRenderOptions;
import com._4point.aem.fluentforms.api.output.BatchOptions;
import com._4point.aem.fluentforms.api.output.BatchResult;
import com._4point.aem.fluentforms.api.output.OutputService.OutputServiceException;
import com._4point.aem.fluentforms.impl.AdobeDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.forms.AdobeFormsServiceAdapter;
import com._4point.aem.fluentforms.api.output.PDFOutputOptions;
import com._4point.aem.fluentforms.api.output.PrintedOutputOptions;

public class AdobeOutputServiceAdapter implements TraditionalOutputService {

	private static final Logger log = LoggerFactory.getLogger(AdobeOutputServiceAdapter.class);

	private final DocumentFactory documentFactory;
	private final com.adobe.fd.output.api.OutputService adobeOutputService;

	private AdobeOutputServiceAdapter(com.adobe.fd.output.api.OutputService adobeOutputService) {
		super();
		this.documentFactory = DocumentFactory.getDefault();
		this.adobeOutputService = Objects.requireNonNull(adobeOutputService, "Adobe Output Service cannot be null.");
	}

	private AdobeOutputServiceAdapter(com.adobe.fd.output.api.OutputService adobeOutputService, DocumentFactory documentFactory) {
		super();
		this.documentFactory = Objects.requireNonNull(documentFactory, "Document Factory cannot be null.");
		this.adobeOutputService = Objects.requireNonNull(adobeOutputService, "Adobe Output Service cannot be null.");
	}

	@Override
	public Document generatePDFOutput(Document template, Document data, PDFOutputOptions pdfOutputOptions) throws OutputServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document generatePDFOutput(String urlOrFileName, Document data, PDFOutputOptions pdfOutputOptions) throws OutputServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BatchResult generatePDFOutputBatch(Map<String, String> templates, Map<String, Document> data, PDFOutputOptions pdfOutputOptions, BatchOptions batchOptions)
			throws OutputServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document generatePrintedOutput(Document template, Document data, PrintedOutputOptions printedOutputOptions) throws OutputServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document generatePrintedOutput(String urlOrFileName, Document data, PrintedOutputOptions printedOutputOptions) throws OutputServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BatchResult generatePrintedOutputBatch(Map<String, String> templates, Map<String, Document> data, PrintedOutputOptions printedOutputOptions, BatchOptions batchOptions)
			throws OutputServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	// Package visibility so that it can be used in unit testing.
	/* package */ static com.adobe.fd.output.api.PDFOutputOptions toAdobePDFOutputOptions(PDFOutputOptions options) {
		com.adobe.fd.output.api.PDFOutputOptions adobeOptions = new com.adobe.fd.output.api.PDFOutputOptions();
		setIfNotNull(adobeOptions::setAcrobatVersion, options.getAcrobatVersion());
		setIfNotNull((cr)->adobeOptions.setContentRoot(cr.toString()), options.getContentRoot());
		setIfNotNull((dd)->adobeOptions.setDebugDir(dd.toString()), options.getDebugDir());
		setIfNotNull(adobeOptions::setEmbedFonts, options.getEmbedFonts());
		setIfNotNull(adobeOptions::setLinearizedPDF, options.getLinearizedPDF());
		setIfNotNull((l)->adobeOptions.setLocale(l.toLanguageTag()), options.getLocale());
		setIfNotNull(adobeOptions::setRetainPDFFormState, options.getRetainPDFFormState());
		setIfNotNull(adobeOptions::setRetainUnsignedSignatureFields, options.getRetainUnsignedSignatureFields());
		setIfNotNull(adobeOptions::setTaggedPDF, options.getTaggedPDF());
		setIfNotNull((ad)->adobeOptions.setXci(AdobeDocumentFactoryImpl.getAdobeDocument(ad)), options.getXci());
		log.info("AcrobatVersion=" + adobeOptions.getAcrobatVersion().toString());
		log.info("ContentRoot=" + adobeOptions.getContentRoot());
		log.info("DebugDir=" + adobeOptions.getDebugDir());
		log.info("EmbedFonts=" + adobeOptions.getEmbedFonts());
		log.info("LinearizedPdf=" + adobeOptions.getLinearizedPDF());
		log.info("Locale=" + adobeOptions.getLocale());
		log.info("RetainPDFFormState=" + adobeOptions.getRetainPDFFormState());
		log.info("RetainUnsignedSignatureFields=" + adobeOptions.getRetainUnsignedSignatureFields());
		log.info("TaggedPdf=" + adobeOptions.getTaggedPDF());
		log.info("Xci is null=" + Boolean.toString(adobeOptions.getXci() == null));
		return adobeOptions;

	}
}
