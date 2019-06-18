package com._4point.aem.docservices.rest_services.server.docassurance;

import static com._4point.aem.docservices.rest_services.server.FormParameters.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import java.util.function.Supplier;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com._4point.aem.docservices.rest_services.server.AcceptHeaders;
import com._4point.aem.docservices.rest_services.server.Exceptions.BadRequestException;
import com._4point.aem.docservices.rest_services.server.Exceptions.InternalServerErrorException;
import com._4point.aem.docservices.rest_services.server.Exceptions.NotAcceptableException;
import com._4point.aem.docservices.rest_services.server.ServletUtils;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.docassurance.DocAssuranceService;
import com._4point.aem.fluentforms.api.docassurance.DocAssuranceService.DocAssuranceServiceException;
import com._4point.aem.fluentforms.impl.docassurance.AdobeDocAssuranceServiceAdapter;
import com._4point.aem.fluentforms.impl.docassurance.DocAssuranceServiceImpl;
import com._4point.aem.fluentforms.impl.docassurance.TraditionalDocAssuranceService;

@SuppressWarnings("serial")
@Component(service=Servlet.class, property={Constants.SERVICE_DESCRIPTION + "=DocAssuranceService.SecureDocument Service"})
@SlingServletResourceTypes(methods=HttpConstants.METHOD_POST, resourceTypes = { "" })
@SlingServletPaths("/services/DocAssuranceService/SecureDocument")
public class SecureDocument extends SlingAllMethodsServlet {

	private static final Logger log = LoggerFactory.getLogger(SecureDocument.class);

	private final DocumentFactory docFactory = DocumentFactory.getDefault();
	private Supplier<TraditionalDocAssuranceService> docAssuranceServiceFactory;
	private ResourceResolver resourceResolver;

