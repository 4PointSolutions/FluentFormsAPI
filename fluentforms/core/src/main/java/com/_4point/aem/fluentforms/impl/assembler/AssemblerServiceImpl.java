package com._4point.aem.fluentforms.impl.assembler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSpec;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.api.assembler.AssemblerService;
import com._4point.aem.fluentforms.api.assembler.LogLevel;
import com._4point.aem.fluentforms.api.assembler.PDFAConversionOptionSpec;
import com._4point.aem.fluentforms.api.assembler.PDFAConversionResult;
import com._4point.aem.fluentforms.api.assembler.PDFAValidationOptionSpec;
import com._4point.aem.fluentforms.api.assembler.PDFAValidationResult;
import com._4point.aem.fluentforms.impl.UsageContext;
import com.adobe.fd.assembler.client.OperationException;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.ColorSpace;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.Compliance;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.OptionalContent;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.ResultLevel;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.Signatures;

public class AssemblerServiceImpl implements AssemblerService {
	private final TraditionalDocAssemblerService adobeDocAssemblerService;

	public AssemblerServiceImpl(TraditionalDocAssemblerService adobDocAssemblerService, UsageContext usageContext) {
		this.adobeDocAssemblerService = new SafeAssemblerServiceAdapterWrapper(adobDocAssemblerService);
	}

	@Override
	public AssemblerResult invoke(Document ddx, Map<String, Object> sourceDocuments,
			AssemblerOptionsSpec assemblerOptionSpec) throws AssemblerServiceException {
		return adobeDocAssemblerService.invoke(ddx, sourceDocuments, assemblerOptionSpec);
	}

	@Override 
	public PDFAValidationResult isPDFA(Document inDoc, PDFAValidationOptionSpec options) throws AssemblerServiceException { 
		return adobeDocAssemblerService.isPDFA(inDoc, options); 
	}

	@Override 
	public PDFAConversionResult toPDFA(Document inDoc, PDFAConversionOptionSpec options) throws AssemblerServiceException{ 
		return adobeDocAssemblerService.toPDFA(inDoc, options); 
	}
	
	protected TraditionalDocAssemblerService getAdobeAssemblerService() {
		return adobeDocAssemblerService;
	}


	@Override
	public AssemblerArgumentBuilder invoke() {
		return new AssemblerArgumentBuilderImpl();
	}

	@Override
	public PDFAConversionArgumentBuilder toPDFA() {
		return new PDFAConversionArgumentonBuilderImpl();
	}

	@Override
	public PDFAValidationArgumentBuilder isPDFA() {
		return new PDFAValidationArgumentBuilderImpl();
	}

	private class AssemblerArgumentBuilderImpl implements AssemblerArgumentBuilder {
		
		AssemblerOptionsSpec assemblerOptionsSpec = new AssemblerOptionsSpecImpl();
		Map<String, EitherDocumentOrDocumentList> sourceDocumentMap = new HashMap<>();;
		

		@Override
		public AssemblerArgumentBuilder setFailOnError(Boolean isFailOnError) {
			this.assemblerOptionsSpec.setFailOnError(isFailOnError);
			return this;
		}

		/**
		 * Deprecated because it is not typesafe, use add() or executeOn2 instead.
		 */
		@Deprecated
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
		public AssemblerArgumentBuilder setLogLevel(LogLevel logLevel) {
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

		@Override
		public AssemblerArgumentBuilder add(String name, Document document) {
			this.sourceDocumentMap.put(name, EitherDocumentOrDocumentList.from(document));
			return this;
		}

		@Override
		public AssemblerArgumentBuilder add(String name, List<Document> documentList) {
			this.sourceDocumentMap.put(name, EitherDocumentOrDocumentList.from(documentList));
			return this;
		}

		@Override
		public AssemblerArgumentBuilder add(String name, EitherDocumentOrDocumentList docOrList) {
			this.sourceDocumentMap.put(name, docOrList);
			return this;
		}

		@Override
		public AssemblerResult executeOn2(Document ddx, Map<String, EitherDocumentOrDocumentList> sourceDocuments)
				throws AssemblerServiceException, OperationException {
			Map<String, Object> docMap = sourceDocuments.entrySet()
	        			   							    .stream()
	        			   							    .collect(Collectors.toMap(Map.Entry::getKey,
	        			   							    						  e -> e.getValue().toObject()
	        			   							    						 )
	        			   							    		);
			return invoke(ddx, docMap, this.assemblerOptionsSpec);
		}

		@Override
		public AssemblerResult executeOn(Document ddx) throws AssemblerServiceException, OperationException {
			return this.executeOn2(ddx, sourceDocumentMap);
		}       

	}

