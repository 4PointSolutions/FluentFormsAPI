package com._4point.aem.fluentforms.impl.assembler;

import java.util.Map;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSpec;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.api.assembler.AssemblerService.AssemblerServiceException;
import com.adobe.fd.assembler.client.OperationException;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec;
import com.adobe.fd.assembler.client.PDFAConversionResult;
import com.adobe.fd.assembler.client.PDFAValidationOptionSpec;
import com.adobe.fd.assembler.client.PDFAValidationResult;


public interface TraditionalDocAssemblerService {

	AssemblerResult invoke(Document ddx, Map<String, Object> inputs,
			AssemblerOptionsSpec adobAssemblerOptionSpec) throws AssemblerServiceException, OperationException; 
	
    PDFAValidationResult isPDFA(Document inDoc, PDFAValidationOptionSpec options) throws AssemblerServiceException;
    
    PDFAConversionResult toPDFA(Document inDoc, PDFAConversionOptionSpec options) throws AssemblerServiceException;

}
