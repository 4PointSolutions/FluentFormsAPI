package com._4point.aem.fluentforms.testing.assembler;

import java.util.Map;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSpec;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.api.assembler.AssemblerService.AssemblerServiceException;
import com._4point.aem.fluentforms.api.output.OutputService.OutputServiceException;
import com._4point.aem.fluentforms.impl.assembler.TraditionalDocAssemblerService;
import com.adobe.fd.assembler.client.OperationException;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec;
import com.adobe.fd.assembler.client.PDFAConversionResult;
import com.adobe.fd.assembler.client.PDFAValidationOptionSpec;
import com.adobe.fd.assembler.client.PDFAValidationResult;

public class ExceptionalMockTraditionalAssemblerService implements TraditionalDocAssemblerService {
	private final String message;

	private ExceptionalMockTraditionalAssemblerService(String message) {
		super();
		this.message = message;
	}


	@Override
	public AssemblerResult invoke(Document ddx, Map<String, Object> inputs,
			AssemblerOptionsSpec adobAssemblerOptionSpec) throws AssemblerServiceException, OperationException {
		throw new AssemblerServiceException(this.message);
  }

	@Override
	public PDFAValidationResult isPDFA(Document inDoc, PDFAValidationOptionSpec options) throws AssemblerServiceException {
		throw new AssemblerServiceException(this.message);	}

	@Override
	public PDFAConversionResult toPDFA(Document inDoc, PDFAConversionOptionSpec options) throws AssemblerServiceException {
		throw new AssemblerServiceException(this.message);	
		}
	
	public static ExceptionalMockTraditionalAssemblerService create(String message) {
		return new ExceptionalMockTraditionalAssemblerService(message);
	}


}
