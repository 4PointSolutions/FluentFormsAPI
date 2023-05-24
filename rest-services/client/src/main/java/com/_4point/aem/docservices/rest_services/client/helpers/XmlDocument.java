package com._4point.aem.docservices.rest_services.client.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;

public class XmlDocument {
	private final XPath xpath = XPathFactory.newInstance().newXPath();
	
	private final Document document;
	
    public XmlDocument(Document document) {
		this.document = document;
	}
    
    public static XmlDocument create(InputStream is) throws XmlDocumentException {
    	try {
			return new XmlDocument(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is));
		} catch (SAXException | IOException | ParserConfigurationException e) {
			throw new XmlDocumentException("Error while parsing XmlDocument.", e);
		}
    }

    public String getString(String xPathExpr) throws XmlDocumentException {
    	try {
			return xpath.evaluate(xPathExpr, document);
		} catch (XPathExpressionException e) {
			throw new XmlDocumentException("Error while processing xpath(" + xPathExpr + ").", e);
		}
    }

	public List<String> getStrings(String xPathExpr) throws XmlDocumentException {
		try {
			NodeList nodes = (NodeList) xpath.evaluate(xPathExpr, document, XPathConstants.NODESET);
			// Down the road, improve this with Java 11's Collectors.toUnmodifiableList() or Java 16's Stream.toList()
			return Collections.unmodifiableList(IntStream.range(0, nodes.getLength())
          		  										 .mapToObj(nodes::item)
          		  										 .map(Node::getTextContent)
          		  										 .collect(Collectors.toList())
          		  								);
		} catch (XPathExpressionException | DOMException e) {
			throw new XmlDocumentException("Error while processing xpath(" + xPathExpr + ").", e);
		}
	}

    public com._4point.aem.fluentforms.api.Document getDocument(String xPathExpr) throws XmlDocumentException {
    	return SimpleDocumentFactoryImpl.getFactory().create(Base64.getDecoder().decode(getString(xPathExpr)));
    }
    
    public List<com._4point.aem.fluentforms.api.Document> getDocuments(String xPathExpr) throws XmlDocumentException {
    	DocumentFactory docFactory = SimpleDocumentFactoryImpl.getFactory();
    	return Collections.unmodifiableList(getStrings(xPathExpr).stream()
    															 .map(s->docFactory.create(Base64.getDecoder().decode(s)))
    															 .collect(Collectors.toList())
    										);
    }

    public Boolean getBoolean(String xPathExpr) throws XmlDocumentException {
    	return Boolean.valueOf(getString(xPathExpr));
    }

    @SuppressWarnings("serial")
	public static class XmlDocumentException extends Exception {

		public XmlDocumentException() {
		}

		public XmlDocumentException(String message, Throwable cause) {
			super(message, cause);
		}

		public XmlDocumentException(String message) {
			super(message);
		}

		public XmlDocumentException(Throwable cause) {
			super(cause);
		}
    }

}
