package com._4point.aem.docservices.rest_services.client.helpers;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Function;

/**
 * Class that can be used to retrieve a filter function that prepends necessary prefix (/aem) in from of URLs so that the
 * FormsFeeder server can reverse-proxy resources required by HTML5 and Adaptive Forms.
 * 
 * The function it returns is pre-configured to modify the standard FormsFeeder URLs.
 *
 */
/**
 * @author rob.mcdougall
 *
 */
public class StandardFormsFeederUrlFilters {
	public static final String FORMSFEEDER_AF_BASE_LOCATION_PROP = "formsfeeder.af-base-location";

	private static final String[] replacedUrls = {
			"/etc.clientlibs/",
			"/libs/wcm/",
			"/etc/clientlibs/",
			"/libs/fd/",
			"/content/forms/",
			"/content/xfaforms/",
			"/libs/granite/",
			"/libs/clientlibs/granite",
			"/libs/cq/",
			"/apps/"
	};
	
	/**
	 * Function to replace the standard AEM Urls
	 * @deprecated
	 * This method has been replaced with one that returns a function (since a function is what is generally required.)
	 * <p> Use {@link #getStandardOutputStreamFilter()} instead
	 * 
	 * @param outputStream
	 * @return
	 */
	@Deprecated
	public static OutputStream replaceAemUrls(OutputStream outputStream) {
		return replaceAemUrls(outputStream, AemServerType.StandardType.OSGI.pathPrefix());
	}

	/**
	 * Function to replace the standard AEM Urls
	 * @deprecated
	 * This method has been replaced with one that returns a function (since a function is what is generally required.)
	 * <p> Use {@link #getStandardOutputStreamFilter(AemServerType)} instead
	 * 
	 * @param outputStream
	 * @param aemServerType
	 * @return
	 */
	@Deprecated
	public static OutputStream replaceAemUrls(OutputStream outputStream, AemServerType aemServerType) {
		return replaceAemUrls(outputStream, aemServerType.pathPrefix());
	}

	/**
	 * Function to replace the standard AEM Urls
	 * @deprecated
	 * This method has been replaced with one that returns a function (since a function is what is generally required.)
	 * <p> Use {@link #getStandardOutputStreamFilter(String)} instead
	 * 
	 * @param outputStream
	 * @param prefix
	 * @return
	 */
	@Deprecated
	public static OutputStream replaceAemUrls(OutputStream outputStream, String prefix) {
		return getUrlFilterBuilder().appPrefix(prefix)
									.buildOutputStreamFn()
									.apply(outputStream);
	}

	/**
	 * Function to replace the standard AEM Urls
	 * @deprecated
	 * This method has been replaced with one that returns a function (since a function is what is generally required.)
	 * <p> Use {@link #getStandardInputStreamFilter()} instead
	 * 
	 * @param inputStream
	 * @return
	 */
	@Deprecated
	public static InputStream replaceAemUrls(InputStream inputStream) {
		return replaceAemUrls(inputStream, AemServerType.StandardType.OSGI.pathPrefix());
		
	}
	
	/**
	 * Function to replace the standard AEM Urls
	 * @deprecated
	 * This method has been replaced with one that returns a function (since a function is what is generally required.)
	 * <p> Use {@link #getStandardInputStreamFilter(AemServerType)} instead
	 * 
	 * @param inputStream
	 * @param aemServerType
	 * @return
	 */
	@Deprecated
	public static InputStream replaceAemUrls(InputStream inputStream, AemServerType aemServerType) {
		return replaceAemUrls(inputStream, aemServerType.pathPrefix());
		
	}
	
	/**
	 * Function to replace the standard AEM Urls
	 * @deprecated
	 * This method has been replaced with one that returns a function (since a function is what is generally required.)
	 * <p> Use {@link #getStandardInputStreamFilter(String)} instead
	 * 
	 * @param inputStream
	 * @param prefix
	 * @return
	 */
	@Deprecated
	public static InputStream replaceAemUrls(InputStream inputStream, String prefix) {
		return getUrlFilterBuilder().appPrefix(prefix)
				.buildInputStreamFn()
				.apply(inputStream);
	}

