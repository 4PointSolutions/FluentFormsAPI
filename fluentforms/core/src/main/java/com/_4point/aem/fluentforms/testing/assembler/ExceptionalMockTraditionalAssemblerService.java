package com._4point.aem.fluentforms.testing.assembler;

import java.util.Map;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSpec;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.api.assembler.AssemblerService.AssemblerServiceException;
import com._4point.aem.fluentforms.api.assembler.PDFAConversionOptionSpec;
import com._4point.aem.fluentforms.api.assembler.PDFAConversionResult;
import com._4point.aem.fluentforms.api.assembler.PDFAValidationOptionSpec;
import com._4point.aem.fluentforms.api.assembler.PDFAValidationResult;
import com._4point.aem.fluentforms.impl.assembler.TraditionalDocAssemblerService;

public class ExceptionalMockTraditionalAssemblerService implements TraditionalDocAssemblerService {
	private final String message;

	private ExceptionalMockTraditionalAssemblerService(String message) {
		this.message = message;
	}

	@Override
	public AssemblerResult invoke(Document ddx, Map<String, Object> inputs,
			AssemblerOptionsSpec adobAssemblerOptionSpec) throws AssemblerServiceException {
		throw new AssemblerServiceException(this.message);
	}

	@Override
	public PDFAValidationResult isPDFA(Document inDoc, PDFAValidationOptionSpec options) throws AssemblerServiceException {
		throw new AssemblerServiceException(this.message);
	}

	@Override
	public PDFAConversionResult toPDFA(Document inDoc, PDFAConversionOptionSpec options) throws AssemblerServiceException {
		throw new AssemblerServiceException(this.message);
	}
	
	public static ExceptionalMockTraditionalAssemblerService create(String message) {
		return new ExceptionalMockTraditionalAssemblerService(message);
	}
}
