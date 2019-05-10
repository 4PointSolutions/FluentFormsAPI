package com._4point.aem.docservices.rest_services.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com._4point.aem.docservices.rest_services.server.Exceptions.BadRequestException;

public class FormParameters {

	/**
	 *	Private constructor to prevent instantiation of this class. 
	 */
	private FormParameters() {
		super();
	}

	/**
	 * Gets a mandatory parameter from the provided request.  Throws a BadRequestException if the parameter is not found.
	 * This method only gets the first occurrence of a given parameter.
	 *  
	 * @param request		request object
	 * @param paramName		parameter to be extracted
	 * @return				parameter found
	 * @throws BadRequestException	if paramName parameter is not found
	 */
	public static RequestParameter getMandatoryParameter(SlingHttpServletRequest request, String paramName) throws BadRequestException {
		RequestParameter param_value = request.getRequestParameter(paramName);
		if (param_value == null) { 
			throw new BadRequestException("Missing form parameter '" + paramName + "'");
		}
		return param_value;
	}

	/**
	 * Gets a mandatory parameter from the provided request.  Throws a BadRequestException if the parmeter is not found.
	 * This method returns all occurrences of a given parameter.
	 * 
	 * @param request		request object
	 * @param paramName		parameter to be extracted
	 * @return				parameters found
	 * @throws BadRequestException	if paramName parameter is not found
	 */
	public static RequestParameter[] getMandatoryParameters(SlingHttpServletRequest request, String paramName) throws BadRequestException {
		RequestParameter[] param_value = request.getRequestParameters(paramName);
		if (param_value == null || param_value.length < 1) { 
			throw new BadRequestException("Missing form parameter '" + paramName + "'");
		}
		return param_value;
	}

	/**
	 * Gets an optional parameter from the provided request.    
	 * This method optionally only gets the first occurrence of a given parameter.
	 * 
	 * @param request		request object
	 * @param paramName		parameter to be extracted
	 * @return				(optional) paramName parameter found (or not)
	 */
	public static Optional<RequestParameter> getOptionalParameter(SlingHttpServletRequest request, String paramName) {
		return Optional.ofNullable(request.getRequestParameter(paramName));
	}
	
	/**
	 * Gets an optional parameter from the provided request.    
	 * This method optionally only gets the first occurrence of a given parameter.
	 * 
	 * @param request		request object
	 * @param paramName		parameter to be extracted
	 * @return				(optional) paramName parameters found (or not)
	 */
	public static Optional<RequestParameter[]> getOptionalParameters(SlingHttpServletRequest request, String paramName) {
		return Optional.ofNullable(request.getRequestParameters(paramName));
	}
	
	
	/**
	 * Converts RequestParameter that we expect to be a boolean (i.e. "true" or "false") to a boolean.
	 * Produces a BadRequestException if a bad value (i.e. not true or false) is suppled.
	 * 
	 * @param requestParameter	requestParameter value extracted from the incoming request.
	 * @param paramName			parameter name used to retrieve the parameter (this is used in the exception message).
	 * @return					decoded value
	 * @throws BadRequestException if a bad value (i.e. not true or false) is suppled.
	 */
	public static boolean getBooleanParameterValue(RequestParameter requestParameter, String paramName) throws BadRequestException {
		String stringValue = requestParameter.getString();
		boolean booleanValue = Boolean.parseBoolean(stringValue);
		if (!Boolean.toString(booleanValue).equalsIgnoreCase(stringValue)) {
			throw new BadRequestException("Invalid boolean value (" + stringValue + ") specified for parameter " + paramName + ".");
		}
		return booleanValue;
	}
	
	/**
	 * Validates incoming XML data.  This is a common operation required in order to validate request parameters.
	 * 
	 * @param xml
	 * @throws BadRequestException
	 */
	public static void validateXmlData(byte[] xml) throws BadRequestException
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
			throw new BadRequestException(msg, e);
		}
	}


}
