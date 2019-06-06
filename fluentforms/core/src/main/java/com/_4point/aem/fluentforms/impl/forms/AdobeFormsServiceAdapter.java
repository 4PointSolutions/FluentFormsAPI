package com._4point.aem.fluentforms.impl.forms;

import static com._4point.aem.fluentforms.impl.BuilderUtils.setIfNotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com._4point.aem.fluentforms.api.AbsoluteOrRelativeUrl;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.forms.FormsService.FormsServiceException;
import com._4point.aem.fluentforms.api.forms.PDFFormRenderOptions;
import com._4point.aem.fluentforms.api.forms.ValidationOptions;
import com._4point.aem.fluentforms.api.forms.ValidationResult;
import com.adobe.fd.forms.api.DataFormat;

public class AdobeFormsServiceAdapter implements TraditionalFormsService {

	private static final Logger log = LoggerFactory.getLogger(AdobeFormsServiceAdapter.class);


	private final DocumentFactory documentFactory;

	private final com.adobe.fd.forms.api.FormsService adobeFormsService;

	public AdobeFormsServiceAdapter(com.adobe.fd.forms.api.FormsService adobeFormsService) {
		super();
		this.documentFactory = DocumentFactory.getDefault();
		this.adobeFormsService = Objects.requireNonNull(adobeFormsService, "adobeFormsService is null.");
	}

	public AdobeFormsServiceAdapter(DocumentFactory documentFactory, com.adobe.fd.forms.api.FormsService adobeFormsService) {
		super();
		this.documentFactory = Objects.requireNonNull(documentFactory, "documentFactory is null");
		this.adobeFormsService = Objects.requireNonNull(adobeFormsService, "adobeFormsService is null");
	}

	@Override
	public Document exportData(Document pdfOrXdp, DataFormat dataFormat) throws FormsServiceException {
		try {
			return documentFactory.create(adobeFormsService.exportData(pdfOrXdp.getAdobeDocument(), dataFormat));
		} catch (com.adobe.fd.forms.api.FormsServiceException e) {
			throw new FormsServiceException(e);
		}
	}

	@Override
	public Document importData(Document pdf, Document data) throws FormsServiceException {
		try {
			return documentFactory.create(adobeFormsService.importData(pdf.getAdobeDocument(), data.getAdobeDocument()));
		} catch (com.adobe.fd.forms.api.FormsServiceException e) {
			throw new FormsServiceException(e);
		}
	}

	@Override
	public Document renderPDFForm(String urlOrfilename, Document data, PDFFormRenderOptions pdfFormRenderOptions) throws FormsServiceException {
		try {
			log.info("renderPdfForm form='" + urlOrfilename + "', contentRoot='" + pdfFormRenderOptions.getContentRoot() + "'");
			return documentFactory.create(adobeFormsService.renderPDFForm(urlOrfilename, (data != null ? data.getAdobeDocument() : null), toAdobePDFFormRenderOptions(pdfFormRenderOptions)));
		} catch (com.adobe.fd.forms.api.FormsServiceException e) {
			throw new FormsServiceException(e);
		}
	}

	@Override
	public ValidationResult validate(String template, Document data, ValidationOptions validationOptions) throws FormsServiceException {
		try {
			return new ValidationResultImpl(adobeFormsService.validate(template, data.getAdobeDocument(), validationOptions.toAdobeValidationOptions()), documentFactory);
		} catch (com.adobe.fd.forms.api.FormsServiceException e) {
			throw new FormsServiceException(e);
		}
	}
	
	public static com.adobe.fd.forms.api.PDFFormRenderOptions toAdobePDFFormRenderOptions(PDFFormRenderOptions options) {
		com.adobe.fd.forms.api.PDFFormRenderOptions adobeOptions = new com.adobe.fd.forms.api.PDFFormRenderOptions();
		setIfNotNull(adobeOptions::setAcrobatVersion, options.getAcrobatVersion());
		setIfNotNull(adobeOptions::setCacheStrategy, options.getCacheStrategy());
		setIfNotNull((cr)->adobeOptions.setContentRoot(cr.toString()), options.getContentRoot());
		setIfNotNull((dd)->adobeOptions.setDebugDir(dd.toString()), options.getDebugDir());
		setIfNotNull((l)->adobeOptions.setLocale(l.toLanguageTag()), options.getLocale());
		setIfNotNull(adobeOptions::setSubmitUrls, mapToStrings(options.getSubmitUrls()));
		setIfNotNull(adobeOptions::setTaggedPDF, options.getTaggedPDF());
		setIfNotNull((ad)->adobeOptions.setXci(ad.getAdobeDocument()), options.getXci());
		return adobeOptions;
	}

	private static List<String> mapToStrings(List<AbsoluteOrRelativeUrl> urls) {
		return urls == null ? null : urls.stream().map(AbsoluteOrRelativeUrl::toString).collect(Collectors.toList());
	}

}
