package com._4point.aem.fluentforms.impl.forms;

import static com._4point.aem.fluentforms.impl.BuilderUtils.setIfNotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com._4point.aem.fluentforms.api.AbsoluteOrRelativeUrl;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.forms.FormsService.FormsServiceException;
import com._4point.aem.fluentforms.impl.AdobeDocumentFactoryImpl;
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
			return documentFactory.create(adobeFormsService.exportData(AdobeDocumentFactoryImpl.getAdobeDocument(pdfOrXdp), dataFormat));
		} catch (com.adobe.fd.forms.api.FormsServiceException e) {
			throw new FormsServiceException(e);
		}
	}

	@Override
	public Document importData(Document pdf, Document data) throws FormsServiceException {
		try {
			return documentFactory.create(adobeFormsService.importData(AdobeDocumentFactoryImpl.getAdobeDocument(pdf), AdobeDocumentFactoryImpl.getAdobeDocument(data)));
		} catch (com.adobe.fd.forms.api.FormsServiceException e) {
			throw new FormsServiceException(e);
		}
	}

	@Override
	public Document renderPDFForm(String urlOrfilename, Document data, PDFFormRenderOptions pdfFormRenderOptions) throws FormsServiceException {
		try {
			log.info("renderPdfForm form='" + urlOrfilename + "', contentRoot='" + pdfFormRenderOptions.getContentRoot() + "'");
			return documentFactory.create(adobeFormsService.renderPDFForm(urlOrfilename, (data != null ? AdobeDocumentFactoryImpl.getAdobeDocument(data) : null), toAdobePDFFormRenderOptions(pdfFormRenderOptions))).setContentTypeIfEmpty(Document.CONTENT_TYPE_PDF);
		} catch (com.adobe.fd.forms.api.FormsServiceException | IOException e) {
			throw new FormsServiceException(e);
		}
	}

	@Override
	public Document renderPDFForm(Document template, Document data, PDFFormRenderOptions pdfFormRenderOptions)
			throws FormsServiceException {
		// Since the Adobe's FormsService api doesn't accept documents, only filenames, we save this to a temp file and
		// pass in then call AEM with that name.
		try {
			Path tmpFile = Files.createTempFile("fluentforms", ".xdp");
			try {
				template.copyToFile(tmpFile.toFile());
				return this.renderPDFForm(tmpFile.toString(), data, pdfFormRenderOptions);
			}  finally {
				Files.delete(tmpFile);
			}
		} catch (IOException e) {
			throw new FormsServiceException("Error during rendering of template Document object.", e);
		}
	}
	
	@Override
	public ValidationResult validate(String template, Document data, ValidationOptions validationOptions) throws FormsServiceException {
		try {
			return new ValidationResultImpl(adobeFormsService.validate(template, AdobeDocumentFactoryImpl.getAdobeDocument(data), validationOptions.toAdobeValidationOptions()), documentFactory);
		} catch (com.adobe.fd.forms.api.FormsServiceException e) {
			throw new FormsServiceException(e);
		}
	}
	
	public static com.adobe.fd.forms.api.PDFFormRenderOptions toAdobePDFFormRenderOptions(PDFFormRenderOptions options) {
		com.adobe.fd.forms.api.PDFFormRenderOptions adobeOptions = new com.adobe.fd.forms.api.PDFFormRenderOptions();
		setIfNotNull(adobeOptions::setAcrobatVersion, options.getAcrobatVersion());
		setIfNotNull(adobeOptions::setCacheStrategy, options.getCacheStrategy());
		setIfNotNull((cr)->adobeOptions.setContentRoot(
				// Compensate for the fact that AEM doesn't handle relative URLs in the content root because XMLForm.exe uses a different cwd than AEM.
				// See issue #31 for details.
				cr.isPath() ? cr.getPath().toAbsolutePath().toString() : cr.toString()), 
				options.getContentRoot()
				);
		setIfNotNull((dd)->adobeOptions.setDebugDir(dd.toString()), options.getDebugDir());
		setIfNotNull(adobeOptions::setEmbedFonts, options.getEmbedFonts());
		setIfNotNull((l)->adobeOptions.setLocale(l.toLanguageTag()), options.getLocale());
		setIfNotNull(adobeOptions::setRenderAtClient, options.getRenderAtClient());
		setIfNotNull(adobeOptions::setSubmitUrls, mapToStrings(options.getSubmitUrls()));
		setIfNotNull(adobeOptions::setTaggedPDF, options.getTaggedPDF());
		setIfNotNull((ad)->adobeOptions.setXci(AdobeDocumentFactoryImpl.getAdobeDocument(ad)), options.getXci());
		log.info("RenderAtClient=" + adobeOptions.getRenderAtClient().toString());
		log.info("AcrobatVersion=" + adobeOptions.getAcrobatVersion().toString());
		log.info("CacheStrategy=" + adobeOptions.getCacheStrategy().toString());
		log.info("ContentRoot=" + adobeOptions.getContentRoot());
		log.info("DebugDir=" + adobeOptions.getDebugDir());
		log.info("Locale=" + adobeOptions.getRenderAtClient().toString());
		log.info("TaggedPdf=" + adobeOptions.getTaggedPDF());
		log.info("Xci is null=" + Boolean.toString(adobeOptions.getXci() == null));
		final List<String> submitUrls = adobeOptions.getSubmitUrls();
		if (submitUrls != null && submitUrls.size() > 0) { log.info("SubmitUrl=" + submitUrls.get(0)); }
		return adobeOptions;
	}

	private static List<String> mapToStrings(List<AbsoluteOrRelativeUrl> urls) {
		return urls == null ? null : urls.stream().map(AbsoluteOrRelativeUrl::toString).collect(Collectors.toList());
	}

}
