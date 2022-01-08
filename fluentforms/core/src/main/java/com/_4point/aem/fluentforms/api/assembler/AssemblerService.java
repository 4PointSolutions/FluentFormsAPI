package com._4point.aem.fluentforms.api.assembler;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.Transformable;
import com._4point.aem.fluentforms.impl.assembler.LogLevel;
import com.adobe.fd.assembler.client.OperationException;
public interface AssemblerService {
	
	AssemblerResult invoke(Document ddx, Map<String,Object> sourceDocuments, AssemblerOptionsSpec assemblerOptionSpec) throws AssemblerServiceException; 

	
	/*
	 * PDFAValidationResult isPDFA(Document inDoc, PDFAValidationOptionSpec options)
	 * throws AssemblerServiceException;
	 * 
	 * PDFAConversionResult toPDFA(Document inDoc, PDFAConversionOptionSpec options)
	 * throws AssemblerServiceException ;
	 */
	
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
		AssemblerArgumentBuilder setFailOnError(Boolean isFailOnError);
		
		@Override
		AssemblerArgumentBuilder setDefaultStyle(String defaultStyle);
	    
		@Override
		AssemblerArgumentBuilder setFirstBatesNumber(int start);
	    
		@Override
		AssemblerArgumentBuilder setLogLevel(LogLevel logLevel);
	    
		@Override
		AssemblerArgumentBuilder setTakeOwnership(Boolean takeOwnership);
	    
		@Override
		AssemblerArgumentBuilder setValidateOnly(Boolean validateOnly);
      
		AssemblerArgumentBuilder add(String name, Document document);
		
		AssemblerArgumentBuilder add(String name, List<Document> documentList);
		
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

	}

}
