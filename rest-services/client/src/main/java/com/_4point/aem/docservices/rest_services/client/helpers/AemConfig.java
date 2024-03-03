package com._4point.aem.docservices.rest_services.client.helpers;

import java.util.Objects;

public interface AemConfig {
	String servername()	;
	Integer port();
	String user();
	String password();
	Boolean useSsl();

	default public String url() {
		return "http" + (useSsl() ? "s" : "") + "://" + servername() + (port() != 80 ? ":" + port() : "") + "/";
	}
	
	public static SimpleAemConfigBuilder builder() { return new SimpleAemConfigBuilder(); }
	
	record SimpleAemConfig(String servername, Integer port, String user, String password, Boolean useSsl) implements AemConfig {} ;

	/**
	 * Builder class for a Simple AEM Configuration.
	 * 
	 * The builder class supplies defaults settings for typical development environments (i.e. servername is localhost,
	 * port = 4502, default user/password, and no SSL). 
	 * 
	 */
	public class SimpleAemConfigBuilder {
		private String serverName = "localhost";
		private Integer port = 4502;
		private String ussr = "admin";
		private String password = "admin";
		private Boolean useSsl = Boolean.FALSE;

		public SimpleAemConfigBuilder serverName(String serverName) {
			this.serverName = serverName;
			return this;
		}

		public SimpleAemConfigBuilder port(Integer port) {
			this.port = port;
			return this;
		}

		public SimpleAemConfigBuilder ussr(String ussr) {
			this.ussr = ussr;
			return this;
		}

		public SimpleAemConfigBuilder password(String password) {
			this.password = password;
			return this;
		}

		public SimpleAemConfigBuilder useSsl(Boolean useSsl) {
			this.useSsl = useSsl;
			return this;
		}

		public AemConfig build() {
			return new SimpleAemConfig(
					Objects.requireNonNull(serverName, "Servername cannot be null"),
					Objects.requireNonNull(port,"Port cannot be null"),
					Objects.requireNonNull(ussr, "User cannot be null"),
					Objects.requireNonNull(password, "Password cannot be null"),
					Objects.requireNonNull(useSsl,"UseSSL cannot be null")
					);
		}
	}
}
