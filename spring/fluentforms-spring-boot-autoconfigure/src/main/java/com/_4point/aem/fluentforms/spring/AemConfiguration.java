package com._4point.aem.fluentforms.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

/**
 * Configuration parameters that pertain to connecting to AEM.
 * 
 * @param servername	the machine name of the AEM host machine
 * @param port	the port that AEM is running on
 * @param user	the username that will be used for authentication (Can be encoded using JASYPT ENC())
 * @param password	the password that will be used for authentication (Can be encoded using JASYPT ENC())
 * @param useSsl	boolean indicating whether to connect using SSL or not. (Defaults to false)
 *
 */
@EnableEncryptableProperties
@ConfigurationProperties("fluentforms.aem")
public record AemConfiguration(
	String servername,						// "aem.servername"
	Integer port,							// "aem.port"
	String user,							// "aem.user"
	String password,						// "aem.password"
	@DefaultValue("false") Boolean useSsl,	// "aem.useSsl"
	@DefaultValue("aem") String sslBundle	// "aem.sslBundle"	- Spring SSL Bundle for trust store
	) {

	public String url() {
		return "http" + (useSsl ? "s" : "") + "://" + servername + (port != 80 ? ":" + port : "") + "/";
	}
}
