package com._4point.aem.docservices.rest_services.client.helpers;

import java.util.function.Supplier;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.WebTarget;

/**
 * This interface is is for a Builder object that assists in building a JAX-RS client.  It is shared between the following projects:
 * fluentforms/rest-services.client
 * formsfeeder.client
 * 
 * If changes are made to this file, those changes should be propagated into each of the other projects.
 * These files are copied because there is not enough code to justify creating another project and adding another dependency. 
 *
 */
public interface Builder {

	public Builder machineName(String machineName);

	public Builder port(int port);

	public Builder useSsl(boolean useSsl);

	public Builder clientFactory(Supplier<Client> clientFactory);

	public Builder basicAuthentication(String username, String password);

	public Builder correlationId(Supplier<String> correlationIdFn);

	public Supplier<String> getCorrelationIdFn();

	public WebTarget createLocalTarget();

	public Builder aemServerType(AemServerType serverType);

	public AemServerType getAemServerType();
}
