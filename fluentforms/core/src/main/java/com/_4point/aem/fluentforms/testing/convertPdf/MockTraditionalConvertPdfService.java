package com._4point.aem.fluentforms.testing.convertPdf;

import java.util.Collections;
import java.util.List;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.convertPdf.ConvertPdfService.ConvertPdfServiceException;
import com._4point.aem.fluentforms.api.convertPdf.ToImageOptionsSpec;
import com._4point.aem.fluentforms.api.convertPdf.ToPSOptionsSpec;
import com._4point.aem.fluentforms.impl.convertPdf.TraditionalConvertPdfService;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;

public class MockTraditionalConvertPdfService implements TraditionalConvertPdfService {
	private final DocumentFactory documentFactory;
	private final Document DUMMY_DOCUMENT;
	private final List<Document> DUMMY_DOCUMENT_LIST;

	Document result;
	List<Document> resultList;
	
	ToImageArgs toImageArgs;
	ToPSArgs toPSArgs;
	
	public MockTraditionalConvertPdfService() {
		super();
		this.documentFactory = new MockDocumentFactory();
		this.DUMMY_DOCUMENT = documentFactory.create(new byte[0]);
		this.DUMMY_DOCUMENT_LIST = Collections.singletonList(documentFactory.create(new byte[0]));
	}
	
	public MockTraditionalConvertPdfService(DocumentFactory documentFactory) {
		super();
		this.documentFactory = documentFactory;
		this.DUMMY_DOCUMENT = documentFactory.create(new byte[0]);
		this.DUMMY_DOCUMENT_LIST = Collections.singletonList(documentFactory.create(new byte[0]));
	}
	
	public static MockTraditionalConvertPdfService createDocumentListMock(List<Document> convertResult) {
		return new MockTraditionalConvertPdfService().setResultList(convertResult);
	}
	
	public static MockTraditionalConvertPdfService createDocumentListMock(DocumentFactory documentFactory, List<Document> convertResult) {
		return new MockTraditionalConvertPdfService(documentFactory).setResultList(convertResult);
	}
	
	public static MockTraditionalConvertPdfService createDocumentMock(Document convertResult) {
		return new MockTraditionalConvertPdfService().setResult(convertResult);
	}
	
	public static MockTraditionalConvertPdfService createDocumentMock(DocumentFactory documentFactory, Document convertResult) {
		return new MockTraditionalConvertPdfService(documentFactory).setResult(convertResult);
	}
	
	public MockTraditionalConvertPdfService setResultList(List<Document> resultList) {
		this.resultList = resultList;
		return this;
	}
	
	public MockTraditionalConvertPdfService setResult(Document result) {
		this.result = result;
		return this;
	}

	@Override
	public List<Document> toImage(Document inPdfDoc, ToImageOptionsSpec toImageOptionsSpec) throws ConvertPdfServiceException {
		this.toImageArgs = new ToImageArgs(inPdfDoc, toImageOptionsSpec);
		return this.resultList == null ? DUMMY_DOCUMENT_LIST : this.resultList;
	}

	@Override
	public Document toPS(Document inPdfDoc, ToPSOptionsSpec toPSOptionsSpec) throws ConvertPdfServiceException {
		this.toPSArgs = new ToPSArgs(inPdfDoc, toPSOptionsSpec);
		return this.result == null ? DUMMY_DOCUMENT : this.result;
	}
	
	public ToImageArgs getToImageArgs() {
		return this.toImageArgs;
	}
	
	public ToPSArgs getToPSArgs() {
		return this.toPSArgs;
	}
	
	public static class ToImageArgs {
		private final Document inPdfDoc;
		private final ToImageOptionsSpec toImageOptionsSpec;
		
		public ToImageArgs(Document inPdfDoc, ToImageOptionsSpec toImageOptionsSpec) {
			super();
			this.inPdfDoc = inPdfDoc;
			this.toImageOptionsSpec = toImageOptionsSpec;
		}

		public Document getInPdfDoc() {
			return inPdfDoc;
		}

		public ToImageOptionsSpec getToImageOptionsSpec() {
			return toImageOptionsSpec;
		}
	}
	
	public static class ToPSArgs {
		private final Document inPdfDoc;
		private final ToPSOptionsSpec toPSOptionsSpec;
		
		public ToPSArgs(Document inPdfDoc, ToPSOptionsSpec toPSOptionsSpec) {
			super();
			this.inPdfDoc = inPdfDoc;
			this.toPSOptionsSpec = toPSOptionsSpec;
		}

		public Document getInPdfDoc() {
			return inPdfDoc;
		}

		public ToPSOptionsSpec getToPSOptionsSpec() {
			return toPSOptionsSpec;
		}
	}

}
