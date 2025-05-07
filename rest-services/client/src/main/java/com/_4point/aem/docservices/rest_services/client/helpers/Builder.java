package com._4point.aem.docservices.rest_services.client.helpers;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import com._4point.aem.docservices.rest_services.client.RestClient;

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

	public Builder basicAuthentication(String username, String password);

	public Builder correlationId(Supplier<String> correlationIdFn);

	public Supplier<String> getCorrelationIdFn();

	public Builder aemServerType(AemServerType serverType);

	public AemServerType getAemServerType();
	
	public interface RestClientFactory extends TriFunction<AemConfig, String, Supplier<String>, RestClient> {}
	
	@FunctionalInterface
	public interface TriFunction<T, U, V, R> {

	    R apply(T t, U u, V v);

	    default <K> TriFunction<T, U, V, K> andThen(Function<? super R, ? extends K> after) {
	        Objects.requireNonNull(after);
	        return (T t, U u, V v) -> after.apply(apply(t, u, v));
	    }
	}

}
