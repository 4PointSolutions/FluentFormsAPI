package com._4point.aem.fluentforms.testing.assembler;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSpec;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.api.assembler.AssemblerService.AssemblerServiceException;
import com._4point.aem.fluentforms.impl.assembler.TraditionalDocAssemblerService;
import com.adobe.fd.assembler.client.OperationException;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec;
import com.adobe.fd.assembler.client.PDFAConversionResult;
import com.adobe.fd.assembler.client.PDFAValidationOptionSpec;
import com.adobe.fd.assembler.client.PDFAValidationResult;

public class MockTraditionalAssemblerService implements TraditionalDocAssemblerService {

	private final AssemblerResult DUMMY_ASSEMBLER_RESULT;

	public AssemblerResult getDUMMY_ASSEMBLER_RESULT() {
		return DUMMY_ASSEMBLER_RESULT;
	}

	AssemblerResult assemblerResult;
	GenerateAssemblerResultArgs generateAssemblerResultArgs;

	public GenerateAssemblerResultArgs getGenerateAssemblerResultArgs() {
		return generateAssemblerResultArgs;
	}

	@Override
	public AssemblerResult invoke(Document ddx, Map<String, Object> inputs,
			AssemblerOptionsSpec adobAssemblerOptionSpec) throws AssemblerServiceException, OperationException {
		this.generateAssemblerResultArgs = new GenerateAssemblerResultArgs(ddx, inputs, adobAssemblerOptionSpec);
		return this.assemblerResult == null ? DUMMY_ASSEMBLER_RESULT : this.assemblerResult;
	}

	@Override
	public PDFAValidationResult isPDFA(Document inDoc, PDFAValidationOptionSpec options)
			throws AssemblerServiceException {
		throw new UnsupportedOperationException("isPDFA has not been implemented yet.");
	}

	@Override
	public PDFAConversionResult toPDFA(Document inDoc, PDFAConversionOptionSpec options)
			throws AssemblerServiceException {
		throw new UnsupportedOperationException("toPDFA has not been implemented yet.");
	}

	MockTraditionalAssemblerService() {
		super();
		this.DUMMY_ASSEMBLER_RESULT = new DummyAssemblerResult();
	}

	public MockTraditionalAssemblerService setResult(AssemblerResult assemblerResult) {
		this.assemblerResult = assemblerResult;
		return this;
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
			return null;
		}

		@Override
		public List<String> getFailedBlockNames() {
			return null;
		}

		@Override
		public Document getJobLog() {
			return null;
		}

		@Override
		public int getLastBatesNumber() {
			return 0;
		}

		@Override
		public Map<String, List<String>> getMultipleResultsBlocks() {
			return null;
		}

		@Override
		public int getNumRequestedBlocks() {
			return 0;
		}

		@Override
		public List<String> getSuccessfulBlockNames() {
			return null;
		}

		@Override
		public List<String> getSuccessfulDocumentNames() {
			return null;
		}

		@Override
		public Map<String, OperationException> getThrowables() {
			return null;
		}
	
	}

}
