package com._4point.aem.fluentforms.api;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class PathOrUrl {
	private static final String CRX_URL_PROTOCOL = "crx:";
	private static final int CRX_URL_PROTOCOL_LENGTH = CRX_URL_PROTOCOL.length();
	private static final String CRX_URL_SUBSTITUTE = "file:";
	private static final int CRX_URL_SUBSTITUTE_LENGTH = CRX_URL_SUBSTITUTE.length();
	
	private final Path path;
	private final URL url;
	private final boolean isCrxUrl;
	
	public PathOrUrl(Path path) {
		super();
		this.path = path;
		this.url = null;
		this.isCrxUrl = false;
	}

	public PathOrUrl(URL url) {
		super();
		this.path = null;
		this.url = url;
		this.isCrxUrl = false;
	}

	private PathOrUrl(URL url, boolean isCrxUrl) {
		super();
		this.path = null;
		this.url = url;
		this.isCrxUrl = isCrxUrl;
	}

	public Path getPath() {
		return this.path;
	}

	public URL getUrl() {
		return this.isCrxUrl ? null : this.url;
	}

	public String getCrxUrl() {
		return this.isCrxUrl ? urlToCrx(this.url) : null;
	}

	public boolean isPath() { return this.path != null; }
	public boolean isUrl() { return !this.isCrxUrl && this.url != null; }
	public boolean isCrxUrl() { return this.isCrxUrl && this.url != null; }
	
	public static PathOrUrl fromString(final String pathOrUrl) {
		String trimmedPathOrUrl = pathOrUrl.trim();
		if (Objects.requireNonNull(trimmedPathOrUrl).isEmpty()) {
			throw new IllegalArgumentException("Empty Path or Url provided.");
		}
		if (trimmedPathOrUrl.length() > CRX_URL_PROTOCOL_LENGTH && CRX_URL_PROTOCOL.equalsIgnoreCase(trimmedPathOrUrl.substring(0, CRX_URL_PROTOCOL_LENGTH))) {
			// We've encountered a crx URL
			// substitute http: for crx: so that we can validate this as an URL
			try {
				return new PathOrUrl(crxToUrl(trimmedPathOrUrl), true);
			} catch (MalformedURLException e) {
				throw new IllegalArgumentException("Bad CRX URL provided. '" + pathOrUrl + "'.", e);
			}
		}
		try {
			// Unix allows colons in file paths, this means that on Unix URLs can be valid file Paths but not vice versa.
			// So we try creating the URL first, if that fails then try the path. 
			return new PathOrUrl(new URL(trimmedPathOrUrl));
		} catch (MalformedURLException e) {
			try {
				return new PathOrUrl(Paths.get(trimmedPathOrUrl));
			} catch (InvalidPathException e2) {
				throw new IllegalArgumentException("Bad Path or URL provided. '" + pathOrUrl + "'.", e2);
			}
		}
	}

	@Override
	public String toString() {
		if (this.isPath()) {
			return this.path.toString();
		} else if (this.isUrl()) {
			return this.url.toString();
		} else if (this.isCrxUrl()) {
			return urlToCrx(this.url);
		} else {
			throw new IllegalStateException("PathOrUrl object is neither Path nor URL!");	// Should never happen.
		}
	}
	
	public static URL crxToUrl(String crxUrl) throws MalformedURLException {
		return new URL( CRX_URL_SUBSTITUTE + crxUrl.substring(CRX_URL_PROTOCOL_LENGTH));
	}
	
	public static String urlToCrx(URL url) {
		return  CRX_URL_PROTOCOL + url.toString().substring(CRX_URL_SUBSTITUTE_LENGTH);
	}
}
