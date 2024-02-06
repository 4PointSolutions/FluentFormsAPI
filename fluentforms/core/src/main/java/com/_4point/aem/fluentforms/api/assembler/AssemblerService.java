package com._4point.aem.fluentforms.api.assembler;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.Transformable;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com.adobe.fd.assembler.client.OperationException;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.ColorSpace;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.Compliance;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.OptionalContent;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.ResultLevel;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.Signatures;

public interface AssemblerService {
	
	AssemblerResult invoke(Document ddx, Map<String,Object> sourceDocuments, AssemblerOptionsSpec assemblerOptionSpec) throws AssemblerServiceException; 
	
	PDFAValidationResult isPDFA(Document inDoc, PDFAValidationOptionSpec options) throws AssemblerServiceException;
	
	PDFAConversionResult toPDFA(Document inDoc, PDFAConversionOptionSpec options)throws AssemblerServiceException;
	
	AssemblerArgumentBuilder invoke();

	PDFAConversionArgumentBuilder toPDFA();
	
	PDFAValidationArgumentBuilder isPDFA();
	
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
   
	// Perhaps this will be converted to record when Java 17 becomes the minimumm version for AEM
	public static class EitherDocumentOrDocumentList {
		private final Optional<Document> document;
		private final Optional<List<Document>> list;

		private EitherDocumentOrDocumentList(Optional<Document> document, Optional<List<Document>> list) {
			super();
			this.document = document;
			this.list = list;
		}

		public Optional<Document> document() {
			return document;
		}

		public Optional<List<Document>> list() {
			return list;
		}
		
		public Object toObject() {
			// I usually eschew isPresent()/get() in favour of orElse(), however in this case, the typesafety of those
			// methods make the resulting code quite complex so I've used isPresent()/get to keep things simple.
			if (document.isPresent()) {
				return document.get();
			} else if (list.isPresent()) {
				return list.get();
			} else {
				throw new IllegalStateException("Neither document or list is present.");
			}
		}
		
		public static EitherDocumentOrDocumentList from(Document document) {
			return new EitherDocumentOrDocumentList(Optional.of(document), Optional.empty());
		}
		public static EitherDocumentOrDocumentList from(List<Document> list) {
			return new EitherDocumentOrDocumentList(Optional.empty(), Optional.of(list));
		}
	}
	
	public static interface AssemblerArgumentBuilder extends AssemblerOptionsSetter, Transformable<AssemblerArgumentBuilder> {
		
		@Override
		public AssemblerArgumentBuilder setFailOnError(Boolean isFailOnError);
		
		@Override
		public AssemblerArgumentBuilder setDefaultStyle(String defaultStyle);
	    
		@Override
		public AssemblerArgumentBuilder setFirstBatesNumber(int start);
	    
		@Override
		public AssemblerArgumentBuilder setLogLevel(LogLevel logLevel);
	    
		@Override
		public AssemblerArgumentBuilder setTakeOwnership(Boolean takeOwnership);
	    
		@Override
		public AssemblerArgumentBuilder setValidateOnly(Boolean validateOnly);
      
		public AssemblerArgumentBuilder add(String name, Document document);
		
		public AssemblerArgumentBuilder add(String name, List<Document> documentList);
		
		public AssemblerArgumentBuilder add(String name, EitherDocumentOrDocumentList docOrList);
		
		/**
		 * This is deprecated because it is not typesafe.  As alternatives use:
		 * add() for each document or document list
		 * or
		 * executeOn2()
		 * 
		 * @param ddx
		 * @param sourceDocuments
		 * @return
		 * @throws AssemblerServiceException
		 * @throws OperationException
		 */
		@Deprecated
		public AssemblerResult executeOn(Document ddx, Map<String,Object>sourceDocuments) throws AssemblerServiceException, OperationException;

		public AssemblerResult executeOn2(Document ddx, Map<String,EitherDocumentOrDocumentList>sourceDocuments) throws AssemblerServiceException, OperationException;

