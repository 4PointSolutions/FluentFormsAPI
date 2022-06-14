package com._4point.aem.docservices.rest_services.server.data;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com._4point.aem.docservices.rest_services.server.data.DataCache.Entry;
import com.adobe.forms.common.service.AbstractDataProvider;
import com.adobe.forms.common.service.ContentType;
import com.adobe.forms.common.service.DataOptions;
import com.adobe.forms.common.service.DataProvider;
import com.adobe.forms.common.service.FormsException;
import com.adobe.forms.common.service.PrefillData;

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
		service = DataProvider.class 
)
public class DataCachePrefillService extends AbstractDataProvider {
	private static final String SERVICE_NAME = "FFPrefillService";
	private static final String PROTOCOL_SERVICE = "service:";
	private Logger logger = LoggerFactory.getLogger(DataCachePrefillService.class);

	public DataCachePrefillService() {
	}

	@Override
	public PrefillData getPrefillData(DataOptions options) throws FormsException {
		String dataRef = Objects.requireNonNull(options, "null DataXMLOptions was passed to com._4point.aem.docservices.rest_services.server.data.DataCachePrefillService.getDataXMLForDataRef().")
								.getDataRef();
		ContentType contentType = options.getContentType();
		
		logger.info("Prefill Service - dataRef = '" + dataRef + "'.");
		logger.info("Prefill Service - contentType = '" + contentType + "'.");
		logger.debug("Prefill Service - serviceName = '" + options.getServiceName() + "'.");
	
		if (logger.isDebugEnabled()) {
			displayParams(options);
		}
		
		if (dataRef == null) {
			return null;
		} else {
			String uuid = extractIdentifier(dataRef);
			Optional<Entry> entry = DataCache.getDataFromCache(uuid);

			return entry.map(e->toPrefillData(e))
						.orElseGet(()->issueWarning(uuid));
		}
	}

	private PrefillData issueWarning(String uuid) {
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
	
	private void displayParams(DataOptions options) {

		Map<String, Object> params = options.getExtras();
		if (params == null) {
			logger.debug("Prefill Service - no Extras (i.e. null).");
		} else {
			if (params.isEmpty()) {
				logger.debug("Prefill Service - Extras is empty.");
			} else {
				Set<Map.Entry<String, Object>> entrySet = params.entrySet();
				if (entrySet == null || entrySet.isEmpty()) {
					logger.debug("Prefill Service - no Entry Set.");
				} else {
					for (Map.Entry<String, Object> entry : entrySet) {
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
	
	private static ContentType toContentType(String contentTypeStr) {
		com._4point.aem.docservices.rest_services.server.ContentType contentType = com._4point.aem.docservices.rest_services.server.ContentType.valueOf(contentTypeStr);
		
		if (contentType.isCompatibleWith(com._4point.aem.docservices.rest_services.server.ContentType.APPLICATION_XML)) {
			return ContentType.XML;
		} else if (contentType.isCompatibleWith(com._4point.aem.docservices.rest_services.server.ContentType.APPLICATION_JSON)) {
			return ContentType.JSON;
		} else {
			throw new FormsException("No support for data with content type '" + contentTypeStr + "'.");
		}
	}
	
	private static PrefillData toPrefillData(Entry entry) {
		return new PrefillData(new ByteArrayInputStream(entry.data()), toContentType(entry.contentType()));
	}
}