	@Reference
	private com.adobe.fd.docassurance.client.api.DocAssuranceService adobeDocAssuranceService;

	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		try {
			this.processInput(request, response);
		} catch (BadRequestException br) {
			log.warn("Bad Request from the user", br);
			response.sendError(SlingHttpServletResponse.SC_BAD_REQUEST, br.getMessage());
		} catch (InternalServerErrorException ise) {
			log.error("Internal server error", ise);
			response.sendError(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR, ise.getMessage());
		} catch (NotAcceptableException nae) {
			log.error("NotAcceptable error", nae);
			response.sendError(SlingHttpServletResponse.SC_NOT_ACCEPTABLE, nae.getMessage());
		} catch (Exception e) {  			// Some exception we haven't anticipated.
			log.error(e.getMessage() != null ? e.getMessage() : e.getClass().getName() , e);	// Make sure this gets into our log.
			throw e;
		}
	}

	private void processInput(SlingHttpServletRequest request, SlingHttpServletResponse response) throws BadRequestException, InternalServerErrorException, NotAcceptableException {
		DocAssuranceService docAssuranceService = new DocAssuranceServiceImpl(docAssuranceServiceFactory.get());
		
		this.resourceResolver = request.getResourceResolver();
		this.docAssuranceServiceFactory = this::getAdobeDocAssuranceService;

		ReaderExtensionsParameters reqParameters = ReaderExtensionsParameters.readReaderExtensionsParameters(request);
		String credentialAlias = reqParameters.getCredentialAlias();
		Document inDoc = reqParameters.getInDoc() != null ? docFactory.create(reqParameters.getInDoc()) : null;
		String message = reqParameters.getMessage();
		Boolean isModeFinal = reqParameters.getIsModeFinal();
		Boolean enabledBarcodeDecoding = reqParameters.getEnabledBarcodeDecoding();
		Boolean enabledComments = reqParameters.getEnabledComments();
		Boolean enabledCommentsOnline = reqParameters.getEnabledCommentsOnline();
		Boolean enabledDigitalSignatures = reqParameters.getEnabledDigitalSignatures();
		Boolean enabledDynamicFormFields = reqParameters.getEnabledDynamicFormFields();
		Boolean enabledDynamicFormPages = reqParameters.getEnabledDynamicFormPages();
		Boolean enabledEmbeddedFiles = reqParameters.getEnabledEmbeddedFiles();
		Boolean enabledFormDataImportExport = reqParameters.getEnabledFormDataImportExport();
		Boolean enabledFormFillIn = reqParameters.getEnabledFormFillIn();
		Boolean enabledOnlineForms = reqParameters.getEnabledOnlineForms();
		Boolean enabledSubmitStandalone = reqParameters.getEnabledSubmitStandalone();
		
		try {
			// All usage rights will be enabled by default except for Barcode Decoding, Comments and Online Comments.
			try (Document result = docAssuranceService.secureDocument()
									.readerExtensionsOptions(credentialAlias)
									.setReOptions()
										.transform(b->message == null ? b : b.setMessage(message))
										.transform(b->isModeFinal == null ? b : b.setModeFinal(isModeFinal.booleanValue()))
										.setUsageRights()
											.transform(b->enabledBarcodeDecoding == null ? b : b.setEnabledBarcodeDecoding(enabledBarcodeDecoding.booleanValue()))
											.transform(b->enabledComments == null ? b : b.setEnabledComments(enabledComments.booleanValue()))
											.transform(b->enabledCommentsOnline == null ? b : b.setEnabledCommentsOnline(enabledBarcodeDecoding.booleanValue()))
											.transform(b->enabledDigitalSignatures == null ? b.setEnabledDigitalSignatures(true) : b.setEnabledDigitalSignatures(enabledDigitalSignatures.booleanValue()))
											.transform(b->enabledDynamicFormFields == null ? b.setEnabledDynamicFormFields(true) : b.setEnabledDynamicFormFields(enabledDynamicFormFields.booleanValue()))
											.transform(b->enabledDynamicFormPages == null ? b.setEnabledDynamicFormPages(true) : b.setEnabledDynamicFormPages(enabledDynamicFormPages.booleanValue()))
											.transform(b->enabledEmbeddedFiles == null ? b.setEnabledEmbeddedFiles(true) : b.setEnabledEmbeddedFiles(enabledEmbeddedFiles.booleanValue()))
											.transform(b->enabledFormDataImportExport == null ? b.setEnabledFormDataImportExport(true) : b.setEnabledFormDataImportExport(enabledFormDataImportExport.booleanValue()))
											.transform(b->enabledFormFillIn == null ? b.setEnabledFormFillIn(true) : b.setEnabledFormFillIn(enabledFormFillIn.booleanValue()))
											.transform(b->enabledOnlineForms == null ? b.setEnabledOnlineForms(true) : b.setEnabledOnlineForms(enabledOnlineForms.booleanValue()))
											.transform(b->enabledSubmitStandalone == null ? b.setEnabledSubmitStandalone(true) : b.setEnabledSubmitStandalone(enabledSubmitStandalone.booleanValue()))
											.done()
										.done()
									.done()
									.executeOn(inDoc)) {
				String contentType = result.getContentType();
				ServletUtils.validateAcceptHeader(request.getHeader(AcceptHeaders.ACCEPT_HEADER_STR), contentType);
				response.setContentType(contentType);
				response.setContentLength((int)result.length());
				ServletUtils.transfer(result.getInputStream(), response.getOutputStream());
			}
		} catch (FileNotFoundException | NullPointerException ex1) {
			throw new BadRequestException("Bad request parameter while reader extending a PDF (" + ex1.getMessage() + ").", ex1);
		} catch (DocAssuranceServiceException | IOException ex2) {
			throw new InternalServerErrorException("Internal Error while reader extending a PDF.", ex2);
		} catch (IllegalArgumentException ex3) {
			throw new BadRequestException("Bad arguments while reader extending a PDF", ex3);
		}
				
	}

	private TraditionalDocAssuranceService getAdobeDocAssuranceService() {
		return new AdobeDocAssuranceServiceAdapter(this.adobeDocAssuranceService, this.resourceResolver);
	}

	private static class ReaderExtensionsParameters {
		private static final String CREDENTIAL_ALIAS_PARAM = "credentialAlias";
		private static final String DOCUMENT_PARAM = "inDoc";
		private static final String MESSAGE_PARAM = "message";
		private static final String IS_MODE_FINAL_PARAM = "isModeFinal";
		private static final String ENABLED_BARCODED_DECODING_PARAM = "usageRights.enabledBarcodedDecoding";
		private static final String ENABLED_COMMENTS_PARAM = "usageRights.enabledComments";
		private static final String ENABLED_COMMENTS_ONLINE_PARAM = "usageRights.enabledCommentsOnline";
		private static final String ENABLED_DIGITAL_SIGNATURES_PARAM = "usageRights.enabledDigitalSignatures";
		private static final String ENABLED_DYNAMIC_FORM_FIELDS_PARAM = "usageRights.enabledDynamicFormFields";
		private static final String ENABLED_DYNAMIC_FORM_PAGES_PARAM = "usageRights.enabledDynamicFormPages";
		private static final String ENABLED_EMBEDDED_FILES_PARAM = "usageRights.enabledEmbeddedFiles";
		private static final String ENABLED_FORM_DATA_IMPORT_EXPORT_PARAM = "usageRights.enabledFormDataImportExport";
		private static final String ENABLED_FORM_FILL_IN_PARAM = "usageRights.enabledFormFillIn";
		private static final String ENABLED_ONLINE_FORMS_PARAM = "usageRights.enabledOnlineForms";
		private static final String ENABLED_SUBMIT_STANDALONE_PARAM = "usageRights.enabledSubmitStandalone";
		
		private final byte[] inDoc;
		private final String credentialAlias;

		private String message = null;
		private Boolean isModeFinal = null;
		private Boolean enabledBarcodeDecoding = null;
		private Boolean enabledComments = null;
		private Boolean enabledCommentsOnline = null;
		private Boolean enabledDigitalSignatures = null;
		private Boolean enabledDynamicFormFields = null;
		private Boolean enabledDynamicFormPages = null;
		private Boolean enabledEmbeddedFiles = null;
		private Boolean enabledFormDataImportExport = null;
		private Boolean enabledFormFillIn = null;
		private Boolean enabledOnlineForms = null;
		private Boolean enabledSubmitStandalone = null;

		public ReaderExtensionsParameters(byte[] inDoc, String credentialAlias) {
			super();
			this.inDoc = inDoc;
			this.credentialAlias = credentialAlias;
		}

		public String getCredentialAlias() {
			return credentialAlias;
		}

		public byte[] getInDoc() {
			return inDoc;
		}

		public String getMessage() {
			return message;
		}

		private ReaderExtensionsParameters setMessage(String message) {
			this.message = message;
			return this;
		}

		public Boolean getIsModeFinal() {
			return isModeFinal;
		}

		private ReaderExtensionsParameters setIsModeFinal(String isModeFinal) {
			this.isModeFinal = Boolean.valueOf(isModeFinal);
			return this;
		}

		public Boolean getEnabledBarcodeDecoding() {
			return enabledBarcodeDecoding;
		}

		private ReaderExtensionsParameters setEnabledBarcodeDecoding(String enabledBarcodeDecoding) {
			this.enabledBarcodeDecoding = Boolean.valueOf(enabledBarcodeDecoding);
			return this;
		}

		public Boolean getEnabledComments() {
			return enabledComments;
		}

		private ReaderExtensionsParameters setEnabledComments(String enabledComments) {
			this.enabledComments = Boolean.valueOf(enabledComments);
			return this;
		}

		public Boolean getEnabledCommentsOnline() {
			return enabledCommentsOnline;
		}

		private ReaderExtensionsParameters setEnabledCommentsOnline(String enabledCommentsOnline) {
			this.enabledCommentsOnline = Boolean.valueOf(enabledCommentsOnline);
			return this;
		}

		public Boolean getEnabledDigitalSignatures() {
			return enabledDigitalSignatures;
		}

		private ReaderExtensionsParameters setEnabledDigitalSignatures(String enabledDigitalSignatures) {
			this.enabledDigitalSignatures = Boolean.valueOf(enabledDigitalSignatures);
			return this;
		}

		public Boolean getEnabledDynamicFormFields() {
			return enabledDynamicFormFields;
		}

		private ReaderExtensionsParameters setEnabledDynamicFormFields(String enabledDynamicFormFields) {
			this.enabledDynamicFormFields = Boolean.valueOf(enabledDynamicFormFields);
			return this;
		}

		public Boolean getEnabledDynamicFormPages() {
			return enabledDynamicFormPages;
		}

		private ReaderExtensionsParameters setEnabledDynamicFormPages(String enabledDynamicFormPages) {
			this.enabledDynamicFormPages = Boolean.valueOf(enabledDynamicFormPages);
			return this;
		}

		public Boolean getEnabledEmbeddedFiles() {
			return enabledEmbeddedFiles;
		}

		private ReaderExtensionsParameters setEnabledEmbeddedFiles(String enabledEmbeddedFiles) {
			this.enabledEmbeddedFiles = Boolean.valueOf(enabledEmbeddedFiles);
			return this;
		}

		public Boolean getEnabledFormDataImportExport() {
			return enabledFormDataImportExport;
		}

		private ReaderExtensionsParameters setEnabledFormDataImportExport(String enabledFormDataImportExport) {
			this.enabledFormDataImportExport = Boolean.valueOf(enabledFormDataImportExport);
			return this;
		}

		public Boolean getEnabledFormFillIn() {
			return enabledFormFillIn;
		}

		private ReaderExtensionsParameters setEnabledFormFillIn(String enabledFormFillIn) {
			this.enabledFormFillIn = Boolean.valueOf(enabledFormFillIn);
			return this;
		}

		public Boolean getEnabledOnlineForms() {
			return enabledOnlineForms;
		}

		private ReaderExtensionsParameters setEnabledOnlineForms(String enabledOnlineForms) {
			this.enabledOnlineForms = Boolean.valueOf(enabledOnlineForms);
			return this;
		}

		public Boolean getEnabledSubmitStandalone() {
			return enabledSubmitStandalone;
		}

		private ReaderExtensionsParameters setEnabledSubmitStandalone(String enabledSubmitStandalone) {
			this.enabledSubmitStandalone = Boolean.valueOf(enabledSubmitStandalone);
			return this;
		}

		public static ReaderExtensionsParameters readReaderExtensionsParameters(SlingHttpServletRequest request) throws BadRequestException {
			try {

				// PDF document input is mandatory.
				byte[] inputPDF = getMandatoryParameter(request, DOCUMENT_PARAM).get();
				String alias = getMandatoryParameter(request, CREDENTIAL_ALIAS_PARAM).getString();
	
				ReaderExtensionsParameters result = new ReaderExtensionsParameters(inputPDF, alias);
				
				getOptionalParameter(request, MESSAGE_PARAM).ifPresent(rp->result.setMessage(rp.getString()));
				getOptionalParameter(request, IS_MODE_FINAL_PARAM).ifPresent(rp->result.setIsModeFinal(rp.getString()));
				getOptionalParameter(request, ENABLED_BARCODED_DECODING_PARAM).ifPresent(rp->result.setEnabledBarcodeDecoding(rp.getString()));
				getOptionalParameter(request, ENABLED_COMMENTS_PARAM).ifPresent(rp->result.setEnabledComments(rp.getString()));
				getOptionalParameter(request, ENABLED_COMMENTS_ONLINE_PARAM).ifPresent(rp->result.setEnabledCommentsOnline(rp.getString()));
				getOptionalParameter(request, ENABLED_DIGITAL_SIGNATURES_PARAM).ifPresent(rp->result.setEnabledDigitalSignatures(rp.getString()));
				getOptionalParameter(request, ENABLED_DYNAMIC_FORM_FIELDS_PARAM).ifPresent(rp->result.setEnabledDynamicFormFields(rp.getString()));
				getOptionalParameter(request, ENABLED_DYNAMIC_FORM_PAGES_PARAM).ifPresent(rp->result.setEnabledDynamicFormPages(rp.getString()));
				getOptionalParameter(request, ENABLED_EMBEDDED_FILES_PARAM).ifPresent(rp->result.setEnabledEmbeddedFiles(rp.getString()));
				getOptionalParameter(request, ENABLED_FORM_DATA_IMPORT_EXPORT_PARAM).ifPresent(rp->result.setEnabledFormDataImportExport(rp.getString()));
				getOptionalParameter(request, ENABLED_FORM_FILL_IN_PARAM).ifPresent(rp->result.setEnabledFormFillIn(rp.getString()));
				getOptionalParameter(request, ENABLED_ONLINE_FORMS_PARAM).ifPresent(rp->result.setEnabledOnlineForms(rp.getString()));
				getOptionalParameter(request, ENABLED_SUBMIT_STANDALONE_PARAM).ifPresent(rp->result.setEnabledSubmitStandalone(rp.getString()));
				
				return result;
			} catch (IllegalArgumentException e) {
				throw new BadRequestException("There was a problem with one of the incoming parameters.", e);
			}
		}
	}

}
