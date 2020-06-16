package com._4point.aem.fluentforms.api.assembler;

import java.util.Map;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.Transformable;
import com._4point.aem.fluentforms.api.output.PDFOutputOptionsSetter;
import com._4point.aem.fluentforms.api.output.OutputService.GeneratePdfOutputArgumentBuilder;
import com.adobe.fd.assembler.client.ConversionException;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec;
import com.adobe.fd.assembler.client.PDFAConversionResult;
import com.adobe.fd.assembler.client.PDFAValidationOptionSpec;
import com.adobe.fd.assembler.client.PDFAValidationResult;
import com.adobe.fd.assembler.client.ValidationException;

public interface AssemblerService {
	AssemblerResult invoke(Document ddx, Map<String,Object> inputs, AssemblerOptionsSpec assemblerOptionSpec) throws AssemblerServiceException; 


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
   
	
	public static interface pdfAssemblerArgumentBuilder extends AssemblerOptionsSetter, Transformable<pdfAssemblerArgumentBuilder> {
			
		@Override
		pdfAssemblerArgumentBuilder setFailOnError(boolean isFailOnError);
	}
}
