package com._4point.aem.fluentforms.testing.convertPdf;

import java.util.List;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.convertPdf.ConvertPdfService.ConvertPdfServiceException;
import com._4point.aem.fluentforms.api.convertPdf.ToImageOptionsSpec;
import com._4point.aem.fluentforms.api.convertPdf.ToPSOptionsSpec;
import com._4point.aem.fluentforms.impl.convertPdf.TraditionalConvertPdfService;

public class ExceptionalMockTraditionalConvertPdfService implements TraditionalConvertPdfService {
	private final String message;
	
	private ExceptionalMockTraditionalConvertPdfService(String message) {
		super();
		this.message = message;
	}

	@Override
	public List<Document> toImage(Document inPdfDoc, ToImageOptionsSpec toImageOptionsSpec) throws ConvertPdfServiceException {
		throw new ConvertPdfServiceException(this.message);
	}

	@Override
	public Document toPS(Document inPdfDoc, ToPSOptionsSpec toPSOptionsSpec) throws ConvertPdfServiceException {
		throw new ConvertPdfServiceException(this.message);
	}

}
