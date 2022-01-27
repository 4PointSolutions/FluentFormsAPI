package com._4point.aem.docservices.rest_services.client.helpers;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author rob.mcdougall
 *
 */
/**
 * @author rob.mcdougall
 *
 */
public class FormsFeederUrlFilterBuilder {
	public enum Protocol { HTTP, HTTPS };
	
	private static final String FORMSFEEDER_URL_PREFIX = "/aem";

	private final List<String> replacedUrls;
	private String appPrefix = "";
	private Location location = null;

	/**
	 * Constructor 
	 * 
	 * @param replacedUrls List of URLs to have prefixes added
	 */
	public FormsFeederUrlFilterBuilder(List<String> replacedUrls) {
		this.replacedUrls = new ArrayList<>(replacedUrls);
	}

	/**
	 * Constructor 
	 * 
	 * @param replacedUrls Array of URLs to have prefixes added
	 */
	public FormsFeederUrlFilterBuilder(String... replacedUrls) {
		this.replacedUrls = new ArrayList<>(Arrays.asList(replacedUrls));
	}

	/**
	 * Add additional URLs to have prefixes added
	 * 
	 * @param replacedUrls additional URLs to have prefixes added 
	 * @return this builder
	 */
	public FormsFeederUrlFilterBuilder addReplacementUrls(String... replacedUrls) {
		for (String replacementUrl : replacedUrls) {
			this.replacedUrls.add(replacementUrl);
		}
		return this;
	}

	/**
	 * Add additional URLs to have prefixes added
	 * 
	 * @param replacedUrls collection of additional URLs to have prefixes added 
	 * @return this builder
	 */
	public FormsFeederUrlFilterBuilder addReplacementUrls(Collection<String> replacedUrls) {
		this.replacedUrls.addAll(replacedUrls);
		return this;
	}

	/**
	 * set the string that will be prefixed in front of the URLs
	 * 
	 * @param appPrefix String that will be prefixed in front of URLs
	 * @return this builder
	 */
	public FormsFeederUrlFilterBuilder appPrefix(String appPrefix) {
		this.appPrefix = appPrefix;
		return this;
	}
	
	/**
	 * Add absolute location to the prefix added to the URls.
	 * 
	 * For example http://example.com:5050 would be appended if absoluteLocation(Protocol.HTTP, "example.com", 5050) were called.
	 * 
	 * @param protocol protocol - either HTTP or HTTPS
	 * @param machineName machinename to be added
	 * @param port port to be added
	 * @return this builder
	 */
	public FormsFeederUrlFilterBuilder absoluteLocation(Protocol protocol, String machineName, int port) {
		return absoluteLocation(Location.from(Objects.requireNonNull(protocol), Objects.requireNonNull(machineName), port));
	}

	/**
	 * Add absolute location to the prefix added to the URls.
	 * 
	 * For example http://example.com would be appended if absoluteLocation(Protocol.HTTP, "example.com") were called.
	 * 
	 * @param protocol protocol - either HTTP or HTTPS
	 * @param machineName machinename to be added
	 * @return this builder
	 */
	public FormsFeederUrlFilterBuilder absoluteLocation(Protocol protocol, String machineName) {
		return absoluteLocation(protocol, machineName, 80);
	}

	/**
	 * Add absolute location to the prefix added to the URls.
	 * 
	 * For example http://example.com:5050 would be appended if absoluteLocation(Protocol.HTTP, "example.com", 5050) were called.
	 * 
	 * @param location Location object
	 * @return this builder
	 */
	public FormsFeederUrlFilterBuilder absoluteLocation(Location location) {
		this.location = location;
		return this;
	}

	/**
	 * Add absolute location to the prefix added to the URls.
	 * 
	 * For example http://example.com:5050 would be appended if absoluteLocation(Protocol.HTTP, "example.com", 5050) were called.
	 * 
	 * @param location Location String (e.g. "http://example.com:8090" or "http://example.com/" - it can contain trailing slash.
	 * @return this builder
	 * @throws MalformedURLException 
	 */
	public FormsFeederUrlFilterBuilder absoluteLocation(String location) throws MalformedURLException {
		return absoluteLocation(Location.from(location));
	}

	/**
	 * Build an Function<InputStream, InputStream> from the current builder (using the current settings in the builder).
	 * 
	 * @return
	 */
	public Function<InputStream, InputStream> buildInputStreamFn() {
		String srcPrefix = appPrefix;
		String targetPrefix = constructTargetPrefix();
		Function<InputStream, InputStream> fn = Function.identity();
		for (String url : replacedUrls) {
			fn = fn.andThen(is->new ReplacingInputStream(is, srcPrefix + url, targetPrefix + url));
		}
		return fn;
	}

	/**
	 * Build an Function<OutputStream, OutputStream> from the current builder (using the current settings in the builder).
	 * 
	 * @return
	 */
	public Function<OutputStream, OutputStream> buildOutputStreamFn() {
		String srcPrefix = appPrefix;
		String targetPrefix = constructTargetPrefix();
		Function<OutputStream, OutputStream> fn = Function.identity();
		for (String url : replacedUrls) {
			fn = fn.andThen(os->new ReplacingOutputStream(os, srcPrefix + url, targetPrefix + url));
		}
		return fn;
	}

	private String constructTargetPrefix() {
		String prefix = FORMSFEEDER_URL_PREFIX + appPrefix;
		if (location == null) {
			// The URL does not have a machine name
			return prefix;
		} else {
			// The URL has a machine name
			return  location.toString() + prefix;
		}
	}

	/**
	 * Interface for a machine location.  Must implement it's own toString() method.
	 *
	 */
	public interface Location {
		
		// Constructors for Default implementations of this interface.
		public static Location from(Protocol protocol, String machineName, int port) {
			return new LocationImpl(protocol, machineName, port);
		}

		public static Location from(Protocol protocol, String machineName) {
			return new LocationImpl(protocol, machineName);
		}

		public static Location from(String locationStr) throws MalformedURLException {
			URL url = new URL(locationStr);
			Protocol protocol = Protocol.valueOf(url.getProtocol().toUpperCase());
			String host = url.getHost();
			int port = url.getPort();
			return port == -1 ? new LocationImpl(protocol, host) : new LocationImpl(protocol, host, port);
		}
	}
	
	private static class LocationImpl implements Location {
		private final Protocol protocol;
		private final String machineName;
		private final int port;

		private LocationImpl(Protocol protocol, String machineName, int port) {
			this.protocol = protocol;
			this.machineName = machineName;
			this.port = port;
		}

		private LocationImpl(Protocol protocol, String machineName) {
			this(protocol, machineName, 80);
		}

		@Override
		public String toString() {
			return protocol.toString().toLowerCase() + "://" + machineName + (port != 80 ? ":" + port : "");
		}
	}
}

