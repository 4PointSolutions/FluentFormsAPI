package com._4point.aem.fluentforms.testing.assembler;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSpec;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.api.assembler.AssemblerService.AssemblerServiceException;
import com._4point.aem.fluentforms.api.assembler.PDFAConversionOptionSpec;
import com._4point.aem.fluentforms.api.assembler.PDFAConversionResult;
import com._4point.aem.fluentforms.api.assembler.PDFAValidationOptionSpec;
import com._4point.aem.fluentforms.api.assembler.PDFAValidationResult;
import com._4point.aem.fluentforms.impl.assembler.TraditionalDocAssemblerService;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com.adobe.fd.assembler.client.OperationException;

public class MockTraditionalAssemblerService implements TraditionalDocAssemblerService {

	private final AssemblerResult dummyAssemblerResult;
	private AssemblerResult assemblerResult;
	private GenerateAssemblerResultArgs generateAssemblerResultArgs;
	private final Document dummyDocument;
	private final DocumentFactory documentFactory;
	private final PDFAValidationResult dummyPdfaValidationResult;
	private PDFAValidationResult pdfaValidationResult;
	private final PDFAConversionResult dummyPdfaConversionResult;
	private PDFAConversionResult pdfaConversionResult;
	private IsPdfaArguments isPdfaArguments;
	private toPdfaArguments toPdfaArguments;
	
	MockTraditionalAssemblerService() {
		this.documentFactory = new MockDocumentFactory();
		this.dummyDocument = documentFactory.create(new byte[0]);
		this.dummyAssemblerResult = new DummyAssemblerResult();
		this.dummyPdfaValidationResult = new DummyPdfAValidationResult();
		this.dummyPdfaConversionResult = new DummyPdfAConversionResult();
	}
	
	public static MockTraditionalAssemblerService createAssemblerMock(AssemblerResult assemblerResult) {
		return new MockTraditionalAssemblerService().setAssemblerResult(assemblerResult);
	}
	
	public static MockTraditionalAssemblerService createAssemblerMock(PDFAValidationResult validationResult) {
		return new MockTraditionalAssemblerService().setPdfaValidationResult(validationResult);
	}
	
	public static MockTraditionalAssemblerService createAssemblerMock(PDFAConversionResult conversionResult) {
		return new MockTraditionalAssemblerService().setPdfaConversionResult(conversionResult);
	}
	
	@Override
	public AssemblerResult invoke(Document ddx, Map<String, Object> inputs,
			AssemblerOptionsSpec adobAssemblerOptionSpec) throws AssemblerServiceException {
		this.generateAssemblerResultArgs = new GenerateAssemblerResultArgs(ddx, inputs, adobAssemblerOptionSpec);
		return this.assemblerResult == null ? dummyAssemblerResult : this.assemblerResult;
	}

	@Override
	public PDFAValidationResult isPDFA(Document inDoc, PDFAValidationOptionSpec options) throws AssemblerServiceException {
		this.isPdfaArguments = new IsPdfaArguments(inDoc, options);
		return this.pdfaValidationResult == null ? dummyPdfaValidationResult : pdfaValidationResult;
	}

	@Override
	public PDFAConversionResult toPDFA(Document inDoc, PDFAConversionOptionSpec options) throws AssemblerServiceException {
		this.toPdfaArguments = new toPdfaArguments(inDoc, options);
		return this.pdfaConversionResult == null ? dummyPdfaConversionResult : pdfaConversionResult;
	}

	public MockTraditionalAssemblerService setAssemblerResult(AssemblerResult assemblerResult) {
		this.assemblerResult = assemblerResult;
		return this;
	}
	
	public MockTraditionalAssemblerService setPdfaValidationResult(PDFAValidationResult pdfaValidationResult) {
		this.pdfaValidationResult = pdfaValidationResult;
		return this;
	}

	public MockTraditionalAssemblerService setPdfaConversionResult(PDFAConversionResult pdfaConversionResult) {
		this.pdfaConversionResult = pdfaConversionResult;
		return this;
	}

	
	public GenerateAssemblerResultArgs getGenerateAssemblerResultArgs() {
		return generateAssemblerResultArgs;
	}

	public IsPdfaArguments getIsPdfaArguments() {
		return isPdfaArguments;
	}

