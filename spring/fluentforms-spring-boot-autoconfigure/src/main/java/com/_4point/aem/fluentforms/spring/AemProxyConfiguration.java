package com._4point.aem.fluentforms.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

/**
 * Configuration parameters that pertain to the Reverse Proxy Library which reverse proxies secondary
 * resources (.css, .js, etc.) that the browser will request.  These requests are forwarded to AEM.
 * 
 * @param afBaseLocation	String indicating the base location of the Adaptive Forms.
 *
 */
@EnableEncryptableProperties
@ConfigurationProperties("fluentforms.rproxy")
public record AemProxyConfiguration(
		@DefaultValue("") String afBaseLocation,	// "rproxy.af-base-location"
		@DefaultValue("") String aemPrefix
		) {
}
