package com._4point.aem.fluentforms.api.assembler;

import java.net.URL;
import java.nio.file.Path;
import java.util.Map;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.api.Transformable;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec;
import com.adobe.fd.assembler.client.PDFAConversionResult;
import com.adobe.fd.assembler.client.PDFAValidationOptionSpec;
import com.adobe.fd.assembler.client.PDFAValidationResult;
public interface AssemblerService {
	
	AssemblerResult invoke(Document ddx, Map<String,Object> sourceDocuments, AssemblerOptionsSpec assemblerOptionSpec) throws AssemblerServiceException; 

	
	PDFAValidationResult isPDFA(Document inDoc, PDFAValidationOptionSpec options) throws AssemblerServiceException;
    
    PDFAConversionResult toPDFA(Document inDoc, PDFAConversionOptionSpec options) throws AssemblerServiceException;

	AssemblerArgumentBuilder invoke();
	
	@SuppressWarnings("serial")
	public static class AssemblerServiceException extends Exception {

		public AssemblerServiceException() {
			super();
		}

		public AssemblerServiceException(String message, Throwable cause) {
			super(message, cause);
		}

		public AssemblerServiceException(String message) {
			super(message);
		}

        public AssemblerServiceException(Throwable cause) {
			super(cause);
		}
	}
   
	
	public static interface AssemblerArgumentBuilder extends AssemblerOptionsSetter, Transformable<AssemblerArgumentBuilder> {
		
		@Override
		AssemblerArgumentBuilder setFailOnError(boolean isFailOnError);
		
		@Override
		AssemblerArgumentBuilder setContentRoot(PathOrUrl contentRoot);

		@Override
		default AssemblerArgumentBuilder setContentRoot(Path contentRoot) {
			AssemblerOptionsSetter.super.setContentRoot(contentRoot);
			return this;
		}

		@Override
		default AssemblerArgumentBuilder setContentRoot(URL contentRoot) {
			AssemblerOptionsSetter.super.setContentRoot(contentRoot);
			return this;
		}
       
		public AssemblerResult executeOn(Document ddx, Map<String,Object>sourceDocuments) throws AssemblerServiceException;
        	
	}

}
