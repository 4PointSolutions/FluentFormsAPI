package com._4point.aem.docservices.rest_services.server.data;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com._4point.aem.docservices.rest_services.server.data.DataCache.Entry;
import com.adobe.forms.common.service.DataXMLOptions;
import com.adobe.forms.common.service.DataXMLProvider;

/**
 * DataCache prefill service
 * 
 * This is an AEM Forms Adaptive Forms Prefill Service implementation as outlined here:
 * https://docs.adobe.com/content/help/en/experience-manager-65/forms/adaptive-forms-advanced-authoring/prepopulate-adaptive-form-fields.html
 * 
 * It accepts a UUID identifier, uses that to retrieve XML data from the DataCache and returns that data in an InputStream.
 *
 */
@Component(
		name = "FFPrefillService",
		immediate = true,
		property = {Constants.SERVICE_DESCRIPTION + "=Fluent Forms REST Services Prefill Service"},
		service = DataXMLProvider.class 
)
public class DataCachePrefillService implements DataXMLProvider {
	private static final String SERVICE_NAME = "FFPrefillService";
	private static final String PROTOCOL_SERVICE = "service:";
	private Logger logger = LoggerFactory.getLogger(DataCachePrefillService.class);

	public DataCachePrefillService() {
	}
	// 

	public InputStream getDataXMLForDataRef(DataXMLOptions options) /* throws FormsException */ {
		String dataRef = Objects.requireNonNull(options, "null DataXMLOptions was passed to com._4point.aem.docservices.rest_services.server.data.DataCachePrefillService.getDataXMLForDataRef().")
								.getDataRef();
		
		logger.info("Prefill Service - dataRef = '" + dataRef + "'.");
		logger.debug("Prefill Service - serviceName = '" + options.getServiceName() + "'.");
	
		if (logger.isDebugEnabled()) {
			displayParams(options);
		}
		
		String uuid = extractIdentifier(dataRef);
		Optional<Entry> entry = DataCache.getDataFromCache(uuid);
		
//		if (entry.isPresent() ) {
//			return new ByteArrayInputStream(entry.get().data()); 
//		} else {
//			issueWarning(uuid);
//			return null;
//		}
		return entry.map(Entry::data)
					.map(ByteArrayInputStream::new)
					.orElseGet(()->issueWarning(uuid));
	}

	private ByteArrayInputStream issueWarning(String uuid) {
		logger.warn(SERVICE_NAME + " could not retrieve data for uuid '" + uuid + "' from cache.");
		return null;
	}

	/**
	 * Extract the uuid in the identifier location.
	 * 
	 * We're expecting the dataRed to be in the format service://[SERVICE_NAME]/[IDENTIFIER] (per the docs) 
	 * This means it should be something like this: service://FFPrefillService/[UUID]
	 * 
	 * @param dataRef
	 * @return
	 */
	private String extractIdentifier(String dataRef) {
		String[] splitDataRef = dataRef.split("/");
		int numParams = splitDataRef.length;
		String uuid = splitDataRef[numParams - 1];
		String serviceName = splitDataRef[numParams - 2];
		String protocol = splitDataRef[0];
		
		if (!protocol.equalsIgnoreCase(PROTOCOL_SERVICE)) {
			logger.warn("Expected dataRef protocol to be '" + PROTOCOL_SERVICE + "' but was '" + protocol + "'.");
		}
		if (!serviceName.equals(SERVICE_NAME)) {
			logger.warn("Expected dataRef service name to be '" + SERVICE_NAME + "' but was '" + serviceName + "'.");
		}
		return uuid;
	}
	
	private void displayParams(DataXMLOptions options) {
		@SuppressWarnings("unchecked")
		Map<String, String> params = options.getParams();	// It's annoying that in 2020, the Adobe API is returning a non-typed Map object
															// I'm just guessing at the types since, so far, this has always been null.
		if (params == null) {
			logger.debug("Prefill Service - no Params (i.e. null).");
		} else {
			if (params.isEmpty()) {
				logger.debug("Prefill Service - Params is empty.");
			} else {
				Set<Map.Entry<String, String>> entrySet = params.entrySet();
				if (entrySet == null || entrySet.isEmpty()) {
					logger.debug("Prefill Service - no Entry Set.");
				} else {
					for (Map.Entry<String, String> entry : entrySet) {
						logger.debug("Prefill Service - parameter = '" + entry.getKey() + "'/'" + entry.getValue() + "'.");
					}
				}
			}
		}
	}

	public String getServiceName() {
		return SERVICE_NAME;
	}

	public String getServiceDescription() {
		return "Fluent Forms REST Services Prefill Service for Adaptive Forms";
	}

}