	public toPdfaArguments getToPdfaArguments() {
		return toPdfaArguments;
	}

	// Allow clients access to dummies to make sure they are unaltered.
	public AssemblerResult getDummyAssemblerResult() {
		return dummyAssemblerResult;
	}

	public Document getDummyDocument() {
		return dummyDocument;
	}

	public PDFAValidationResult getDummyPdfaValidationResult() {
		return dummyPdfaValidationResult;
	}

	public PDFAConversionResult getDummyPdfaConversionResult() {
		return dummyPdfaConversionResult;
	}

	public static class GenerateAssemblerResultArgs {
		private final Document ddx;
		private final Map<String, Object> sourceDocuments;
		private final AssemblerOptionsSpec assemblerOptionsSpec;

		private GenerateAssemblerResultArgs(Document ddx, Map<String, Object> sourceDocuments,
				AssemblerOptionsSpec assemblerOptionsSpec) {
			super();
			this.ddx = ddx;
			this.sourceDocuments = sourceDocuments;
			this.assemblerOptionsSpec = Objects.requireNonNull(assemblerOptionsSpec);
		}

		public Document getDdx() {
			return ddx;
		}

		public Map<String, Object> getSourceDocuments() {
			return sourceDocuments;
		}

		public AssemblerOptionsSpec getAssemblerOptionsSpec() {
			return assemblerOptionsSpec;
		}

	}

	public static class IsPdfaArguments {
		private final Document inDoc;
		private final PDFAValidationOptionSpec options;

		public IsPdfaArguments(Document inDoc, PDFAValidationOptionSpec options) {
			this.inDoc = inDoc;
			this.options = options;
		}

		public Document getInDoc() {
			return inDoc;
		}

		public PDFAValidationOptionSpec getOptions() {
			return options;
		}
	}
	
	public static class toPdfaArguments {
		private final Document inDoc;
		private final PDFAConversionOptionSpec options;

		public toPdfaArguments(Document inDoc, PDFAConversionOptionSpec options) {
			this.inDoc = inDoc;
			this.options = options;
		}

		public Document getInDoc() {
			return inDoc;
		}

		public PDFAConversionOptionSpec getOptions() {
			return options;
		}
	}

	private class DummyAssemblerResult implements AssemblerResult {

		@Override
		public Map<String, Document> getDocuments() {
			return  Collections.emptyMap();
		}

		@Override
		public List<String> getFailedBlockNames() {
			return  Collections.emptyList();
		}

		@Override
		public Document getJobLog() {
			return dummyDocument;
		}

		@Override
		public int getLastBatesNumber() {
			return 0;
		}

		@Override
		public Map<String, List<String>> getMultipleResultsBlocks() {
			return  Collections.emptyMap();
		}

		@Override
		public int getNumRequestedBlocks() {
			return 0;
		}

		@Override
		public List<String> getSuccessfulBlockNames() {
			return  Collections.emptyList();
		}

		@Override
		public List<String> getSuccessfulDocumentNames() {
			return  Collections.emptyList();
		}

		@Override
		public Map<String, OperationException> getThrowables() {
			return  Collections.emptyMap();
		}
	}
	
	private class DummyPdfAValidationResult implements PDFAValidationResult {
		private final boolean isPdfA;
		
		public DummyPdfAValidationResult(boolean isPdfA) {
			this.isPdfA = isPdfA;
		}

		public DummyPdfAValidationResult() {
			this(true);
		}

		@Override
		public Document getJobLog() {
			return dummyDocument;
		}

		@Override
		public Document getValidationLog() {
			return dummyDocument;
		}

		@Override
		public boolean isPDFA() {
			return isPdfA;
		}
		
	}
	
	private class DummyPdfAConversionResult implements PDFAConversionResult {
		private final boolean isPdfA;

		public DummyPdfAConversionResult(boolean isPdfA) {
			this.isPdfA = isPdfA;
		}
		
		public DummyPdfAConversionResult() {
			this(true);
		}

		@Override
		public Document getConversionLog() {
			return dummyDocument;
		}

		@Override
		public Document getJobLog() {
			return dummyDocument;
		}

		@Override
		public Document getPDFADocument() {
			return dummyDocument;
		}

		@Override
		public Boolean isPDFA() {
			return isPdfA;
		}
		
	}
}