	private class PDFAConversionArgumentonBuilderImpl implements PDFAConversionArgumentBuilder {
		private final PDFAConversionOptionSpecImpl options = new PDFAConversionOptionSpecImpl();
		@Override
		public PDFAConversionArgumentBuilder setColorSpace(ColorSpace colorSpace) {
			options.setColorSpace(colorSpace);
			return this;
		}

		@Override
		public PDFAConversionArgumentBuilder setCompliance(Compliance compliance) {
			options.setCompliance(compliance);
			return this;
		}

		@Override
		public PDFAConversionArgumentBuilder setLogLevel(LogLevel logLevel) {
			options.setLogLevel(logLevel);
			return this;
		}

		@Override
		public PDFAConversionArgumentBuilder setMetadataSchemaExtensions(List<Document> metadataSchemaExtensions) {
			options.setMetadataSchemaExtensions(metadataSchemaExtensions);
			return this;
		}

		@Override
		public PDFAConversionArgumentBuilder setOptionalContent(OptionalContent optionalContent) {
			options.setOptionalContent(optionalContent);
			return this;
		}

		@Override
		public PDFAConversionArgumentBuilder setRemoveInvalidXMPProperties(boolean remove) {
			options.setRemoveInvalidXMPProperties(remove);
			return this;
		}

		@Override
		public PDFAConversionArgumentBuilder setResultLevel(ResultLevel resultLevel) {
			options.setResultLevel(resultLevel);
			return this;
		}

		@Override
		public PDFAConversionArgumentBuilder setRetainPDFFormState(boolean retainPDFFormState) {
			options.setRetainPDFFormState(retainPDFFormState);
			return this;
		}

		@Override
		public PDFAConversionArgumentBuilder setSignatures(Signatures signatures) {
			options.setSignatures(signatures);
			return this;
		}

		@Override
		public PDFAConversionArgumentBuilder setVerify(boolean verify) {
			options.setVerify(verify);
			return this;
		}

		@Override
		public PDFAConversionResult executeOn(Document inDoc) throws AssemblerServiceException {
			return toPDFA(inDoc, this.options);
		}
	}
	
	private class PDFAValidationArgumentBuilderImpl implements PDFAValidationArgumentBuilder {
		private final PDFAValidationOptionSpecImpl options =new PDFAValidationOptionSpecImpl();
		
		@Override
		public PDFAValidationArgumentBuilder setAllowCertificationSignatures(boolean allowCertificationSignatures) {
			options.setAllowCertificationSignatures(allowCertificationSignatures);
			return this;
		}

		@Override
		public PDFAValidationArgumentBuilder setCompliance(Compliance compliance) {
			options.setCompliance(compliance);
			return this;
		}

		@Override
		public PDFAValidationArgumentBuilder setIgnoreUnusedResource(boolean ignoreUnusedResource) {
			options.setIgnoreUnusedResource(ignoreUnusedResource);
			return this;
		}

		@Override
		public PDFAValidationArgumentBuilder setLogLevel(LogLevel logLevel) {
			options.setLogLevel(logLevel);
			return this;
		}

		@Override
		public PDFAValidationArgumentBuilder setResultLevel(com.adobe.fd.assembler.client.PDFAValidationOptionSpec.ResultLevel resultLevel) {
			options.setResultLevel(resultLevel);
			return this;
		}

		@Override
		public PDFAValidationResult executeOn(Document inDoc) throws AssemblerServiceException {
			return isPDFA(inDoc, options);
		}
	}
}
