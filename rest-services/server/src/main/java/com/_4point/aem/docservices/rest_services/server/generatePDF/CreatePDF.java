package com._4point.aem.docservices.rest_services.server.generatePDF;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Base64;
import java.util.function.Supplier;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

import com._4point.aem.docservices.rest_services.server.Exceptions.BadRequestException;
import com._4point.aem.docservices.rest_services.server.Exceptions.InternalServerErrorException;
import com._4point.aem.docservices.rest_services.server.Exceptions.NotAcceptableException;
import com._4point.aem.docservices.rest_services.server.AcceptHeaders;
import com._4point.aem.docservices.rest_services.server.ContentType;
import com._4point.aem.docservices.rest_services.server.FormParameters;
import com._4point.aem.docservices.rest_services.server.ServletUtils;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.generatePDF.CreatePDFResult;
import com._4point.aem.fluentforms.api.generatePDF.GeneratePDFService;
import com._4point.aem.fluentforms.api.generatePDF.GeneratePDFService.CreatePDFResultArgumentBuilder;
import com._4point.aem.fluentforms.api.generatePDF.GeneratePDFService.GeneratePDFServiceException;
import com._4point.aem.fluentforms.impl.AdobeDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.generatePDF.AdobeGeneratePDFServiceAdapter;
import com._4point.aem.fluentforms.impl.generatePDF.GeneratePDFServiceImpl;
import com._4point.aem.fluentforms.impl.generatePDF.PDFSettings;
import com._4point.aem.fluentforms.impl.generatePDF.SecuritySettings;
import com._4point.aem.fluentforms.impl.generatePDF.TraditionalGeneratePDFService;

@SuppressWarnings("serial")
@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=GeneratePdfService.CreatePDF Service",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST })
@SlingServletPaths(ServletUtils.SERVICES_PREFIX + "/GeneratePDFService/CreatePDF")
public class CreatePDF extends SlingAllMethodsServlet {
	private static final Logger log = LoggerFactory.getLogger(CreatePDF.class);
	private final DocumentFactory docFactory = AdobeDocumentFactoryImpl.getFactory();	// We know we're running on AEM, so we'll use the AdobeDocumentFactoryImpl.
	private final Supplier<TraditionalGeneratePDFService> generatePDFServiceFactory = this::getAdobeGeneratePDFService;
	private static final String DATA_PARAM_NAME = "data";
	private static final String FILE_EXTENSION = "fileExtension";
	private static final String FILE_TYPE_SETTINGS = "fileTypeSettings";
	private static final String PDF_SETTINGS = "pdfSettings";
	private static final String SECURITY_SETTINGS = "securitySettings";
	private static final String SETTING_DOC = "settingDoc";
	private static final String XMP_DOC = "xmpDoc";

	@Reference
	com.adobe.pdfg.service.api.GeneratePDFService adobeGeneratePDFService;

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
		} catch (Exception e) { // Some exception we haven't anticipated.
			log.error(e.getMessage() != null ? e.getMessage() : e.getClass().getName(), e); // Make sure this gets into
			// our log.
			throw e;
		}
	}

	private void processInput(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws BadRequestException, InternalServerErrorException, NotAcceptableException {
		try {
			GeneratePDFService generatePDFService = new GeneratePDFServiceImpl(generatePDFServiceFactory.get());
			RequestParameter data = FormParameters.getMandatoryParameter(request, DATA_PARAM_NAME);
			RequestParameter fileExtension = FormParameters.getMandatoryParameter(request, FILE_EXTENSION);
			RequestParameter fileTypeSettings = request.getRequestParameter(FILE_TYPE_SETTINGS);
			RequestParameter pdfSettings = request.getRequestParameter(PDF_SETTINGS);
			RequestParameter securitySettings = request.getRequestParameter(SECURITY_SETTINGS);
			RequestParameter xmpDoc = request.getRequestParameter(XMP_DOC);
			RequestParameter settingDoc = request.getRequestParameter(SETTING_DOC);
			Document inputDoc = docFactory.create(data.get());
			CreatePDFResultArgumentBuilder createPDFResultArgumentBuilder = generatePDFService.createPDF()
					.transform(b -> fileTypeSettings == null ? b : b.setFileTypeSettings(fileTypeSettings.toString()))
					.transform(b -> pdfSettings == null ? b : b.setPdfSetting(PDFSettings.valueOf(pdfSettings.toString())))
					.transform(b -> securitySettings == null ? b : b.setSecuritySetting(SecuritySettings.valueOf(securitySettings.toString())))
					.transform(b -> xmpDoc == null ? b : b.setxmpDoc(docFactory.create(xmpDoc.get())))
					.transform(b -> settingDoc == null ? b : b.setSettingDoc(docFactory.create(settingDoc.get())));
			try (CreatePDFResult createPDFResult = createPDFResultArgumentBuilder.executeOn(inputDoc,
					fileExtension.toString())) {
				String createResultXml = convertCreatePDFResultToXml(createPDFResult);
				String contentType = ContentType.APPLICATION_XML.toString();
				ServletUtils.validateAcceptHeader(request.getHeader(AcceptHeaders.ACCEPT_HEADER_STR), contentType);
				response.setContentType(contentType);
				response.getWriter().write(createResultXml);
			} catch (ParserConfigurationException | TransformerFactoryConfigurationError | TransformerException ex1) {
				throw new InternalServerErrorException("Internal Error while  createPDFResult to xml(" + ex1.getMessage() + ").", ex1);
			}
		} catch (GeneratePDFServiceException | IOException ex1) {
			throw new InternalServerErrorException("Internal Error while converting doc to PDF. (" + ex1.getMessage() + ").", ex1);
		}
	}

	// Package visibility so that it can be unit tested.
	/* package */ static String convertCreatePDFResultToXml(CreatePDFResult createPDFResult)
			throws ParserConfigurationException, InternalServerErrorException, TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
		org.w3c.dom.Document document = documentBuilder.newDocument();
		org.w3c.dom.Element root = document.createElement("createPDFResult");
		document.appendChild(root);
		createElement(document, root, "createdDoc", "createdDocValue", createPDFResult.getCreatedDocument());
		createElement(document, root, "logDoc", "logDocValue", createPDFResult.getLogDocument());
		return domToString(document);
	}

	private static void createElement(org.w3c.dom.Document document, Element root, String parentElementName,
			String attributeName, Document doc) throws DOMException, InternalServerErrorException {
		Element createdDoc = document.createElement(parentElementName);
		root.appendChild(createdDoc);
		if (createdDoc != null && doc != null) {			
			Attr attr = document.createAttribute(attributeName);
			attr.setValue(toBase64String(doc));
			createdDoc.setAttributeNode(attr);
		}
	}

	private static String toBase64String(Document doc) throws InternalServerErrorException {
		try {
			return Base64.getEncoder().encodeToString(IOUtils.toByteArray(doc.getInputStream()));
		} catch (IOException e) {
			String msg = e.getMessage();
			throw new InternalServerErrorException("Error while converting byte array to String ("
					+ (msg == null ? e.getClass().getName() : msg) + ").", e);
		}
	}
	
	private static String domToString(org.w3c.dom.Document document)
			throws TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		DOMSource domSource = new DOMSource(document);
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		StringWriter sw = new StringWriter();
		StreamResult sr = new StreamResult(sw);
		transformer.transform(domSource, sr);
		return sw.toString();
	}

	private TraditionalGeneratePDFService getAdobeGeneratePDFService() {
		return new AdobeGeneratePDFServiceAdapter(adobeGeneratePDFService, docFactory);
	}

}
