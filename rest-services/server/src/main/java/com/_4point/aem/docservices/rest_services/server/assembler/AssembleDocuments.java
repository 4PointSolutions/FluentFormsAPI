package com._4point.aem.docservices.rest_services.server.assembler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com._4point.aem.docservices.rest_services.server.AcceptHeaders;
import com._4point.aem.docservices.rest_services.server.Exceptions.BadRequestException;
import com._4point.aem.docservices.rest_services.server.Exceptions.InternalServerErrorException;
import com._4point.aem.docservices.rest_services.server.Exceptions.NotAcceptableException;
import com._4point.aem.docservices.rest_services.server.ServletUtils;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSpec;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.api.assembler.AssemblerService;
import com._4point.aem.fluentforms.api.assembler.AssemblerService.AssemblerServiceException;
import com._4point.aem.fluentforms.impl.UsageContext;
import com._4point.aem.fluentforms.impl.assembler.AdobeDocAssemblerServiceAdapter;
import com._4point.aem.fluentforms.impl.assembler.AssemblerOptionsSpecImpl;
import com._4point.aem.fluentforms.impl.assembler.AssemblerServiceImpl;
import com._4point.aem.fluentforms.impl.assembler.TraditionalDocAssemblerService;

@SuppressWarnings("serial")
@Component(service = Servlet.class, property = {
		Constants.SERVICE_DESCRIPTION + "=FormsService.AssembleDocuments Service",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST })
@SlingServletPaths("/services/AssemblerService/AssembleDocuments")
public class AssembleDocuments extends SlingAllMethodsServlet {

	private static final Logger log = LoggerFactory.getLogger(AssembleDocuments.class);

	@Reference
	private com.adobe.fd.assembler.service.AssemblerService adobeAssembleService;

	private final Supplier<TraditionalDocAssemblerService> assemblerServiceFactory = this::getAdobeAssemblerService;
	

	private final DocumentFactory docFactory = DocumentFactory.getDefault();

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
			AssemblerService assemblerService = new AssemblerServiceImpl(assemblerServiceFactory.get(),
				UsageContext.SERVER_SIDE);
		log.debug("In Assemble Uploaded Files");
		try {
			Map<String, Object> sourceDocuments = new HashMap<String, Object>();
			InputStream dDXFile = createDdxFile(request, sourceDocuments);
			Document ddx = docFactory.create(dDXFile);
		
			try (AssemblerResult assemblerResult  = executeOn(ddx, sourceDocuments, assemblerService)) {
				Map<String, Document> allDocsReturned = assemblerResult.getDocuments();

				Document concatenatedDoc = null;
				for (Entry<String, Document> docsMap : allDocsReturned.entrySet()) {
					String concatenatedPDFName = (String) docsMap.getKey();
					if (concatenatedPDFName.equalsIgnoreCase("concatenatedPDF.pdf")) {
						Object pdf = docsMap.getValue();
						concatenatedDoc = (Document) pdf;
						break;
					}
				}
				String contentType = concatenatedDoc.getContentType();
				ServletUtils.validateAcceptHeader(request.getHeader(AcceptHeaders.ACCEPT_HEADER_STR), contentType);
				response.setContentType(contentType);
				response.setContentLength((int) concatenatedDoc.length());
				ServletUtils.transfer(concatenatedDoc.getInputStream(), response.getOutputStream());
			}
		} catch (AssemblerServiceException | IOException e) {
			throw new InternalServerErrorException("Internal Error while merging PDF. (" + e.getMessage() + ").", e);
		} catch (Exception e) {
			log.error("Error while Merging pdfs " + e.getMessage());
		}

	}

	private InputStream createDdxFile(SlingHttpServletRequest request, Map<String, Object> sourceDocuments)
			throws Exception {
		InputStream xmlInputStream = null;

		final boolean isMultipart = org.apache.commons.fileupload.servlet.ServletFileUpload.isMultipartContent(request);
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			org.w3c.dom.Document ddx = docBuilder.newDocument();
			Element mainRootElement = ddx.createElementNS("http://ns.adobe.com/DDX/1.0/", "DDX");
			ddx.appendChild(mainRootElement);
			mainRootElement.appendChild(getPDFNodes(ddx, isMultipart, sourceDocuments, request));

			DOMSource domSource = new DOMSource(ddx);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			Result outputTarget = new StreamResult(outputStream);

			TransformerFactory.newInstance().newTransformer().transform(domSource, outputTarget);
			xmlInputStream = new ByteArrayInputStream(outputStream.toByteArray());

		} catch (Exception e) {
			throw new Exception("Error creating ddx xml file", e);
		}

		return xmlInputStream;
	}

	private Node getPDFNodes(org.w3c.dom.Document ddx, boolean isMultipart, Map<String, Object> mapOfDocuments,
			SlingHttpServletRequest request) throws InternalServerErrorException {
		Element pdfResult = ddx.createElement("PDF");
		pdfResult.setAttribute("result", "concatenatedPDF.pdf");
		if (isMultipart) {
			Map<String, org.apache.sling.api.request.RequestParameter[]> params = request.getRequestParameterMap();
			for (Map.Entry<String, org.apache.sling.api.request.RequestParameter[]> pairs : params.entrySet()) {
				final RequestParameter[] pArr = pairs.getValue();
				final RequestParameter param = pArr[0];

				try {
					if (!param.isFormField()) {
						final InputStream stream = param.getInputStream();
						log.debug("the file name is " + param.getFileName());
						log.debug("Got input Stream inside my servlet####" + stream.available());
						Document document = docFactory.create(stream);
						mapOfDocuments.put(param.getFileName(), document);
						Element pdfSourceElement = ddx.createElement("PDF");
						pdfSourceElement.setAttribute("source", param.getFileName() + ".pdf");
						pdfResult.appendChild(pdfSourceElement);
						log.debug("The map size is " + mapOfDocuments.size());
					} else {
						log.debug("The form field is" + param.getString());

					}
				} catch (IOException e) {
					throw new InternalServerErrorException("Internal Error while merging PDF. (" + e.getMessage() + ").", e);			}

			}
		}
		return pdfResult;
	}
	
	
	private AssemblerResult executeOn(Document ddx, Map<String, Object> sourceDocuments, AssemblerService assemblerService)
			throws AssemblerServiceException, IOException {
        AssemblerOptionsSpec assemblerOptionSpec = new AssemblerOptionsSpecImpl();
		log.info("FailonError=" + assemblerOptionSpec.isFailOnError());

		AssemblerResult assemblerResult = assemblerService.invoke(ddx, sourceDocuments, assemblerOptionSpec);
		
		return assemblerResult;
	}


	private TraditionalDocAssemblerService getAdobeAssemblerService() {
		return new AdobeDocAssemblerServiceAdapter(adobeAssembleService, docFactory);
	}

}
