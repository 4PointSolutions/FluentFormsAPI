package com._4point.aem.docservices.rest_services.server.assembler;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
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

import com._4point.aem.docservices.rest_services.server.ContentType;
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
import com._4point.aem.fluentforms.impl.assembler.AdobeAssemblerServiceAdapter;
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
	private static final String IS_FAIL_ON_ERROR = "isFailOnError";

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
			RequestParameter parameter = request.getRequestParameter(DDX);
			RequestParameter[] soruceDocName = request.getRequestParameters(SOURCE_DOCUMENT_KEY);
			RequestParameter[] sourceDocs = request.getRequestParameters(SOURCE_DOCUMENT_VALUE);
			if (soruceDocName.length == sourceDocs.length) {
				log.info("sourceDocs Size : " + sourceDocs.length);
				for (int i = 0; i < soruceDocName.length; i++) {
					log.info("Document Name: " + soruceDocName[i].toString());
					log.info("Content Type: " + sourceDocs[i].getContentType());
					sourceDocuments.put(soruceDocName[i].getString(), docFactory.create(sourceDocs[i].get()));
				}
			}
			RequestParameter isFailonError = request.getRequestParameter(IS_FAIL_ON_ERROR);
			Document ddx = docFactory.create(parameter.get());
			AssemblerArgumentBuilder argumentBuilder = assemblerService.invoke().transform(
					b -> isFailonError == null ? b : b.setFailOnError(Boolean.valueOf(isFailonError.toString())));
			try (AssemblerResult assemblerResult = argumentBuilder.executeOn(ddx, sourceDocuments)) {
				log.info("assemblerResult : " + assemblerResult);
				String assemblerResultxml = convertAssemblerResultToxml(assemblerResult);
				response.setContentType(ContentType.APPLICATION_XML.getContentTypeStr());
				response.getWriter().write(assemblerResultxml);
			}

		} catch (AssemblerServiceException e) {
			throw new InternalServerErrorException("Internal Error while merging PDF. (" + e.getMessage() + ").", e);
		} catch (Exception e) {
			log.error("Error while Merging pdfs " + e.getMessage());
		}

	}

	private String convertAssemblerResultToxml(AssemblerResult assemblerResult) throws AssemblerServiceException {
		log.info("Converting assemblerresult to xml");
		try {
			Map<String, Document> resultDocMap = assemblerResult.getDocuments();
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
			org.w3c.dom.Document document = documentBuilder.newDocument();
			org.w3c.dom.Element root = document.createElement("assemblerResult");
			document.appendChild(root);
			if (MapUtils.isNotEmpty(resultDocMap)) {
				resultDocMap.forEach((docName, resultDoc) -> {
					log.info("Result pdf name : " + docName);
					Element result = document.createElement("resultDocument");
					root.appendChild(result);
					Attr attr = document.createAttribute("documentName");
					attr.setValue(docName);
					result.setAttributeNode(attr);
					Document concatenatedDoc = resultDoc;
					if (resultDoc != null) {
						concatenatedDoc.setContentType(ContentType.APPLICATION_PDF.toString());
						log.info("converting to bytarray");
						byte[] concatenatedPdf = null;
						try {
							concatenatedPdf = org.apache.commons.io.IOUtils
									.toByteArray(concatenatedDoc.getInputStream());
							if (concatenatedPdf != null) {
								String doc = Base64.getEncoder().encodeToString(concatenatedPdf);
								Element mergedDoc = document.createElement("mergedDoc");
								mergedDoc.appendChild(document.createTextNode(doc));
								result.appendChild(mergedDoc);
							}
						} catch (IOException e) {
							log.error("Error in converting concatenatedPdf to String");
						};
					}
					
					  Element failedBlockName = document.createElement("failedBlockNames");
					  root.appendChild(failedBlockName);
					  if(CollectionUtils.isNotEmpty(assemblerResult.getFailedBlockNames())) {
					  assemblerResult.getFailedBlockNames().forEach(blocName -> { Element
					  failedBlock = document.createElement("failedBlock");
					  failedBlock.appendChild(document.createTextNode(blocName));
					  failedBlockName.appendChild(failedBlock); 
					   }); 
					  }
					  
					  Element latestBatesNum = document.createElement("latestBatesNumber");
					  root.appendChild(latestBatesNum); Attr latestBatesNumberAttr =
					  document.createAttribute("Number");
					  latestBatesNumberAttr.setValue(Integer.toString(assemblerResult.
					  getLastBatesNumber()));
					  latestBatesNum.setAttributeNode(latestBatesNumberAttr);
					  
					  Element numRequestedBlock = document.createElement("numRequestedBlocks");
					  root.appendChild(numRequestedBlock); Attr numRequestedBlockAttr =
					  document.createAttribute("numRequestedBlock");
					  numRequestedBlockAttr.setValue(Integer.toString(assemblerResult.
					  getNumRequestedBlocks()));
					  latestBatesNum.setAttributeNode(numRequestedBlockAttr);
					  
					  Element successfulDocumentName =
					  document.createElement("successfulDocumentNames");
					  root.appendChild(successfulDocumentName);
					  if(CollectionUtils.isNotEmpty(assemblerResult.getSuccessfulBlockNames())) {
					  assemblerResult.getSuccessfulBlockNames().forEach(successFullDocName -> {
					  Element successfulDoc = document.createElement("successfulDocumentName");
					  successfulDoc.appendChild(document.createTextNode(successFullDocName));
					  successfulDocumentName.appendChild(successfulDoc); }); }
					  
					  Element successfulBlocNames = document.createElement("successfulBlocNames");
					  root.appendChild(successfulBlocNames);
					  if(CollectionUtils.isNotEmpty(assemblerResult.getSuccessfulBlockNames())) {
					  assemblerResult.getSuccessfulBlockNames().forEach(blocName -> { Element
					  successfulBloc = document.createElement("successfulBlocName");
					  successfulBloc.appendChild(document.createTextNode(blocName));
					  successfulBlocNames.appendChild(successfulBloc); }); }
					  
					  Element multipleResultBloc = document.createElement("multipleResultBlocs");
					  root.appendChild(multipleResultBloc);
					  if(MapUtils.isNotEmpty(assemblerResult.getMultipleResultsBlocks())) {
					  assemblerResult.getMultipleResultsBlocks().forEach((blockName,docNames)-> {
					  Element resultBlockName = document.createElement("resultBlockName");
					  root.appendChild(resultBlockName); Attr nameAttr =
					  document.createAttribute("name"); nameAttr.setValue(blockName);
					  resultBlockName.setAttributeNode(nameAttr); Element documentNames =
					  document.createElement("documentNames"); root.appendChild(documentNames);
					  
					  }); }
					  
					  Element joblog = document.createElement("jobLog"); root.appendChild(joblog);
					  if(assemblerResult.getJobLog() !=null) { Attr logAttr =
					  document.createAttribute("joblogValue"); try {
					  logAttr.setValue(Base64.getEncoder().encodeToString(assemblerResult.getJobLog
					  ().getInlineData())); } catch (DOMException | IOException e) {
					  log.error("Error in converting jobLog to bas64 String"); }
					  joblog.setAttributeNode(logAttr); joblog.setAttributeNode(logAttr);
					  }				 
				});
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
		return new AdobeAssemblerServiceAdapter(adobeAssembleService, docFactory);
	}

}
