package com._4point.aem.fluentforms.api;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

/**
 * This class is used to house a location (typically a XDP template location) that is one of the following things:
 * 
 *   1) A <code>Path</code> on the local file system.
 *   2) An <code>URL</code>.
 *   3) A <code>URL</code> that points to a location in the crx repository.
 *   
 *   This class will handle all three types.  You typically construct it from a String using from(String) however it can also
 *   be constructed directly from <code>java.nio.file.Path</code> or <code>java.net.URL</code> objects.
 *
 */
public class PathOrUrl {
	private static final String CRX_URL_PROTOCOL = "crx:";
	private static final int CRX_URL_PROTOCOL_LENGTH = CRX_URL_PROTOCOL.length();
	private static final String CRX_URL_SUBSTITUTE = "file:";
	private static final int CRX_URL_SUBSTITUTE_LENGTH = CRX_URL_SUBSTITUTE.length();
	
	private final Path path;
	private final URL url;
	private final boolean isCrxUrl;
	
	/**
	 * Constructor
	 * 
	 * @param path
	 */
	private PathOrUrl(Path path) {
		super();
		this.path = path;
		this.url = null;
		this.isCrxUrl = false;
	}

	/**
	 * Constructor
	 * 
	 * @param url
	 */
	private PathOrUrl(URL url) {
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

	/**
	 * Return the value as a <code>Path</code> object if <code>isPath()</code> is true, otherwise return null.
	 * 
	 * @return
	 */
	public Path getPath() {
		return this.path;
	}

	/**
	 * Return the value as an <code>URL</code> object if <code>isUrl()</code> is true, otherwise return null.
	 * 
	 * @return
	 */
	public URL getUrl() {
		return this.isCrxUrl ? null : this.url;
	}

	/**
	 * Return the value as a <code>String</code> object if <code>isCrxUrl()</code> is true, otherwise return null.
	 * 
	 * @return
	 */
	public String getCrxUrl() {
		return this.isCrxUrl ? urlToCrx(this.url) : null;
	}

	/**
	 * Does this object house a <code>Path</code> object?
	 * 
	 * @return true if it houses a <code>Path</code> object, otherwise false.
	 */
	public boolean isPath() { return this.path != null; }

	/**
	 * Does this object house a <code>URL</code> object?
	 * 
	 * @return true if it houses a <code>URL</code> object, otherwise false.
	 */
	public boolean isUrl() { return !this.isCrxUrl && this.url != null; }

	/**
	 * Does this object house a crx <code>URL</code> object?
	 * 
	 * @return true if it houses a crx <code>URL</code> object, otherwise false.
	 */
	public boolean isCrxUrl() { return this.isCrxUrl && this.url != null; }

	public Optional<String> getFilename() {
		if (isPath()) {
			return Optional.ofNullable(getPath().getFileName()).map(Path::toString);
		} else if (isUrl()) {
			String urlPath = getUrl().getPath();
			int lastSlashIndex = urlPath.lastIndexOf('/');
			if (lastSlashIndex > -1 && lastSlashIndex < urlPath.length() - 1) {
				return Optional.of(urlPath.substring(lastSlashIndex + 1));   
			} else {
				return Optional.empty();
			}
		} else if (isCrxUrl()) {
			String urlPath = getCrxUrl();
			int lastSlashIndex = urlPath.lastIndexOf('/');
			if (lastSlashIndex > -1 && lastSlashIndex < urlPath.length() - 1) {
				return Optional.of(urlPath.substring(lastSlashIndex + 1));   
			} else {
				return Optional.empty();
			}
		} else {
			// This should never happen.
			throw new IllegalStateException("Encountered a PathOrUrl that was not a Path, URL or CRX Url!");
		}
	}

	/**
	 * Static constructor
	 * 
	 * @param pathOrUrl
	 * @return
	 */
	public static PathOrUrl from(final String pathOrUrl) {
		String trimmedPathOrUrl = Objects.requireNonNull(pathOrUrl, "Null Path or Url String provided.").trim();
		if (trimmedPathOrUrl.isEmpty()) {
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

	/**
	 * Static constructor
	 * 
	 * @param path
	 * @return
	 */
	public static PathOrUrl from(final Path path) {
		return new PathOrUrl(Objects.requireNonNull(path, "Null Path provided."));
	}
	
	/**
	 * Static constructor
	 * 
	 * @param url
	 * @return
	 */
	public static PathOrUrl from(final URL url) {
		return new PathOrUrl(Objects.requireNonNull(url, "Null Url provided."));
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
	
	/**
	 * Convert a crx Url into a file: URL for storage. 
	 * 
	 */
	private static URL crxToUrl(String crxUrl) throws MalformedURLException {
		return new URL( CRX_URL_SUBSTITUTE + crxUrl.substring(CRX_URL_PROTOCOL_LENGTH));
	}
	
	/**
	 * Convert a file: URL back into a crx Url for usage by a client. 
	 * 
	 */
	private static String urlToCrx(URL url) {
		return  CRX_URL_PROTOCOL + url.toString().substring(CRX_URL_SUBSTITUTE_LENGTH);
	}

}