	/**
	 * Provides a FormsFeederUrlFilterBuilder prepopulated with the standard set of AEM Urls that need to be modified.
	 * 
	 * This routine assumes an AEM OSGi instance.
	 * 
	 * @return builder
	 */
	public static FormsFeederUrlFilterBuilder getUrlFilterBuilder() {
		return new FormsFeederUrlFilterBuilder(replacedUrls);
	}
	
	/**
	 * Provides a FormsFeederUrlFilterBuilder prepopulated with the standard set of AEM Urls that need to be modified.
	 * 
	 * @param aemServerType - Type of AEM server (JEE or OSGi)
	 * @return builder
	 */
	public static FormsFeederUrlFilterBuilder getUrlFilterBuilder(AemServerType aemServerType) {
		return new FormsFeederUrlFilterBuilder(replacedUrls).appPrefix(aemServerType.pathPrefix());
	}

	/**
	 * Provides a FormsFeederUrlFilterBuilder prepopulated with the standard set of AEM Urls that need to be modified.
	 * 
	 * @param prefix - application prefix that will be prepended to the Urls. 
	 * @return builder
	 */
	public static FormsFeederUrlFilterBuilder getUrlFilterBuilder(String prefix) {
		return new FormsFeederUrlFilterBuilder(replacedUrls).appPrefix(prefix);
	}
	
	/**
	 * Provides a function that can be used to replace URLs in an HTML5 or Adaptive Forms that will be reverse proxied by
	 * FormsFeeder.  This function is typically passed in to the addRenderResultFilter method on the
	 * {@link Html5FormsService} or {@link AdaptiveFormsService}.
	 * 
	 * @return filter Function
	 */
	public static Function<InputStream, InputStream> getStandardInputStreamFilter() {
		return getUrlFilterBuilder().buildInputStreamFn();
	}

	/**
	 * Provides a function that can be used to replace URLs in an HTML5 or Adaptive Forms that will be reverse proxied by
	 * FormsFeeder.  This function is typically passed in to the addRenderResultFilter method on the
	 * {@link Html5FormsService} or {@link AdaptiveFormsService}.
	 * 
	 * @return filter Function
	 */
	public static Function<OutputStream, OutputStream> getStandardOutputStreamFilter() {
		return getUrlFilterBuilder().buildOutputStreamFn();
	}
	
	/**
	 * Provides a function that can be used to replace URLs in an HTML5 or Adaptive Forms that will be reverse proxied by
	 * FormsFeeder.  This function is typically passed in to the addRenderResultFilter method on the
	 * {@link Html5FormsService} or {@link AdaptiveFormsService}.
	 * 
	 * @param aemServerType
	 * @return filter Function
	 */
	public static Function<InputStream, InputStream> getStandardInputStreamFilter(AemServerType aemServerType) {
		return getUrlFilterBuilder(aemServerType).buildInputStreamFn();
	}

	/**
	 * Provides a function that can be used to replace URLs in an HTML5 or Adaptive Forms that will be reverse proxied by
	 * FormsFeeder.  This function is typically passed in to the addRenderResultFilter method on the
	 * {@link Html5FormsService} or {@link AdaptiveFormsService}.
	 * 
	 * @param aemServerType
	 * @return filter Function
	 */
	public static Function<OutputStream, OutputStream> getStandardOutputStreamFilter(AemServerType aemServerType) {
		return getUrlFilterBuilder(aemServerType).buildOutputStreamFn();
	}

	/**
	 * Provides a function that can be used to replace URLs in an HTML5 or Adaptive Forms that will be reverse proxied by
	 * FormsFeeder.  This function is typically passed in to the addRenderResultFilter method on the
	 * {@link Html5FormsService} or {@link AdaptiveFormsService}.
	 * 
	 * @param prefix
	 * @return filter Function
	 */
	public static Function<InputStream, InputStream> getStandardInputStreamFilter(String prefix) {
		return getUrlFilterBuilder(prefix).buildInputStreamFn();
	}

	/**
	 * Provides a function that can be used to replace URLs in an HTML5 or Adaptive Forms that will be reverse proxied by
	 * FormsFeeder.  This function is typically passed in to the addRenderResultFilter method on the
	 * {@link Html5FormsService} or {@link AdaptiveFormsService}.
	 * 
	 * @param prefix
	 * @return filter Function
	 */
	public static Function<OutputStream, OutputStream> getStandardOutputStreamFilter(String prefix) {
		return getUrlFilterBuilder(prefix).buildOutputStreamFn();
	}
}
