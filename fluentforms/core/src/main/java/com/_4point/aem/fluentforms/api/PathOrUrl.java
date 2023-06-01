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
public final class PathOrUrl {
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

	/**
	 * Drops the protocol (if any) from the value and returns a Path object.
	 *  
	 * @return  Path portion of the object
	 */
	public Path toPath() {
		if (isPath()) {
			return this.path; 
		} else if (isCrxUrl()) {
			String pathStr = this.toString().split(":")[1];
			while (pathStr.startsWith("//")) {
				pathStr = pathStr.substring(1);
			}
			return Paths.get(pathStr);
		} else if (isUrl()){
			return Paths.get(this.url.getPath());
		} else {
			// This should never happen.
			throw new IllegalStateException("Encountered a PathOrUrl that was not a Path, URL or CRX Url!");
		}
	}
	
	/**
	 * Returns the filename part of the PathOrUrl.
	 * 
	 * If the PathOrUrl is an URL or CRX Url and ends in a /, then this returns empty.
	 * If the PathOrUrl is a Path, then this returns Path.getFileName().
	 * 
	 * @return Optional filename  
	 */
	public Optional<String> getFilename() {
		if (isPath()) {
			return Optional.ofNullable(getPath().getFileName()).map(Path::toString);
		} else if (isUrl()) {
			return getUrlFilename(getUrl().getPath());
		} else if (isCrxUrl()) {
			return getUrlFilename(getCrxUrl());
		} else {
			// This should never happen.
			throw new IllegalStateException("Encountered a PathOrUrl that was not a Path, URL or CRX Url!");
		}
	}

	private Optional<String> getUrlFilename(String urlPath) {
		int lastSlashIndex = urlPath.lastIndexOf('/');
		if (lastSlashIndex > -1 && lastSlashIndex < urlPath.length() - 1) {
			return Optional.of(urlPath.substring(lastSlashIndex + 1));   
		} else {
			return Optional.empty();
		}
	}

	/**
	 * Returns the filename part of the PathOrUrl.
	 * 
	 * If the PathOrUrl is an URL or CRX Url and ends in a /, then this returns the object this operating on.
	 * If the PathOrUrl is a Path, then this returns Path.getParent().
	 * 
	 * @return Optional Parent
	 */
	public Optional<PathOrUrl> getParent() {
		if (isPath()) {
			return Optional.ofNullable(getPath().getParent()).map(PathOrUrl::from);
		} else if (isUrl()) {
			return getUrlParent(getUrl().toString());
		} else if (isCrxUrl()) {
			return getUrlParent(getCrxUrl());
		} else {
			// This should never happen.
			throw new IllegalStateException("Encountered a PathOrUrl that was not a Path, URL or CRX Url!");
		}
	}

	private Optional<PathOrUrl> getUrlParent(String urlPath) {
		Optional<String> optFilename = getFilename();
		if (optFilename.isPresent()) {
			String parentStr = urlPath.substring(0, urlPath.length() - optFilename.get().length());
			if (parentStr == null || parentStr.isEmpty()) {
				return Optional.empty();
			}
			return Optional.of(PathOrUrl.from(parentStr));
		} else {
			return Optional.of(this);
		}
	}

	/**
	 * Indicates whether the PathOrUrl is relative or absolute?
	 * 
	 * This routine returns true if the location in the PathOrUrl is relative.
	 * 
	 * In general, CRX and URLs are always absolute while paths are relative if they do not start with a file separator.
	 * 
	 * NOTE: Unlike the java.nio.file.Path, this routine treats a Windows path that starts with / as an absolute path. 
	 * 
	 * @return
	 */
	public boolean isRelative() {
		if (!this.isPath()) { 	// If it's an URL or a CRX: path, then it's absolute
			return false; 
		}
		Path path = this.getPath();
		if (path.isAbsolute()) { 	// If Path thinks it's absolute, then it is
			return false;
		}
		Path root = path.getRoot();
		if (root != null && root.toString().equals("\\")) {	// If we're on windows and it starts with \, then Path doesn't consider it to be absolute, but we do.
			return false;
		}
		return true;
	}
	
	public String convertRelativePathToRelativeUrl() {
		if (!isRelative()) {
			throw new IllegalStateException("Path must be relative in order to convert to relative URL.");
		}
		return this.getPath().toString().replace('\\', '/');
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

	@Override
	public int hashCode() {
		return Objects.hash(isCrxUrl, path, url);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PathOrUrl other = (PathOrUrl) obj;
		return isCrxUrl == other.isCrxUrl && Objects.equals(path, other.path) && Objects.equals(url, other.url);
	}

}
