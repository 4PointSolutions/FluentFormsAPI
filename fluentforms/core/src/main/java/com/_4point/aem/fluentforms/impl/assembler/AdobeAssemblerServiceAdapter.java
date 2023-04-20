package com._4point.aem.fluentforms.impl.assembler;

import static com._4point.aem.fluentforms.impl.BuilderUtils.setIfNotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSpec;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.api.assembler.AssemblerService.AssemblerServiceException;
import com._4point.aem.fluentforms.api.assembler.PDFAConversionOptionSpec;
import com._4point.aem.fluentforms.api.assembler.PDFAConversionResult;
import com._4point.aem.fluentforms.api.assembler.PDFAValidationOptionSpec;
import com._4point.aem.fluentforms.api.assembler.PDFAValidationResult;
import com._4point.aem.fluentforms.impl.AdobeDocumentFactoryImpl;
import com.adobe.fd.assembler.client.ConversionException;
import com.adobe.fd.assembler.client.OperationException;
import com.adobe.fd.assembler.client.ValidationException;

public class AdobeAssemblerServiceAdapter implements TraditionalDocAssemblerService {

	private static final Logger log = LoggerFactory.getLogger(AdobeAssemblerServiceAdapter.class);

	private final com.adobe.fd.assembler.service.AssemblerService adobeDocAssemblerService;
    private final DocumentFactory documentFactory;
	
	public AdobeAssemblerServiceAdapter(com.adobe.fd.assembler.service.AssemblerService adobeDocAssemblerService, DocumentFactory documentFactory) {				
		super();
		this.adobeDocAssemblerService = Objects.requireNonNull(adobeDocAssemblerService,
				"adobeDocAssemblerService cannot be null.");		
		this.documentFactory = Objects.requireNonNull(documentFactory, "Document Factory cannot be null.");
	}
	
	public AdobeAssemblerServiceAdapter(com.adobe.fd.assembler.service.AssemblerService adobeDocAssemblerService) {		
		super();
		this.adobeDocAssemblerService = Objects.requireNonNull(adobeDocAssemblerService,
				"adobeDocAssemblerService cannot be null.");		
		this.documentFactory = DocumentFactory.getDefault();
	}
	
	@Override
	public AssemblerResult invoke(Document ddx, Map<String, Object> sourceDocuments,
			AssemblerOptionsSpec adobAssemblerOptionSpec) throws AssemblerServiceException {
		com.adobe.fd.assembler.client.AssemblerResult assemblerResult;
		try {
			assemblerResult = adobeDocAssemblerService.invoke(AdobeDocumentFactoryImpl.getAdobeDocument(ddx), toAdobeDocumentMap(sourceDocuments),
					toAdobeAssemblerOptionSpec(adobAssemblerOptionSpec));
		} catch (OperationException e) {
			 throw new AssemblerServiceException("Error while assembling the documents.", e);
		}
		return toAssemblerResult(assemblerResult);
	}
	
	
	private Map<String, Object> toAdobeDocumentMap(Map<String, Object> sourceDocuments) {
		Map<String, Object> input = new HashMap<String, Object>();
		sourceDocuments.forEach((docName, doc) -> {
			input.put(docName, AdobeDocumentFactoryImpl.getAdobeDocument((Document)doc));
		});
		return input;
	}

	@Override 
	public PDFAValidationResult isPDFA(Document inDoc, PDFAValidationOptionSpec options) throws AssemblerServiceException {
		try {
			return toPDFAValidationResult(adobeDocAssemblerService.isPDFA(AdobeDocumentFactoryImpl.getAdobeDocument(inDoc), toAdobePDFAValidationOptionSpec(options)));
		} catch (ValidationException e) {
			throw new AssemblerServiceException("Error while validating PDF/A document", e);
		} 
	}

	@Override 
	public PDFAConversionResult toPDFA(Document inDoc, PDFAConversionOptionSpec options) throws AssemblerServiceException { 
		try {
			return toPDFAConversionResult(adobeDocAssemblerService.toPDFA(AdobeDocumentFactoryImpl.getAdobeDocument(inDoc), toAdobePDFAConversionOptionSpec(options)));
		} catch (ConversionException e) {
			throw new AssemblerServiceException("Error while converting to PDF/A document", e);
		} 
	}
	
	public static com.adobe.fd.assembler.client.AssemblerOptionSpec toAdobeAssemblerOptionSpec(
			AssemblerOptionsSpec assemblerOptionSpec) {
		com.adobe.fd.assembler.client.AssemblerOptionSpec adobeAssemblerOptionSpec = new com.adobe.fd.assembler.client.AssemblerOptionSpec();
		setIfNotNull(adobeAssemblerOptionSpec::setFailOnError, assemblerOptionSpec.isFailOnError());
		setIfNotNull(adobeAssemblerOptionSpec::setDefaultStyle, assemblerOptionSpec.getDefaultStyle());
		setIfNotNull(adobeAssemblerOptionSpec::setFirstBatesNumber, assemblerOptionSpec.getFirstBatesNumber());
		setIfNotNull(adobeAssemblerOptionSpec::setLogLevel, assemblerOptionSpec.getLogLevel()!=null?assemblerOptionSpec.getLogLevel().toString():null);
		setIfNotNull(adobeAssemblerOptionSpec::setTakeOwnership, assemblerOptionSpec.isTakeOwnership());
		setIfNotNull(adobeAssemblerOptionSpec::setValidateOnly, assemblerOptionSpec.isValidateOnly());
		return adobeAssemblerOptionSpec;

	}

