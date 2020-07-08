package com._4point.aem.fluentforms.impl.assembler;

import java.util.Map;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSpec;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.api.assembler.AssemblerService;
import com._4point.aem.fluentforms.impl.UsageContext;
import com.adobe.fd.assembler.client.ConversionException;
import com.adobe.fd.assembler.client.OperationException;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec;
import com.adobe.fd.assembler.client.PDFAConversionResult;
import com.adobe.fd.assembler.client.PDFAValidationOptionSpec;
import com.adobe.fd.assembler.client.PDFAValidationResult;
import com.adobe.fd.assembler.client.ValidationException;

public class AssemblerServiceImpl implements AssemblerService {
	private final TraditionalDocAssemblerService adobDocAssemblerService;

	public AssemblerServiceImpl(TraditionalDocAssemblerService adobDocAssemblerService, UsageContext usageContext) {
		super();
		this.adobDocAssemblerService = new SafeAssemblerServiceAdapterWrapper(adobDocAssemblerService);
	}

	@Override
	public AssemblerResult invoke(Document ddx, Map<String, Object> sourceDocuments,
			AssemblerOptionsSpec assemblerOptionSpec) throws AssemblerServiceException {
		try {
			return adobDocAssemblerService.invoke(ddx, sourceDocuments, assemblerOptionSpec);
		} catch(OperationException e) {
			throw new AssemblerServiceException("Erroer while assembling documents ", e);
		}
	}

	@Override
	public PDFAValidationResult isPDFA(Document inDoc, PDFAValidationOptionSpec options) throws AssemblerServiceException {
		try {
			return adobDocAssemblerService.isPDFA(inDoc, options);
		} catch (ValidationException e) {
			throw new AssemblerServiceException("Erroer while Validating pdf ", e);
		}
	}

	@Override
	public PDFAConversionResult toPDFA(Document inDoc, PDFAConversionOptionSpec options) throws AssemblerServiceException {
		try {
			return adobDocAssemblerService.toPDFA(inDoc, options);
		}  catch (AssemblerServiceException | ConversionException e) {
			throw new AssemblerServiceException("Error while converting pdf ", e);
		}
		
	}
	
	protected TraditionalDocAssemblerService getAdobeAssemblerService() {
		return adobDocAssemblerService;
	}


	private class AssemblerArgumentBuilderImpl implements AssemblerArgumentBuilder {

		AssemblerOptionsSpec assemblerOptionsSpec = new AssemblerOptionsSpecImpl();

		@Override
		public AssemblerArgumentBuilder setFailOnError(Boolean isFailOnError) {
			this.assemblerOptionsSpec.setFailOnError(isFailOnError);
			return this;
		}

		@Override
		public AssemblerResult executeOn(Document ddx, Map<String, Object> sourceDocuments) throws AssemblerServiceException, OperationException {
			return invoke(ddx, sourceDocuments, this.assemblerOptionsSpec);

		}

		@Override
		public AssemblerArgumentBuilder setDefaultStyle(String defaultStyle) {
			this.assemblerOptionsSpec.setDefaultStyle(defaultStyle);
			return this;
		}

		@Override
		public AssemblerArgumentBuilder setFirstBatesNumber(int start) {
			this.assemblerOptionsSpec.setFirstBatesNumber(start);
			return this;
		}

		@Override
		public AssemblerArgumentBuilder setLogLevel(String logLevel) {
			this.assemblerOptionsSpec.setLogLevel(logLevel);
			return this;
		}

		@Override
		public AssemblerArgumentBuilder setTakeOwnership(Boolean takeOwnership) {
			this.assemblerOptionsSpec.setTakeOwnership(takeOwnership);
			return this;
		}

		@Override
		public AssemblerArgumentBuilder setValidateOnly(Boolean validateOnly) {
			this.assemblerOptionsSpec.setValidateOnly(validateOnly);
			return this;
		}       

	}

	@Override
	public AssemblerArgumentBuilder invoke() {
		return new AssemblerArgumentBuilderImpl();
	}


}
