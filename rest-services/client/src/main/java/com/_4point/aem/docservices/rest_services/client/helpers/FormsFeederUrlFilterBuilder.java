package com._4point.aem.docservices.rest_services.client.helpers;

import java.io.InputStream;
import java.io.OutputStream;
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
	private Protocol protocol = null;
	private String machineName = null;
	private int port;

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
		this.protocol = Objects.requireNonNull(protocol);
		this.machineName = Objects.requireNonNull(machineName);
		this.port = port;
		return this;
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
		if (protocol == null) {
			// The URL does not have a machine name
			return prefix;
		} else {
			// The URL has a machine name
			return protocol.toString().toLowerCase() + "://" + machineName + ":" + port + prefix;
		}
	}
	
}

