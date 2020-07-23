package com._4point.aem.fluentforms.testing.assembler;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSpec;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.api.assembler.AssemblerService.AssemblerServiceException;
import com._4point.aem.fluentforms.impl.assembler.TraditionalDocAssemblerService;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com.adobe.fd.assembler.client.OperationException;

public class MockTraditionalAssemblerService implements TraditionalDocAssemblerService {

	private final AssemblerResult dummtAssemblerResult;
	private AssemblerResult assemblerResult;
	private GenerateAssemblerResultArgs generateAssemblerResultArgs;
	private final Document dummyDocument;
	private final DocumentFactory documentFactory;

	
	@Override
	public AssemblerResult invoke(Document ddx, Map<String, Object> inputs,
			AssemblerOptionsSpec adobAssemblerOptionSpec) throws AssemblerServiceException {
		this.generateAssemblerResultArgs = new GenerateAssemblerResultArgs(ddx, inputs, adobAssemblerOptionSpec);
		return this.assemblerResult == null ? dummtAssemblerResult : this.assemblerResult;
	}

	/*
	 * @Override public PDFAValidationResult isPDFA(Document inDoc,
	 * PDFAValidationOptionSpec options) throws AssemblerServiceException { throw
	 * new UnsupportedOperationException("isPDFA has not been implemented yet."); }
	 * 
	 * @Override public PDFAConversionResult toPDFA(Document inDoc,
	 * PDFAConversionOptionSpec options) throws AssemblerServiceException { throw
	 * new UnsupportedOperationException("toPDFA has not been implemented yet."); }
	 */

	MockTraditionalAssemblerService() {
		super();
		this.documentFactory = new MockDocumentFactory();
		this.dummyDocument = documentFactory.create(new byte[0]);
		this.dummtAssemblerResult = new DummyAssemblerResult();
	}
	
	public static MockTraditionalAssemblerService createAssemblerMock(AssemblerResult assemblerResult) {
		return new MockTraditionalAssemblerService().setAssemblerResult(assemblerResult);
	}

	public MockTraditionalAssemblerService setAssemblerResult(AssemblerResult assemblerResult) {
		this.assemblerResult = assemblerResult;
		return this;
	}
	
	public AssemblerResult getDummyAssemblerResult() {
		return dummtAssemblerResult;
	}
	
	public AssemblerResult getAssemblerResult() {
		return assemblerResult;
	}

	public GenerateAssemblerResultArgs getGenerateAssemblerResultArgs() {
		return generateAssemblerResultArgs;
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

		@Override
		public void close() throws IOException {

			
		}
	
	}

}