		public AssemblerResult executeOn(Document ddx) throws AssemblerServiceException, OperationException;
		
		default public AssemblerResult executeOn2(byte[] ddx) throws AssemblerServiceException, OperationException {
			DocumentFactory factory = SimpleDocumentFactoryImpl.getFactory();
			return executeOn(factory.create(ddx));
		};

		default public AssemblerResult executeOn(byte[] ddx) throws AssemblerServiceException, OperationException {
			DocumentFactory factory = SimpleDocumentFactoryImpl.getFactory();
			return executeOn(factory.create(ddx));
		};

		default AssemblerArgumentBuilder addAllDocs(Stream<Map.Entry<String, Document>> pairs) {
			pairs.forEach(e->add(e.getKey(), e.getValue()));
			return this;
		}

		default AssemblerArgumentBuilder addAllLists(Stream<Map.Entry<String, List<Document>>> pairs) {
			pairs.forEach(e->add(e.getKey(), e.getValue()));
			return this;
		}

		default AssemblerArgumentBuilder addAll(Stream<Map.Entry<String, EitherDocumentOrDocumentList>> pairs) {
			pairs.forEach(e->add(e.getKey(), e.getValue()));
			return this;
		}
	}

	public static interface PDFAConversionArgumentBuilder extends PDFAConversionOptionsSetter, Transformable<PDFAConversionArgumentBuilder> {

		@Override
		public PDFAConversionArgumentBuilder setColorSpace(ColorSpace colorSpace);

		@Override
		public PDFAConversionArgumentBuilder setCompliance(Compliance compliance);

		@Override
		public PDFAConversionArgumentBuilder setLogLevel(LogLevel logLevel);

		@Override
		public PDFAConversionArgumentBuilder setMetadataSchemaExtensions(List<Document> metadataSchemaExtensions);

		@Override
		public PDFAConversionArgumentBuilder setOptionalContent(OptionalContent optionalContent);

		@Override
		public PDFAConversionArgumentBuilder setRemoveInvalidXMPProperties(boolean remove);

		@Override
		public PDFAConversionArgumentBuilder setResultLevel(ResultLevel resultLevel);

		@Override
		public PDFAConversionArgumentBuilder setRetainPDFFormState(boolean retainPDFFormState);

		@Override
		public PDFAConversionArgumentBuilder setSignatures(Signatures signatures);

		@Override
		public PDFAConversionArgumentBuilder setVerify(boolean verify);
		
		public PDFAConversionResult executeOn(Document inDoc) throws AssemblerServiceException;

		default public PDFAConversionResult executeOn(byte[] inPdf) throws AssemblerServiceException {
			DocumentFactory factory = SimpleDocumentFactoryImpl.getFactory();
			return executeOn(factory.create(inPdf));
		};
	}
	
	public static interface PDFAValidationArgumentBuilder extends PDFAValidationOptionsSetter, Transformable<PDFAConversionArgumentBuilder> {

		@Override
		public PDFAValidationArgumentBuilder setAllowCertificationSignatures(boolean allowCertificationSignatures);

		@Override
		public PDFAValidationArgumentBuilder setCompliance(Compliance compliance);

		@Override
		public PDFAValidationArgumentBuilder setIgnoreUnusedResource(boolean ignoreUnusedResource);

		@Override
		public PDFAValidationArgumentBuilder setLogLevel(LogLevel logLevel);

		@Override
		public PDFAValidationArgumentBuilder setResultLevel(com.adobe.fd.assembler.client.PDFAValidationOptionSpec.ResultLevel resultLevel);
		
		public PDFAValidationResult executeOn(Document inDoc) throws AssemblerServiceException;

		default public PDFAValidationResult executeOn(byte[] inPdf) throws AssemblerServiceException {
			DocumentFactory factory = SimpleDocumentFactoryImpl.getFactory();
			return executeOn(factory.create(inPdf));
		};
	}
	
}
