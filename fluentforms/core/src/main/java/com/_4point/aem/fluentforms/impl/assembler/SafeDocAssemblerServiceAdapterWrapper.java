package com._4point.aem.fluentforms.impl.assembler;

import java.util.Map;
import java.util.Objects;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSpec;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.api.assembler.AssemblerService.AssemblerServiceException;
import com.adobe.fd.assembler.client.OperationException;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec;
import com.adobe.fd.assembler.client.PDFAConversionResult;
import com.adobe.fd.assembler.client.PDFAValidationOptionSpec;
import com.adobe.fd.assembler.client.PDFAValidationResult;


public class SafeDocAssemblerServiceAdapterWrapper implements TraditionalDocAssemblerService {
	private final TraditionalDocAssemblerService docAssemblerService;

	public SafeDocAssemblerServiceAdapterWrapper(TraditionalDocAssemblerService docAssemblerService) {
		super();
		this.docAssemblerService = docAssemblerService;
	}

	@Override
	public AssemblerResult invoke(Document ddx, Map<String, Object> sourceDocuments,
			AssemblerOptionsSpec adobAssemblerOptionSpec) throws AssemblerServiceException, OperationException {
		Objects.requireNonNull(ddx, "ddx Document cannot be null.");
		Objects.requireNonNull(sourceDocuments, "sourceDocuments cannot be null.");
		return docAssemblerService.invoke(ddx, sourceDocuments, adobAssemblerOptionSpec);

	}

	@Override
	public PDFAValidationResult isPDFA(Document inDoc, PDFAValidationOptionSpec options) {		
		return null;
	}

	@Override
	public PDFAConversionResult toPDFA(Document inDoc, PDFAConversionOptionSpec options) {		
		return null;
	}

}
