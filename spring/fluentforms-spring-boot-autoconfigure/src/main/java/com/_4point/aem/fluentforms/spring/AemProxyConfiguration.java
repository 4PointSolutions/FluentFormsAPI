package com._4point.aem.fluentforms.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

@EnableEncryptableProperties
@ConfigurationProperties("fluentforms.rproxy")
public record AemProxyConfiguration(
		@DefaultValue("") String afBaseLocation	// "rproxy.af-base-location"
		) {
}
