package com._4point.aem.docservices.rest_services.client.helpers;

import java.util.function.Supplier;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

public class BuilderImpl implements Builder {
	private final static Supplier<Client> defaultClientFactory = ()->ClientBuilder.newClient();
	
	private String machineName = "localhost";
	private int port = 4502;
	private HttpAuthenticationFeature authFeature = null;
	private boolean useSsl = false;
	private Supplier<Client> clientFactory = defaultClientFactory;
	private Supplier<String> correlationIdFn = null;

	public BuilderImpl() {
		super();
	}

	@Override
	public BuilderImpl machineName(String machineName) {
		this.machineName = machineName;
		return this;
	}

	@Override
	public BuilderImpl port(int port) {
		this.port = port;
		return this;
	}

	@Override
	public BuilderImpl useSsl(boolean useSsl) {
		this.useSsl = useSsl;
		return this;
	}

	@Override
	public BuilderImpl clientFactory(Supplier<Client> clientFactory) {
		this.clientFactory = clientFactory;
		return this;
	}

	@Override
	public BuilderImpl basicAuthentication(String username, String password) {
		this.authFeature = HttpAuthenticationFeature.basic(username, password);
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
	public WebTarget createLocalTarget() {
		Client client = clientFactory.get();
		client.register(MultiPartFeature.class);
		if (this.authFeature != null) {
			client.register(authFeature);
		}
		WebTarget localTarget = client.target("http" + (useSsl ? "s" : "") + "://" + machineName + ":" + Integer.toString(port));
		return localTarget;
	}

}
