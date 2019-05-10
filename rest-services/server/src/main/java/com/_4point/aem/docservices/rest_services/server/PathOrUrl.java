package com._4point.aem.docservices.rest_services.server;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class PathOrUrl {
	private final Path path;
	private final URL url;
	
	public PathOrUrl(Path path) {
		super();
		this.path = path;
		this.url = null;
	}

	public PathOrUrl(URL url) {
		super();
		this.path = null;
		this.url = url;
	}

	public Path getPath() {
		return path;
	}

	public URL getUrl() {
		return url;
	}

	public boolean isPath() { return path != null; }
	public boolean isUrl() { return url != null; }
	
	public static PathOrUrl fromString(final String pathOrUrl) {
		if (Objects.requireNonNull(pathOrUrl).isEmpty()) {
			throw new IllegalArgumentException("Empty Path or Url provided.");
		}
		try {
			Path resultPath = Paths.get(pathOrUrl);
			return new PathOrUrl(resultPath);
		} catch (InvalidPathException e) {
			try {
				URL resultUrl = new URL(pathOrUrl);
				return new PathOrUrl(resultUrl);
			} catch (MalformedURLException e2) {
				throw new IllegalArgumentException("Bad Path or URL provided. '" + pathOrUrl + "'.", e2);
			}
		}
	}
}
