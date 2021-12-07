package com._4point.aem.fluentforms.impl.output;

import static com._4point.aem.fluentforms.impl.BuilderUtils.setIfNotNull;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.output.BatchOptions;
import com._4point.aem.fluentforms.api.output.BatchResult;
import com._4point.aem.fluentforms.api.output.OutputService.OutputServiceException;
import com._4point.aem.fluentforms.api.output.PDFOutputOptions;
import com._4point.aem.fluentforms.api.output.PrintConfig;
import com._4point.aem.fluentforms.api.output.PrintedOutputOptions;
import com._4point.aem.fluentforms.impl.AdobeDocumentFactoryImpl;

public class AdobeOutputServiceAdapter implements TraditionalOutputService {

	private static final Logger log = LoggerFactory.getLogger(AdobeOutputServiceAdapter.class);
	private final DocumentFactory documentFactory;
	private final com.adobe.fd.output.api.OutputService adobeOutputService;

	public AdobeOutputServiceAdapter(com.adobe.fd.output.api.OutputService adobeOutputService) {
		super();
		this.documentFactory = DocumentFactory.getDefault();
		this.adobeOutputService = Objects.requireNonNull(adobeOutputService, "Adobe Output Service cannot be null.");
	}

	public AdobeOutputServiceAdapter(com.adobe.fd.output.api.OutputService adobeOutputService, DocumentFactory documentFactory) {
		super();
		this.documentFactory = Objects.requireNonNull(documentFactory, "Document Factory cannot be null.");
		this.adobeOutputService = Objects.requireNonNull(adobeOutputService, "Adobe Output Service cannot be null.");
	}

	@Override
	public Document generatePDFOutput(Document template, Document data, PDFOutputOptions pdfOutputOptions) throws OutputServiceException {
		try {
			Document resultDoc = documentFactory.create(adobeOutputService.generatePDFOutput(AdobeDocumentFactoryImpl.getAdobeDocument(template), AdobeDocumentFactoryImpl.getAdobeDocument(data), toAdobePDFOutputOptions(pdfOutputOptions)));
			resultDoc.setContentType(Document.CONTENT_TYPE_PDF);	// Originally we were doing a setContentTypeIfEmpty() but that fails because of FluentFormsAPI Issue #15
			return resultDoc;
		} catch (com.adobe.fd.output.api.OutputServiceException e) {
			throw new OutputServiceException(e);
		}
	}

	@Override
	public Document generatePDFOutput(String urlOrFileName, Document data, PDFOutputOptions pdfOutputOptions) throws OutputServiceException {
		try {
			log.info("generatePDFOutput form='" + urlOrFileName + "', contentRoot='" + pdfOutputOptions.getContentRoot() + "'");
			Document resultDoc = documentFactory.create(adobeOutputService.generatePDFOutput(urlOrFileName, AdobeDocumentFactoryImpl.getAdobeDocument(data), toAdobePDFOutputOptions(pdfOutputOptions)));
			resultDoc.setContentType(Document.CONTENT_TYPE_PDF);	// Originally we were doing a setContentTypeIfEmpty() but that fails because of FluentFormsAPI Issue #15
			return resultDoc;
		} catch (com.adobe.fd.output.api.OutputServiceException e) {
			throw new OutputServiceException(e);
		}
	}

	@Override
	public BatchResult generatePDFOutputBatch(Map<String, String> templates, Map<String, Document> data, PDFOutputOptions pdfOutputOptions, BatchOptions batchOptions)
			throws OutputServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document generatePrintedOutput(Document template, Document data, PrintedOutputOptions printedOutputOptions) throws OutputServiceException {
		try {
			return documentFactory.create(adobeOutputService.generatePrintedOutput(AdobeDocumentFactoryImpl.getAdobeDocument(template), AdobeDocumentFactoryImpl.getAdobeDocument(data), toAdobePrintedOutputOptions(printedOutputOptions)));
		} catch (com.adobe.fd.output.api.OutputServiceException e) {
			throw new OutputServiceException(e);
		}
	}

