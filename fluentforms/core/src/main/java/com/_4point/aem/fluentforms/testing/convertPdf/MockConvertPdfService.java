package com._4point.aem.fluentforms.testing.convertPdf;

import java.util.List;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.impl.convertPdf.ConvertPdfServiceImpl;
import com._4point.aem.fluentforms.impl.convertPdf.SafeConvertPdfServiceAdapterWrapper;
import com._4point.aem.fluentforms.impl.convertPdf.TraditionalConvertPdfService;
import com._4point.aem.fluentforms.testing.convertPdf.MockTraditionalConvertPdfService.ToImageArgs;
import com._4point.aem.fluentforms.testing.convertPdf.MockTraditionalConvertPdfService.ToPSArgs;

public class MockConvertPdfService extends ConvertPdfServiceImpl {

	public MockConvertPdfService() {
		super(new MockTraditionalConvertPdfService());
	}

	public MockConvertPdfService(DocumentFactory documentFactory) {
		super(new MockTraditionalConvertPdfService(documentFactory));
	}

	public MockConvertPdfService(TraditionalConvertPdfService adobeConvertPdfService) {
		super(adobeConvertPdfService);
	}
	
	private MockTraditionalConvertPdfService getMockService() {
		return (MockTraditionalConvertPdfService)((SafeConvertPdfServiceAdapterWrapper)(this.getAdobeConvertPdfService())).getConvertPdfService();
	}

	public MockConvertPdfService setToImageResultList(List<Document> result) {
		getMockService().setResultList(result);
		return this;
	}

	public MockConvertPdfService setToPSResult(Document result) {
		getMockService().setResult(result);
		return this;
	}

	public static MockConvertPdfService createToImageMock(List<Document> toImageResult) {
		return new MockConvertPdfService(new MockTraditionalConvertPdfService().setResultList(toImageResult));
	}

	public static MockConvertPdfService createToPSMock(Document toPSResult) {
		return new MockConvertPdfService(new MockTraditionalConvertPdfService().setResult(toPSResult));
	}
	
	public ToImageArgs getToImageArgs() {
		return getMockService().getToImageArgs();
	}

	public ToPSArgs getToPSArgs() {
		return getMockService().getToPSArgs();
	}

}
