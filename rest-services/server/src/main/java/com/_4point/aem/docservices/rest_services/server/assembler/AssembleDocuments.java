package com._4point.aem.docservices.rest_services.server.assembler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.httpclient.methods.multipart.Part;
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
import org.w3c.dom.Element;

import com._4point.aem.docservices.rest_services.server.AcceptHeaders;
import com._4point.aem.docservices.rest_services.server.ContentType;
import com._4point.aem.docservices.rest_services.server.ServletUtils;
import com._4point.aem.docservices.rest_services.server.Exceptions.BadRequestException;
import com._4point.aem.docservices.rest_services.server.Exceptions.InternalServerErrorException;
import com._4point.aem.docservices.rest_services.server.Exceptions.NotAcceptableException;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.api.assembler.AssemblerService;
import com._4point.aem.fluentforms.api.assembler.AssemblerService.AssemblerArgumentBuilder;
import com._4point.aem.fluentforms.api.assembler.AssemblerService.AssemblerServiceException;
import com._4point.aem.fluentforms.impl.UsageContext;
import com._4point.aem.fluentforms.impl.assembler.AdobeDocAssemblerServiceAdapter;
import com._4point.aem.fluentforms.impl.assembler.AssemblerServiceImpl;
import com._4point.aem.fluentforms.impl.assembler.TraditionalDocAssemblerService;

@SuppressWarnings("serial")
@Component(service = Servlet.class, property = {
		Constants.SERVICE_DESCRIPTION + "=FormsService.AssembleDocuments Service",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST })
@SlingServletPaths("/services/AssemblerService/AssembleDocuments")
public class AssembleDocuments extends SlingAllMethodsServlet {

	private static final Logger log = LoggerFactory.getLogger(AssembleDocuments.class);
	private static final String DDX = "ddx";
	private static final String SOURCE_DOCUMENT_KEY = "sourceDocumentMap.key";
	private static final String SOURCE_DOCUMENT_VALUE = "sourceDocumentMap.value";
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
		log.info("In Assemble Uploaded Files");
		try {
			Map<String, Object> sourceDocuments = new HashMap<String, Object>();
           // createSourceDocumentMap(sourceDocuments, request);
			RequestParameter parameter = request.getRequestParameter(DDX);			
			  RequestParameter[] soruceDocName= request.getRequestParameters(SOURCE_DOCUMENT_KEY);
              RequestParameter[] sourceDocs= request.getRequestParameters(SOURCE_DOCUMENT_VALUE);
			  if(soruceDocName.length == sourceDocs.length) { 
				  log.info("sourceDocs Size : "+sourceDocs.length);
				  for (int i = 0; i < soruceDocName.length; i++) {
					  log.info("Document Name: "+soruceDocName[i].toString());
					  log.info("Content Type: "+sourceDocs[i].getContentType());
			          sourceDocuments.put(soruceDocName[i].getString(), docFactory.create(sourceDocs[i].get()));
			  } 
		  }
			 
			 
			Boolean isFailonError = false;
			
			Document ddx = docFactory.create(parameter.get());
		
			AssemblerArgumentBuilder argumentBuilder = assemblerService.invoke()
					.transform(b -> isFailonError == null ? b : b.setFailOnError(isFailonError));
			try (AssemblerResult assemblerResult = argumentBuilder.executeOn(ddx, sourceDocuments)) {
				log.info("assemblerResult : " +assemblerResult);
				//String assemblerResultxml = convertAssemblerResultToxml(assemblerResult);
				//response.setContentType(ContentType.APPLICATION_XML.getContentTypeStr());
				//response.getWriter().write(assemblerResultxml);
				//Document result = null;
				String contentType = ContentType.APPLICATION_PDF.toString();	// We know the result is always PDF.
				ServletUtils.validateAcceptHeader(request.getHeader(AcceptHeaders.ACCEPT_HEADER_STR), contentType);
				response.setContentType(contentType);
				assemblerResult.getDocuments().forEach((docName, mergedPDf) ->{
					Document result  = mergedPDf;
					try {
						ServletUtils.transfer(result.getInputStream(), response.getOutputStream());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
				
			
				
			}

		} catch (AssemblerServiceException e) {
			throw new InternalServerErrorException("Internal Error while merging PDF. (" + e.getMessage() + ").", e);
		} catch (Exception e) {
			log.error("Error while Merging pdfs " + e.getMessage());
		}

	}

	private String convertAssemblerResultToxml(AssemblerResult assemblerResult) throws AssemblerServiceException
		{
		log.info("Converting assemblerresult to xml");
		try {
		Map<String, Document> resultDocMap = assemblerResult.getDocuments();
		DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
		org.w3c.dom.Document document = documentBuilder.newDocument();
		org.w3c.dom.Element root = document.createElement("assemblerResult");
		document.appendChild(root);
		for (Entry<String, Document> resultDoc : resultDocMap.entrySet()) {
			log.info("Result pdf name : " +resultDoc.getKey());
			Element result = document.createElement("resultDocument");
			root.appendChild(result);
			Attr attr = document.createAttribute("documentName");
			attr.setValue(resultDoc.getKey());
			result.setAttributeNode(attr);
			Document concatenatedDoc = docFactory.create(resultDoc.getValue().getInputStream());
			log.info("map size " +resultDocMap.size());
			//log.info(resultDoc.getKey() + " is of type " +resultDoc.getValue().getContentType());
			String decoded = Base64.getEncoder().encodeToString(concatenatedDoc.getInlineData());
			Element mergedDoc = document.createElement("mergedDoc");
			mergedDoc.appendChild(document.createTextNode(decoded));
			result.appendChild(mergedDoc);
		}
		DOMSource domSource = new DOMSource(document);
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		StringWriter sw = new StringWriter();
		StreamResult sr = new StreamResult(sw);
		transformer.transform(domSource, sr);
		return sw.toString();
		} catch (Exception e) {
			throw new AssemblerServiceException("Error while converting assemblerResult to xml ", e);
		}
	}

	private TraditionalDocAssemblerService getAdobeAssemblerService() {
		return new AdobeDocAssemblerServiceAdapter(adobeAssembleService, docFactory);
	}
	
}
