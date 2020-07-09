package com._4point.aem.fluentforms.impl.assembler;

import java.util.Map;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSpec;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.api.assembler.AssemblerService.AssemblerServiceException;


public interface TraditionalDocAssemblerService {

	AssemblerResult invoke(Document ddx, Map<String, Object> inputs,
			AssemblerOptionsSpec adobAssemblerOptionSpec) throws AssemblerServiceException; 
	/*
	 * PDFAValidationResult isPDFA(Document inDoc, PDFAValidationOptionSpec options)
	 * throws AssemblerServiceException, ValidationException;
	 * 
	 * PDFAConversionResult toPDFA(Document inDoc, PDFAConversionOptionSpec options)
	 * throws AssemblerServiceException, ConversionException;
	 */
}
