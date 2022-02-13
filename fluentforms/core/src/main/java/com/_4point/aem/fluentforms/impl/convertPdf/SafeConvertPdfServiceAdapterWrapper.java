package com._4point.aem.fluentforms.impl.convertPdf;

import java.util.List;
import java.util.Objects;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.convertPdf.ConvertPdfService.ConvertPdfServiceException;
import com._4point.aem.fluentforms.api.convertPdf.ToImageOptionsSpec;
import com._4point.aem.fluentforms.api.convertPdf.ToPSOptionsSpec;

public class SafeConvertPdfServiceAdapterWrapper implements TraditionalConvertPdfService {

	private final TraditionalConvertPdfService convertPdfService;
	
	public SafeConvertPdfServiceAdapterWrapper(TraditionalConvertPdfService convertPdfService) {
		super();
		this.convertPdfService = convertPdfService;
	}

	@Override
	public List<Document> toImage(Document inPdfDoc, ToImageOptionsSpec toImageOptionsSpec) throws ConvertPdfServiceException {
		Objects.requireNonNull(inPdfDoc, "inPdfDoc parameter cannot be null.");
		Objects.requireNonNull(toImageOptionsSpec, "ToImageOptionsSpec parameter cannot be null.");
		return convertPdfService.toImage(inPdfDoc, toImageOptionsSpec);
	}

	@Override
	public Document toPS(Document inPdfDoc, ToPSOptionsSpec toPSOptionsSpec) throws ConvertPdfServiceException {
		Objects.requireNonNull(inPdfDoc, "inPdfDoc parameter cannot be null.");
		Objects.requireNonNull(toPSOptionsSpec, "ToPSOptionsSpec parameter cannot be null.");
		return convertPdfService.toPS(inPdfDoc, toPSOptionsSpec);
	}

	// This is required by the mock services.
	public TraditionalConvertPdfService getConvertPdfService() {
		return convertPdfService;
	}
}
