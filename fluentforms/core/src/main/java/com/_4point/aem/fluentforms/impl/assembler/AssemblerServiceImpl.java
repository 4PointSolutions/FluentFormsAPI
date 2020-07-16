package com._4point.aem.fluentforms.impl.assembler;

import java.util.Map;
import java.util.Objects;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSpec;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.api.assembler.AssemblerService;
import com._4point.aem.fluentforms.impl.UsageContext;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec;
import com.adobe.fd.assembler.client.PDFAConversionResult;
import com.adobe.fd.assembler.client.PDFAValidationOptionSpec;
import com.adobe.fd.assembler.client.PDFAValidationResult;

public class AssemblerServiceImpl implements AssemblerService {
	private final TraditionalDocAssemblerService adobDocAssemblerService;
	private final UsageContext usageContext;

	public AssemblerServiceImpl(TraditionalDocAssemblerService adobDocAssemblerService, UsageContext usageContext) {
		super();
		this.adobDocAssemblerService = new SafeDocAssemblerServiceAdapterWrapper(adobDocAssemblerService);
		this.usageContext = usageContext;
	}

	@Override
	public AssemblerResult invoke(Document ddx, Map<String, Object> sourceDocuments,
			AssemblerOptionsSpec assemblerOptionSpec) throws AssemblerServiceException {
		Objects.requireNonNull(ddx, "ddx Document cannot be null.");
		Objects.requireNonNull(sourceDocuments, "sourceDocuments cannot be null.");
		try {
			return adobDocAssemblerService.invoke(ddx, sourceDocuments, assemblerOptionSpec);
		} catch (Exception e) {
			throw new AssemblerServiceException(e);
		}
	}

	@Override
	public PDFAValidationResult isPDFA(Document inDoc, PDFAValidationOptionSpec options) throws AssemblerServiceException {
		Objects.requireNonNull(inDoc, "inDoc Document cannot be null.");
		Objects.requireNonNull(options, "options cannot be null.");
		try {
		return adobDocAssemblerService.isPDFA(inDoc, options);
		} catch(Exception e) {
			throw new AssemblerServiceException(e);
		}
	}

	@Override
	public PDFAConversionResult toPDFA(Document inDoc, PDFAConversionOptionSpec options) throws AssemblerServiceException {
		Objects.requireNonNull(inDoc, "options Document cannot be null.");
		Objects.requireNonNull(options, "options cannot be null.");
		try {
		return adobDocAssemblerService.toPDFA(inDoc, options);
		} catch( Exception e) {
			throw new AssemblerServiceException(e);
		}
	}

	private class AssemblerArgumentBuilderImpl implements AssemblerArgumentBuilder {

		AssemblerOptionsSpec assemblerOptionsSpec = new AssemblerOptionsSpecImpl();

		@Override
		public AssemblerResult executeOn(Document ddx, Map<String, Object> sourceDocuments) throws AssemblerServiceException {
			return invoke(ddx, sourceDocuments, this.assemblerOptionsSpec);

		}

		@Override
		public AssemblerArgumentBuilder setFailOnError(Boolean isFailOnError) {
			this.assemblerOptionsSpec.setFailOnError(isFailOnError);
			return this;
		}

		@Override
		public AssemblerArgumentBuilder setContentRoot(PathOrUrl contentRoot) {
			this.assemblerOptionsSpec.setContentRoot(contentRoot);
			return this;
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