	@Override
	public Document generatePrintedOutput(String urlOrFileName, Document data, PrintedOutputOptions printedOutputOptions) throws OutputServiceException {
		try {
			log.info("generatePrintedOutput form='" + urlOrFileName + "', contentRoot='" + printedOutputOptions.getContentRoot() + "'");
			return documentFactory.create(adobeOutputService.generatePrintedOutput(urlOrFileName, AdobeDocumentFactoryImpl.getAdobeDocument(data), toAdobePrintedOutputOptions(printedOutputOptions)));
		} catch (com.adobe.fd.output.api.OutputServiceException e) {
			throw new OutputServiceException(e);
		}
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
		setIfNotNull((cr)->adobeOptions.setContentRoot(
				// Compensate for the fact that AEM doesn't handle relative URLs in the content root because XMLForm.exe uses a different cwd than AEM.
				// See issue #31 for details.
				cr.isPath() ? cr.getPath().toAbsolutePath().toString() : cr.toString()), 
				options.getContentRoot())
		;
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

	// Package visibility so that it can be used in unit testing.
	/* package */ static com.adobe.fd.output.api.PrintedOutputOptions toAdobePrintedOutputOptions(PrintedOutputOptions options) {
		com.adobe.fd.output.api.PrintedOutputOptions adobeOptions = new com.adobe.fd.output.api.PrintedOutputOptions();
		setIfNotNull((cr)->adobeOptions.setContentRoot(
				// Compensate for the fact that AEM doesn't handle relative URLs in the content root because XMLForm.exe uses a different cwd than AEM.
				// See issue #31 for details.
				cr.isPath() ? cr.getPath().toAbsolutePath().toString() : cr.toString()), 
				options.getContentRoot()
				);
		setIfNotNull(adobeOptions::setCopies, options.getCopies());
		setIfNotNull((dd)->adobeOptions.setDebugDir(dd.toString()), options.getDebugDir());
		setIfNotNull((l)->adobeOptions.setLocale(l.toLanguageTag()), options.getLocale());
		setIfNotNull(adobeOptions::setPaginationOverride, options.getPaginationOverride());
		setIfNotNull(adobeOptions::setPrintConfig, toAdobePrintConfig(options.getPrintConfig()));
		setIfNotNull((ad)->adobeOptions.setXci(AdobeDocumentFactoryImpl.getAdobeDocument(ad)), options.getXci());
		log.info("ContentRoot=" + adobeOptions.getContentRoot());
		log.info("Copies=" + adobeOptions.getCopies());
		log.info("DebugDir=" + adobeOptions.getDebugDir());
		log.info("Locale=" + adobeOptions.getLocale());
		log.info("PaginationOverride=" + adobeOptions.getPaginationOverride());
		log.info("PrintConfig=" + adobeOptions.getPrintConfig());
		log.info("Xci is null=" + Boolean.toString(adobeOptions.getXci() == null));
		
		return adobeOptions;
	}
	
	// Package visibility so that it can be unit tested.
	/* package */ enum PrintConfigMapping {
		DPL300(PrintConfigImpl.DPL300, com.adobe.fd.output.api.PrintConfig.DPL300),
		DPL406(PrintConfigImpl.DPL406, com.adobe.fd.output.api.PrintConfig.DPL406),
		DPL600(PrintConfigImpl.DPL600, com.adobe.fd.output.api.PrintConfig.DPL600),
		Generic_PS_L3(PrintConfigImpl.Generic_PS_L3, com.adobe.fd.output.api.PrintConfig.Generic_PS_L3),
		GenericColor_PCL_5c(PrintConfigImpl.GenericColor_PCL_5c, com.adobe.fd.output.api.PrintConfig.GenericColor_PCL_5c),
		HP_PCL_5e(PrintConfigImpl.HP_PCL_5e, com.adobe.fd.output.api.PrintConfig.HP_PCL_5e),
		IPL300(PrintConfigImpl.IPL300, com.adobe.fd.output.api.PrintConfig.IPL300),
		IPL400(PrintConfigImpl.IPL400, com.adobe.fd.output.api.PrintConfig.IPL400),
		PS_PLAIN(PrintConfigImpl.PS_PLAIN, com.adobe.fd.output.api.PrintConfig.PS_PLAIN),
		TPCL305(PrintConfigImpl.TPCL305, com.adobe.fd.output.api.PrintConfig.TPCL305),
		TPCL600(PrintConfigImpl.TPCL600, com.adobe.fd.output.api.PrintConfig.TPCL600),
		ZPL300(PrintConfigImpl.ZPL300, com.adobe.fd.output.api.PrintConfig.ZPL300),
		ZPL600(PrintConfigImpl.ZPL600, com.adobe.fd.output.api.PrintConfig.ZPL600);
		
		private final PrintConfig fluentformsConfig;
		private final com.adobe.fd.output.api.PrintConfig adobeConfig;
		
		private PrintConfigMapping(PrintConfig fluentformsConfig, com.adobe.fd.output.api.PrintConfig adobeConfig) {
			this.fluentformsConfig = fluentformsConfig;
			this.adobeConfig = adobeConfig;
		}
		
		public static Optional<com.adobe.fd.output.api.PrintConfig> from(PrintConfig config) {
			for(PrintConfigMapping underTest : PrintConfigMapping.values()) {
				if (underTest.fluentformsConfig == config) {
					return Optional.of(underTest.adobeConfig);
				}
			}
			return Optional.empty();
		}
	}
	
	// Package visibility so that it can be used in unit testing.
	/* package */ static com.adobe.fd.output.api.PrintConfig toAdobePrintConfig(PrintConfig config) {
		if (config == null) {
			return null;
		}
		return PrintConfigMapping.from(config).orElse(com.adobe.fd.output.api.PrintConfig.Custom(config.getXdcUri().toString(), config.getRenderType()));
	}
}