	private AssemblerResult toAssemblerResult(com.adobe.fd.assembler.client.AssemblerResult assemblerResult) {
		log.info("AdobeAssembler result to fluentForm assembler result");
		Map<String, Document> documents= new HashMap<String, Document>();
		if(isNotEmpty(assemblerResult.getDocuments())) {
			assemblerResult.getDocuments().forEach((docName, doc) -> {
				documents.put(docName, documentFactory.create(doc));
			});		
		}
		return new AssemblerResultImpl(
						documents, 	// documents
						assemblerResult.getFailedBlockNames(), 	// failedBlockNames
						assemblerResult.getJobLog()!=null ? documentFactory.create(assemblerResult.getJobLog()):null, 	// jobLog
						assemblerResult.getLastBatesNumber(), 		// lastBatesNumber
						assemblerResult.getMultipleResultsBlocks(), 	// multipleResultsBlocks
						assemblerResult.getNumRequestedBlocks(), 		// numRequestedBlocks
						assemblerResult.getSuccessfulDocumentNames(), 	// successfulBlockNames
						assemblerResult.getSuccessfulBlockNames(), 	// successfulDocumentNames
						assemblerResult.getThrowables()	// throwables 
						);
	}

	private com.adobe.fd.assembler.client.PDFAValidationOptionSpec toAdobePDFAValidationOptionSpec(PDFAValidationOptionSpec options) {
		com.adobe.fd.assembler.client.PDFAValidationOptionSpec adobeOptions = new com.adobe.fd.assembler.client.PDFAValidationOptionSpec();
		setIfNotNull(adobeOptions::setAllowCertificationSignatures, options.isAllowCertificationSignatures());
		setIfNotNull(adobeOptions::setCompliance, options.getCompliance());
		setIfNotNull(adobeOptions::setIgnoreUnusedResource, options.isIgnoreUnusedResource());
		setIfNotNull(adobeOptions::setLogLevel, options.getLogLevel().toString());
		setIfNotNull(adobeOptions::setResultLevel, options.getResultLevel());
		return adobeOptions ;
	}

	private PDFAValidationResult toPDFAValidationResult(com.adobe.fd.assembler.client.PDFAValidationResult result) {
		return new PDFAValidationResultImpl(documentFactory.create(result.getJobLog()), documentFactory.create(result.getValidationLog()), result.isPDFA());
	}

	private com.adobe.fd.assembler.client.PDFAConversionOptionSpec toAdobePDFAConversionOptionSpec(PDFAConversionOptionSpec options) {
		com.adobe.fd.assembler.client.PDFAConversionOptionSpec adobeOptions = new com.adobe.fd.assembler.client.PDFAConversionOptionSpec();
		setIfNotNull(adobeOptions::setColorSpace, options.getColorSpace());
		setIfNotNull(adobeOptions::setCompliance, options.getCompliance());
		setIfNotNull(adobeOptions::setLogLevel, options.getLogLevel().toString());
		setIfNotNull(adobeOptions::setOptionalContent, options.getOptionalContent());
		setIfNotNull(adobeOptions::setRemoveInvalidXMPProperties, options.isRemoveInvalidXMPProperties());
		setIfNotNull(adobeOptions::setResultLevel, options.getResultLevel());
		setIfNotNull(adobeOptions::setRetainPDFFormState, options.isRetainPDFFormState());
		setIfNotNull(adobeOptions::setSignatures, options.getSignatures());
		setIfNotNull(adobeOptions::setVerify, options.isVerify());
		final List<Document> ext = options.getMetadataSchemaExtensions();
		if(isNotEmpty(ext)) {
			adobeOptions.setMetadataSchemaExtensions(ext.stream()
														.map(AdobeDocumentFactoryImpl::getAdobeDocument)	// Transform to list of Adobe docs
														.collect(Collectors.toList())
													);
		}
		return adobeOptions;
	}

	private PDFAConversionResult toPDFAConversionResult(com.adobe.fd.assembler.client.PDFAConversionResult pdfaResult) {
		return new PDFAConversionResultImpl(
				documentFactory.create(pdfaResult.getConversionLog()), 	// conversionLog
				documentFactory.create(pdfaResult.getJobLog()), 		// jobLog
				documentFactory.create(pdfaResult.getConversionLog()), 	// pdfADocument
				pdfaResult.isPDFA()										// isPDFA
				);
	}
	private static <E> boolean isNotEmpty(Collection<E> c) { return c != null && !c.isEmpty(); }
	private static <K,V> boolean isNotEmpty(Map<K,V> m) { return m != null && !m.isEmpty(); }
}
