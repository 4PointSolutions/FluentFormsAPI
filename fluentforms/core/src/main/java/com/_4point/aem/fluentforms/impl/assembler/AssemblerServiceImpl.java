package com._4point.aem.fluentforms.impl.assembler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSpec;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.api.assembler.AssemblerService;
import com._4point.aem.fluentforms.impl.UsageContext;
import com.adobe.fd.assembler.client.OperationException;

public class AssemblerServiceImpl implements AssemblerService {
	private final TraditionalDocAssemblerService adobeDocAssemblerService;

	public AssemblerServiceImpl(TraditionalDocAssemblerService adobDocAssemblerService, UsageContext usageContext) {
		super();
		this.adobeDocAssemblerService = new SafeAssemblerServiceAdapterWrapper(adobDocAssemblerService);
	}

	@Override
	public AssemblerResult invoke(Document ddx, Map<String, Object> sourceDocuments,
			AssemblerOptionsSpec assemblerOptionSpec) throws AssemblerServiceException {
		return adobeDocAssemblerService.invoke(ddx, sourceDocuments, assemblerOptionSpec);
	}

	/*
	 * @Override public PDFAValidationResult isPDFA(Document inDoc,
	 * PDFAValidationOptionSpec options) throws AssemblerServiceException { try {
	 * return adobDocAssemblerService.isPDFA(inDoc, options); } catch
	 * (ValidationException e) { throw new
	 * AssemblerServiceException("Erroer while Validating pdf ", e); } }
	 * 
	 * @Override public PDFAConversionResult toPDFA(Document inDoc,
	 * PDFAConversionOptionSpec options) throws AssemblerServiceException { try {
	 * return adobDocAssemblerService.toPDFA(inDoc, options); } catch
	 * (AssemblerServiceException | ConversionException e) { throw new
	 * AssemblerServiceException("Error while converting pdf ", e); }
	 * 
	 * }
	 */
	
	protected TraditionalDocAssemblerService getAdobeAssemblerService() {
		return adobeDocAssemblerService;
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

	@Override
	public AssemblerArgumentBuilder invoke() {
		return new AssemblerArgumentBuilderImpl();
	}


}
