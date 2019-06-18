package com._4point.aem.docservices.rest_services.client.helpers;

import java.util.function.Supplier;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

public interface Builder {

	public Builder machineName(String machineName);

	public Builder port(int port);

	public Builder useSsl(boolean useSsl);

	public Builder clientFactory(Supplier<Client> clientFactory);

	public Builder basicAuthentication(String username, String password);

	public Builder correlationId(Supplier<String> correlationIdFn);

	public Supplier<String> getCorrelationIdFn();

	public WebTarget createLocalTarget();

}
