package com._4point.aem.docservices.rest_services.client.helpers;

import java.util.Objects;
import java.util.function.Supplier;

import com._4point.aem.docservices.rest_services.client.RestClient;

/**
 * This class provides an implementation of the Builder interface that assists in building a JAX-RS client.  It is shared between the following projects:
 * fluentforms/rest-services.client
 * formsfeeder.client
 * 
 * If changes are made to this file, those changes should be propagated into each of the other projects. 
 * These files are copied because there is not enough code to justify creating another project and adding another dependency. 
 *
 */
public class BuilderImpl implements Builder {
	private static final String SERVICES_URL_PREFIX = "/services";

	private final RestClientFactory clientFactory;
	private final AemConfig.SimpleAemConfigBuilder aemConfigBuilder = new AemConfig.SimpleAemConfigBuilder();
	private Supplier<String> correlationIdFn = null;
	private AemServerType aemServerType = AemServerType.StandardType.OSGI;	// Defaults to OSGi but can be overridden.

	public interface Method {
		String mathodName();
	}

	// TODO:  Convert to TriFunction (maybe if correlation ID function is present?)
	public BuilderImpl(RestClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	@Override
	public BuilderImpl machineName(String machineName) {
		this.aemConfigBuilder.serverName(machineName);
		return this;
	}

	@Override
	public BuilderImpl port(int port) {
		this.aemConfigBuilder.port(port);
		return this;
	}

	@Override
	public BuilderImpl useSsl(boolean useSsl) {
		this.aemConfigBuilder.useSsl(useSsl);
		return this;
	}

	@Override
	public BuilderImpl basicAuthentication(String username, String password) {
		this.aemConfigBuilder.ussr(username).password(password);
		return this;
	}

	@Override
	public BuilderImpl correlationId(Supplier<String> correlationIdFn) {
		this.correlationIdFn = correlationIdFn;
		return this;
	}

	@Override
	public Supplier<String> getCorrelationIdFn() {
		return this.correlationIdFn;
	}

	@Override
	public Builder aemServerType(AemServerType serverType) {
		this.aemServerType = serverType;
		return this;
	}

	@Override
	public AemServerType getAemServerType() {
		return this.aemServerType;
	}

	private RestClient createClientImplementation(String endpoint) {
		return Objects.requireNonNull(clientFactory, "Client Factory was not provided.").apply(aemConfigBuilder.build(), endpoint, correlationIdFn);
	}
	
	private String constructStandardPath(String serviceName, String methodName) {
		return this.aemServerType.pathPrefix() + SERVICES_URL_PREFIX + "/" + serviceName + "/" + methodName;
	}

	public RestClient createClient(String serviceName, String methodName) {
		return createClientImplementation(constructStandardPath(serviceName, methodName));
	}
	
	public RestClient createClient(String endpoint) {
		return createClientImplementation(this.aemServerType.pathPrefix() + endpoint);
	}
}