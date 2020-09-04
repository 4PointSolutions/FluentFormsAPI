package com._4point.aem.docservices.rest_services.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.sling.api.request.RequestParameter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com._4point.aem.docservices.rest_services.server.Exceptions.BadRequestException;
import com._4point.aem.fluentforms.api.PathOrUrl;

/**
 * Holds a Data Parameter (i.e. an incoming request parameter that specifies data to be merged with a form).  It can be either 
 * by reference (so a pathOrUrl) or by value (so a byte array).   
 *
 */
public class DataParameter {
	
	public enum ParameterType {
		ByteArray, PathOrUrl
	};

	private final byte[] array;
	private final PathOrUrl pathOrUrl;
	private final ParameterType type;

	private DataParameter(byte[] array) {
		super();
		this.array = array;
		this.pathOrUrl = null;
		this.type = ParameterType.ByteArray;
	}

	private DataParameter(PathOrUrl pathOrUrl) {
		super();
		this.array = null;
		this.pathOrUrl = pathOrUrl;
		this.type = ParameterType.PathOrUrl;
	}

	public byte[] getArray() {
		return array;
	}

	public PathOrUrl getPathOrUrl() {
		return pathOrUrl;
	}

	public ParameterType getType() {
		return type;
	}

	public static DataParameter readParameter(RequestParameter dataParameter, boolean validateXml) {
		ContentType dataContentType = ContentType.valueOf(dataParameter.getContentType());
		if (dataContentType.isCompatibleWith(ContentType.TEXT_PLAIN)) {
			return new DataParameter(PathOrUrl.from(dataParameter.getString()));
		} else if (dataContentType.isCompatibleWith(ContentType.APPLICATION_XML)) {
			byte[] dataBytes = dataParameter.get();
			if (validateXml) validateXmlData(dataBytes);
			return new DataParameter(dataBytes);
		} else {
			throw new IllegalArgumentException(
					"Data parmameter has invalid content type. (" + dataContentType.getContentTypeStr() + ").");
		}
	}

	/**
	 * Validates incoming XML data.  This is a common operation required in order to validate request parameters.
	 * 
	 * @param xml
	 * @throws BadRequestException
	 */
	public static void validateXmlData(byte[] xml)
	{
		//---------------------------------------------------------------------
		// Validate the XML from the POST body
		//---------------------------------------------------------------------
		try {
			InputSource is = new InputSource(new ByteArrayInputStream(xml));
			DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
		}
		catch (SAXException | IOException | ParserConfigurationException e) {
			String msg = "Input XML payload invalid: " + e.getMessage();
			throw new IllegalArgumentException(msg, e);
		}
	}
}
