package com._4point.aem.fluentforms.impl.assembler;

import static com._4point.aem.fluentforms.impl.BuilderUtils.setIfNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

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

public class AdobeDocAssemblerServiceAdapter implements TraditionalDocAssemblerService, AssemblerResult {

	private static final Logger log = LoggerFactory.getLogger(AdobeDocAssemblerServiceAdapter.class);

	private final com.adobe.fd.assembler.service.AssemblerService adobeDocAssemblerService;
    private  com.adobe.fd.assembler.client.AssemblerResult assemblerResult;
    private final DocumentFactory documentFactory;
    private Map<String, Document> sourceDocuments ;

	public AdobeDocAssemblerServiceAdapter(com.adobe.fd.assembler.service.AssemblerService adobeDocAssemblerService, DocumentFactory documentFactory) {		
		super();
		this.adobeDocAssemblerService = Objects.requireNonNull(adobeDocAssemblerService,
				"adobeDocAssemblerService cannot be null.");		
		this.assemblerResult =null;
		
		this.documentFactory = Objects.requireNonNull(documentFactory, "Document Factory cannot be null.");
		this.sourceDocuments = null;
	}


	public AdobeDocAssemblerServiceAdapter(com.adobe.fd.assembler.service.AssemblerService adobeDocAssemblerService) {
		super();
		this.documentFactory = DocumentFactory.getDefault();
		
		this.adobeDocAssemblerService = Objects.requireNonNull(adobeDocAssemblerService,
				"adobeDocAssemblerService cannot be null.");	;
		this.assemblerResult = null;
		this.sourceDocuments = null;
	}
	

	public AdobeDocAssemblerServiceAdapter(Map<String, Document> sourceDocuments) {
		super();
		log.info("initializing docs in AdobeDocAssemblerServiceAdapter");
	    this.documentFactory = DocumentFactory.getDefault();
		this.adobeDocAssemblerService =null;
		this.assemblerResult = null;
		this.sourceDocuments = sourceDocuments;
	}
	
	public AdobeDocAssemblerServiceAdapter(com.adobe.fd.assembler.client.AssemblerResult assemblerResult) {
		super();
		this.documentFactory = DocumentFactory.getDefault();
		this.adobeDocAssemblerService = null;
		this.assemblerResult = assemblerResult;
		this.sourceDocuments = null;
	}
	
	
	

	@Override
	public AssemblerResult invoke(Document ddx, Map<String, Object> souceDocuments,
			AssemblerOptionsSpec adobAssemblerOptionSpec) throws AssemblerServiceException, OperationException {
		com.adobe.fd.assembler.client.AssemblerResult assemblerResult = adobeDocAssemblerService.invoke(AdobeDocumentFactoryImpl.getAdobeDocument(ddx), toAdobeMapOfDocuments(souceDocuments),
				toAdobeAssemblerOptionSpec(adobAssemblerOptionSpec));
		
		return toAssemblerResult(assemblerResult);
	}
	
	
	private Map<String, Object> toAdobeMapOfDocuments(Map<String, Object> souceDocuments) {
		Map<String, Object> sourceDoc = new HashMap<String, Object>();
		souceDocuments.forEach((docName, doc) -> {
			sourceDoc.put(docName, AdobeDocumentFactoryImpl.getAdobeDocument((Document)doc));
		});
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
		/**
		 * to instruct the Assembler service to continue processing a job when an error
		 * occurs, invoke the AssemblerOptionSpec object’s setFailOnError method and
		 * pass false.
		 */
		setIfNotNull(adobeAssemblerOptionSpec::setFailOnError, assemblerOptionSpec.isFailOnError());
		log.info("FailonError= " + assemblerOptionSpec.isFailOnError());
		return adobeAssemblerOptionSpec;

	}	

	static AssemblerResult toAssemblerResult(com.adobe.fd.assembler.client.AssemblerResult assemblerResult) {
		return new AdobeDocAssemblerServiceAdapter(assemblerResult);
	}

	@Override
	public Map<String, Document> getDocuments() {

if(sourceDocuments==null) {
	log.info("sourceDocuments empty");
	sourceDocuments =  new HashMap<String, Document>();
		if (assemblerResult != null) {

			if (assemblerResult.getDocuments() != null) {
				log.info("assemblerResult is not empty");
				assemblerResult.getDocuments().forEach((docName, doc) -> {
					sourceDocuments.put(docName, documentFactory.create(doc));
				});

			} else {
				log.error("dcoument map null");
			}
		}
}
log.info("sourceDocuments not empty");
		return sourceDocuments;
	}

	@Override
	public void close() throws Exception {
		
	}

	@Override
	public List<String> getFailedBlockNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document etJobLog() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getLastBatesNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Map<String, List<String>> getMultipleResultsBlocks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumRequestedBlocks() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<String> getSuccessfulBlockNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getSuccessfulDocumentNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, OperationException> getThrowables() {
		// TODO Auto-generated method stub
		return null;
	}

}
