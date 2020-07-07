package com._4point.aem.fluentforms.testing.assembler;

import java.util.Map;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSpec;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.api.assembler.AssemblerService.AssemblerServiceException;
import com._4point.aem.fluentforms.impl.assembler.TraditionalDocAssemblerService;
import com.adobe.fd.assembler.client.OperationException;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec;
import com.adobe.fd.assembler.client.PDFAConversionResult;
import com.adobe.fd.assembler.client.PDFAValidationOptionSpec;
import com.adobe.fd.assembler.client.PDFAValidationResult;

public class MockTraditionalAssemblerService implements TraditionalDocAssemblerService {

	@Override
	public AssemblerResult invoke(Document ddx, Map<String, Object> inputs,
			AssemblerOptionsSpec adobAssemblerOptionSpec) throws AssemblerServiceException, OperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PDFAValidationResult isPDFA(Document inDoc, PDFAValidationOptionSpec options)
			throws AssemblerServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PDFAConversionResult toPDFA(Document inDoc, PDFAConversionOptionSpec options)
			throws AssemblerServiceException {
		// TODO Auto-generated method stub
		return null;
	}
	}
