package com._4point.aem.fluentforms.impl.assembler;

import java.util.Map;
import java.util.Objects;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSpec;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.api.assembler.AssemblerService.AssemblerServiceException;
import com.adobe.fd.assembler.client.ConversionException;
import com.adobe.fd.assembler.client.OperationException;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec;
import com.adobe.fd.assembler.client.PDFAConversionResult;
import com.adobe.fd.assembler.client.PDFAValidationOptionSpec;
import com.adobe.fd.assembler.client.PDFAValidationResult;
import com.adobe.fd.assembler.client.ValidationException;


public class SafeAssemblerServiceAdapterWrapper implements TraditionalDocAssemblerService {
	private final TraditionalDocAssemblerService assemblerService;

	public SafeAssemblerServiceAdapterWrapper(TraditionalDocAssemblerService docAssemblerService) {
		super();
		this.assemblerService = docAssemblerService;
	}

	@Override
	public AssemblerResult invoke(Document ddx, Map<String, Object> sourceDocuments,
			AssemblerOptionsSpec adobAssemblerOptionSpec) throws AssemblerServiceException, OperationException {
		Objects.requireNonNull(ddx, "ddx Document cannot be null.");
		Objects.requireNonNull(sourceDocuments, "sourceDocuments can not be null.");
		return assemblerService.invoke(ddx, sourceDocuments, adobAssemblerOptionSpec);

	}

	@Override
	public PDFAValidationResult isPDFA(Document inDoc, PDFAValidationOptionSpec pdfaValidationOptionSpec) throws AssemblerServiceException, ValidationException {	
		Objects.requireNonNull(inDoc, "inDoc Document cannot be null.");
		Objects.requireNonNull(pdfaValidationOptionSpec, "pdfaValidationOptionSpec ca not be null.");
		return assemblerService.isPDFA(inDoc, pdfaValidationOptionSpec);
	}

	@Override
	public PDFAConversionResult toPDFA(Document inDoc, PDFAConversionOptionSpec pdfaConversionOptionSpec) throws AssemblerServiceException, ConversionException {
		Objects.requireNonNull(inDoc, "ddx Document cannot be null.");
		Objects.requireNonNull(pdfaConversionOptionSpec, "pdfaConversionOptionSpec can not be null.");
		return assemblerService.toPDFA(inDoc, pdfaConversionOptionSpec);
	}
    
	// This is required by the mock services.
    public TraditionalDocAssemblerService getAssemblerService() {
			return assemblerService;
    }

}
