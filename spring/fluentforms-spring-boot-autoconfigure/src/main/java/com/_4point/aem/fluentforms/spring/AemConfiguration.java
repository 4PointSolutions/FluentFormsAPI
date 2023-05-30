package com._4point.aem.fluentforms.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

@EnableEncryptableProperties
@ConfigurationProperties("fluentforms.aem")
public record AemConfiguration(
	String servername,						// "aem.servername"
	Integer port,							// "aem.port"
	String user,							// "aem.user"
	String password,						// "aem.password"
	@DefaultValue("false") Boolean useSsl,	// "aem.useSsl"
	@DefaultValue("") String afBaseLocation	// "aem.af-base-location"
	) {

	public String url() {
		return "http" + (useSsl ? "s" : "") + "://" + servername + (port != 80 ? ":" + port : "") + "/";
	}
}
