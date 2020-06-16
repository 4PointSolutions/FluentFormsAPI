package com._4point.aem.fluentforms.impl.assembler;

import java.util.Map;
import java.util.Objects;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSpec;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.api.assembler.AssemblerService;
import com._4point.aem.fluentforms.impl.UsageContext;
import com.adobe.fd.assembler.client.ConversionException;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec;
import com.adobe.fd.assembler.client.PDFAConversionResult;
import com.adobe.fd.assembler.client.PDFAValidationOptionSpec;
import com.adobe.fd.assembler.client.PDFAValidationResult;
import com.adobe.fd.assembler.client.ValidationException;



public class AssemblerServiceImpl implements AssemblerService {
	private final TraditionalDocAssemblerService adobDocAssemblerService;
	private final UsageContext usageContext;
	public AssemblerServiceImpl(TraditionalDocAssemblerService adobDocAssemblerService,UsageContext usageContext) {
		super();
		this.adobDocAssemblerService = new SafeDocAssemblerServiceAdapterWrapper(adobDocAssemblerService);
		this.usageContext = usageContext;
	}
	
	@Override
	public AssemblerResult invoke(Document ddx, Map<String, Object> sourceDocuments, AssemblerOptionsSpec assemblerOptionSpec)
			throws AssemblerServiceException {
		// TODO Auto-generated method stub
		Objects.requireNonNull(ddx, "ddx Document cannot be null.");
		Objects.requireNonNull(sourceDocuments, "sourceDocuments cannot be null.");
		try {
			return adobDocAssemblerService.invoke(ddx, sourceDocuments, assemblerOptionSpec);
		} catch (Exception e) {
			throw new AssemblerServiceException(e);
		}
		//return null;
	}

	

	private class pdfAssemblerArgumentBuilderImpl implements pdfAssemblerArgumentBuilder {

		AssemblerOptionsSpec assemblerOptionsSpec = new AssemblerOptionsSpecImpl();
		@Override
		public pdfAssemblerArgumentBuilder setFailOnError(boolean isFailOnError) {
	      this.assemblerOptionsSpec.setFailOnError(isFailOnError);
			return this;
		}	
		
	}

}
