package com._4point.aem.docservices.rest_services.cli.restservices.cli;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// TODO:  Get these working using the Spring Boot encrypted configuration capabilities.
//@EnableEncryptableProperties
//@ConfigurationProperties(prefix = "aemshell.aem")
@Component
public class AemConfigProperties implements AemConfig {
	private final static Logger logger = LoggerFactory.getLogger(AemConfigProperties.class);
	
	public static final String AEMSHELL_ENV_PARAM_PREFIX = "aemshell.";
	public static final String AEMSHELL_AEM_ENV_PARAM_PREFIX = AEMSHELL_ENV_PARAM_PREFIX + "aem.";
	public static final String AEM_HOST_ENV_PARAM = AEMSHELL_AEM_ENV_PARAM_PREFIX + "host";
	public static final String AEM_PORT_ENV_PARAM = AEMSHELL_AEM_ENV_PARAM_PREFIX + "port";
	public static final String AEM_USERNAME_ENV_PARAM = AEMSHELL_AEM_ENV_PARAM_PREFIX + "username";
	public static final String AEM_SECRET_ENV_PARAM = AEMSHELL_AEM_ENV_PARAM_PREFIX + "secret";
	public static final String AEM_USE_SSL_ENV_PARAM = AEMSHELL_AEM_ENV_PARAM_PREFIX + "protocol";
	public static final String AEM_SERVER_TYPE_PARAM = AEMSHELL_AEM_ENV_PARAM_PREFIX + "serverType";

	private final String host;
	private final int port;
	private final String username;
	private final String secret;
	private final Protocol protocol;
	private final AemServerType serverType;
	
	private AemConfigProperties(@Value("${" + AEM_HOST_ENV_PARAM + ":}") String host, 
								@Value("${" + AEM_PORT_ENV_PARAM + ":4502}") int port, 
								@Value("${" + AEM_USERNAME_ENV_PARAM + ":admin}") String username, 
								@Value("${" + AEM_SECRET_ENV_PARAM + ":admin}") String secret, 
								@Value("${" + AEM_USE_SSL_ENV_PARAM + ":HTTP}") Protocol protocol,
								@Value("${" + AEM_SERVER_TYPE_PARAM + ":OSGI}") AemServerType serverType) {
		super();
		this.host = host;
		this.port = port;
		this.username = username;
		this.secret = secret;
		this.protocol = protocol;
		this.serverType = serverType;
	}

	@Override
	public String host() {
		return host; 
	}

	@Override
	public int port() {
		return port; 
	}

	@Override
	public String username() {
		return username; 
	}

	@Override
	public String secret() {
		return secret; 
	}

	@Override
	public Protocol protocol() {
		return protocol;
	}

	@Override
	public AemServerType serverType() {
		return serverType;
	}
}
