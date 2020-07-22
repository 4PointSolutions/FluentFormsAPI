package com._4point.aem.fluentforms.impl.assembler;

import static com._4point.aem.fluentforms.impl.BuilderUtils.setIfNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSpec;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.api.assembler.AssemblerService.AssemblerServiceException;
import com._4point.aem.fluentforms.impl.AdobeDocumentFactoryImpl;
import com.adobe.fd.assembler.client.OperationException;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec;
import com.adobe.fd.assembler.client.PDFAConversionResult;
import com.adobe.fd.assembler.client.PDFAValidationOptionSpec;
import com.adobe.fd.assembler.client.PDFAValidationResult;

public class AdobeDocAssemblerServiceAdapter implements TraditionalDocAssemblerService {

	private static final Logger log = LoggerFactory.getLogger(AdobeDocAssemblerServiceAdapter.class);

	private final com.adobe.fd.assembler.service.AssemblerService adobeDocAssemblerService;
	private final DocumentFactory documentFactory;

	public AdobeDocAssemblerServiceAdapter(com.adobe.fd.assembler.service.AssemblerService adobeDocAssemblerService,
			DocumentFactory documentFactory) {
		super();
		this.adobeDocAssemblerService = Objects.requireNonNull(adobeDocAssemblerService,
				"adobeDocAssemblerService cannot be null.");

		this.documentFactory = Objects.requireNonNull(documentFactory, "Document Factory cannot be null.");
	}

	public AdobeDocAssemblerServiceAdapter(com.adobe.fd.assembler.service.AssemblerService adobeDocAssemblerService) {
		super();
		this.documentFactory = DocumentFactory.getDefault();
		this.adobeDocAssemblerService = Objects.requireNonNull(adobeDocAssemblerService,
				"adobeDocAssemblerService cannot be null.");
		;
	}

	@Override
	public AssemblerResult invoke(Document ddx, Map<String, Object> souceDocuments,
			AssemblerOptionsSpec adobAssemblerOptionSpec) throws AssemblerServiceException, OperationException {
		com.adobe.fd.assembler.client.AssemblerResult assemblerResult = adobeDocAssemblerService.invoke(
				AdobeDocumentFactoryImpl.getAdobeDocument(ddx), toAdobeMapOfDocuments(souceDocuments),
				toAdobeAssemblerOptionSpec(adobAssemblerOptionSpec));
		return toAssemblerResult(assemblerResult);
	}

	private Map<String, Object> toAdobeMapOfDocuments(Map<String, Object> souceDocuments) {
		Map<String, Object> sourceDoc = new HashMap<String, Object>();
		if (MapUtils.isNotEmpty(souceDocuments)) {
			souceDocuments.forEach((docName, doc) -> {
				sourceDoc.put(docName, AdobeDocumentFactoryImpl.getAdobeDocument((Document) doc));
			});
		}
		return sourceDoc;
	}

	@Override
	public PDFAValidationResult isPDFA(Document inDoc, PDFAValidationOptionSpec options) {
		return null;
	}

	@Override
	public PDFAConversionResult toPDFA(Document inDoc, PDFAConversionOptionSpec options) {
		return null;
	}

	static com.adobe.fd.assembler.client.AssemblerOptionSpec toAdobeAssemblerOptionSpec(
			AssemblerOptionsSpec assemblerOptionSpec) {
		com.adobe.fd.assembler.client.AssemblerOptionSpec adobeAssemblerOptionSpec = new com.adobe.fd.assembler.client.AssemblerOptionSpec();
		setIfNotNull(adobeAssemblerOptionSpec::setFailOnError, assemblerOptionSpec.isFailOnError());
		setIfNotNull(adobeAssemblerOptionSpec::setDefaultStyle, assemblerOptionSpec.getDefaultStyle());
		setIfNotNull(adobeAssemblerOptionSpec::setFirstBatesNumber, assemblerOptionSpec.getFirstBatesNumber());
		setIfNotNull(adobeAssemblerOptionSpec::setLogLevel, assemblerOptionSpec.getLogLevel());
		setIfNotNull(adobeAssemblerOptionSpec::setTakeOwnership, assemblerOptionSpec.isTakeOwnership());
		setIfNotNull(adobeAssemblerOptionSpec::setValidateOnly, assemblerOptionSpec.isValidateOnly());
		return adobeAssemblerOptionSpec;

	}

	private AssemblerResult toAssemblerResult(com.adobe.fd.assembler.client.AssemblerResult assemblerResult) {
		log.info("Adobe assembler result to fluent form assembler result");
		AssemblerResultImpl assemblerResultImpl = new AssemblerResultImpl();
		assemblerResultImpl.setFailedBlockNames(assemblerResult.getFailedBlockNames());
		assemblerResultImpl.setJobLog(documentFactory.create(assemblerResult.getJobLog()));
		assemblerResultImpl.setLastBatesNumber(assemblerResult.getLastBatesNumber());
		assemblerResultImpl.setMultipleResultsBlocks(assemblerResult.getMultipleResultsBlocks());
		assemblerResultImpl.setNumRequestedBlocks(assemblerResult.getNumRequestedBlocks());
		assemblerResultImpl.setSuccessfulDocumentNames(assemblerResult.getSuccessfulBlockNames());
		assemblerResultImpl.setThrowables(assemblerResult.getThrowables());
		assemblerResultImpl.setSuccessfulBlockNames(assemblerResult.getSuccessfulBlockNames());
		 Map<String, Document> documents= new HashMap<String, Document>();
		if(MapUtils.isNotEmpty(assemblerResult.getDocuments())) {
			assemblerResult.getDocuments().forEach((docName, doc) -> {
				 documents.put(docName, documentFactory.create(doc));
			});		
		}
		assemblerResultImpl.setDocuments(documents);
		return assemblerResultImpl;
		
	}

}
