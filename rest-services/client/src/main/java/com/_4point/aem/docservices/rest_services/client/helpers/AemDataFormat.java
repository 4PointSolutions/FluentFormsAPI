package com._4point.aem.docservices.rest_services.client.helpers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Emumeration of AEM's supported data formats.
 *
 */
public enum AemDataFormat {
	XML('<', "application/xml"),
	JSON('{', "application/json");
	
	private static final int SNIFF_BUFFER_SIZE = 10;
	
	private final char triggerChar;
	private final String contentType;
	private static final AemDataFormat[] values = AemDataFormat.values();
	
	private AemDataFormat(char triggerChar, String contentType) {
		this.triggerChar = triggerChar;
		this.contentType = contentType;
	}

	/**
	 * Returns the content type for this data format. 
	 * 
	 * @return content-type string for this data format
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * "Sniff" the data to see if we can determine what it is.
	 * 
	 *  This method is rather unsophisticated.  It looks at the first few bytes of the data presented 
	 *  to see if it can determine the type of data it is.  It assumes that the incoming data is 
	 *  compatible with UTF-8.
	 * 
	 * @param data
	 * 	the data to be tested
	 * @return Optional<AemDataFormat> object (or Optional.empty() if the data format is not recognized)
	 */
	public static Optional<AemDataFormat> sniff(byte[] data) {
		try {
			Reader reader = new InputStreamReader(new ByteArrayInputStream(data), StandardCharsets.UTF_8);
			for(int i = 0; i < SNIFF_BUFFER_SIZE; i++) {
				int r = reader.read();
				if ( r == -1) {
					return Optional.empty();
				}
				char c = (char) r;
				for(AemDataFormat format : values) {
					if (c == format.triggerChar) {
						return Optional.of(format);
					}
				}
			}
			return Optional.empty();
		} catch (IOException e) {
			// Should never happen
			throw new IllegalStateException("IOException when sniffing data.", e);
		}
	}
}
