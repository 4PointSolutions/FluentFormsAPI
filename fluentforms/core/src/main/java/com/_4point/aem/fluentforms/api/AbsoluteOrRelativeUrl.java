package com._4point.aem.fluentforms.api;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class AbsoluteOrRelativeUrl {
	private final URL absolute;
	private final String relative;
	
	private AbsoluteOrRelativeUrl(URL absolute) {
		super();
		this.absolute = absolute;
		this.relative = null;
	}

	private AbsoluteOrRelativeUrl(String relative) {
		super();
		this.absolute = null;
		this.relative = relative;
	}

	public URL getAbsolute() {
		return absolute;
	}

	public String getRelative() {
		return relative;
	}

	public boolean isAbsolute() {return this.absolute != null;}
	public boolean isRelative() {return this.relative != null;}

	public static AbsoluteOrRelativeUrl fromString(String url) {
		if (Objects.requireNonNull(url.trim()).isEmpty()) {
			throw new IllegalArgumentException("Empty url was provided but not allowed.");
		}
			try {
				URL resultUrl = new URL(url);
				return new AbsoluteOrRelativeUrl(resultUrl);
			} catch (MalformedURLException e) {
				try {
					URL dummyMachineUrl = new URL("http://example.com/");
					URL dummyUrl = new URL(dummyMachineUrl, url);	// throws an exception if url is not relative.
					return new AbsoluteOrRelativeUrl(url);	// If we get this far, it is a relative Url.
				} catch (MalformedURLException e2) {
					throw new IllegalArgumentException("Bad URL provided. '" + url + "'.", e2);
				}
			}
	}

	public static AbsoluteOrRelativeUrl fromUrl(URL url) {
		return new AbsoluteOrRelativeUrl(url);
	}

	@Override
	public String toString() {
		if (this.isAbsolute()) {
			return this.absolute.toString();
		} else if (this.isRelative()) {
			return this.relative;
		} else {
			throw new IllegalStateException("AbsoluteOrRelativeUrl object is neither Absolute nor Relative!");	// Should never happen.
		}
	}
}
