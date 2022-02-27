package com._4point.aem.docservices.rest_services.cli.restservices.cli;

import java.util.Optional;

public interface AemConfig {
	public enum Protocol {
		HTTP("http"), HTTPS("https");
		
		private final String protocolString;

		private Protocol(String protocolString) {
			this.protocolString = protocolString;
		}

		public final String toProtocolString() {
			return protocolString;
		}
		
		public static final Optional<Protocol> from(String string) {
			for (Protocol value : Protocol.values()) {
				if (value.protocolString.equalsIgnoreCase(string)) {
					return Optional.of(value);
				}
			}
			return Optional.empty();
		}
	};
	
	public enum AemServerType {
		JEE, 
		OSGI;

		public static Optional<AemServerType> from(String typeString) {
			if (typeString != null && !typeString.isEmpty()) {
				for (AemServerType st : values()) {
					if (typeString.equalsIgnoreCase(st.name())) {
						return Optional.of(st);
					}
				}
			}
			return Optional.empty();
		}
	}

	public String   host();
	public int      port();
	public String   username();
	public String   secret();
	public Protocol protocol();
	public AemServerType serverType();
	
	default public String url() {
		return protocol().toProtocolString() + "://" + host() + (port() != 80 ? ":" + port() : "") + "/";
	}
}
