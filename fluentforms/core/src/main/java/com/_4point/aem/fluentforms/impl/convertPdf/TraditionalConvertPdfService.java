package com._4point.aem.fluentforms.impl.convertPdf;

import java.util.List;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.convertPdf.ConvertPdfService.ConvertPdfServiceException;
import com._4point.aem.fluentforms.api.convertPdf.ToImageOptionsSpec;
import com._4point.aem.fluentforms.api.convertPdf.ToPSOptionsSpec;

public interface TraditionalConvertPdfService {

	List<Document> toImage(Document inPdfDoc, ToImageOptionsSpec toImageOptionsSpec) throws ConvertPdfServiceException;
	
	Document toPS(Document inPdfDoc, ToPSOptionsSpec toPSOptionsSpec) throws ConvertPdfServiceException;
}
