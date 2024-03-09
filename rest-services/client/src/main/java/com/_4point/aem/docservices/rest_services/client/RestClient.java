package com._4point.aem.docservices.rest_services.client;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Optional;

import com._4point.aem.fluentforms.api.Document;

public interface RestClient {
	
	/**
	 * Returns String representing the final endpoint target location.
	 * 
	 * @return final endpoint target
	 */
	public String target();
	
	/**
	 * Represents a content type / mime type used to identify the content of a response.
	 */
	public record ContentType(String contentType) {
		public static final ContentType APPLICATION_PDF = ContentType.of("application/pdf");
		public static final ContentType APPLICATION_XDP = ContentType.of("application/vnd.adobe.xdp+xml");
		public static final ContentType TEXT_HTML = ContentType.of("text/html");
		public static final ContentType APPLICATION_OCTET_STREAM = ContentType.of("application/octet-stream");
		
		public static ContentType of(String contentType) { return new ContentType(contentType); }
	};
	
	/**
	 * Payload interface is the payload to be sent to AEM.  
	 */
	public interface Payload {
		
		/**
		 * This method returns an Response object unless the response from the called service was NO_CONTENT, in which case
		 * no Response object is returned, 
		 * 
		 * If the called service returns an error code, then this method throws a RestClientException.
		 * 
		 * @param target
		 * @param payload
		 * @param acceptContentType
		 * @return
		 * @throws RestClientException
		 */
		public Optional<Response> postToServer(ContentType acceptContentType) throws RestClientException;
		
	}
	public interface MultipartPayload extends Payload, Closeable {
		
		public interface Builder {
			Builder add(String fieldName, String fieldData);
			Builder add(String fieldName, byte[] fieldData, ContentType contentType);
			Builder add(String fieldName, InputStream fieldData, ContentType contentType);
			default Builder add(String fieldName, Document document) {
				try {
					return add(fieldName, document.getInputStream(), ContentType.of(document.getContentType()));
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			}
			MultipartPayload build();
		}
	}

	/**
	 * Returns a builder that is used to construct the payload for a multipart POST.
	 * 
	 * @return
	 */
	public MultipartPayload.Builder multipartPayloadBuilder();
	
	/**
	 * A response from the AEM Rest Service
	 */
	public interface Response {
		/**
		 * Retrieves the content type of the response (e.g. "application/pdf").
		 * 
		 * @return String representation of the content type.
		 */
		public ContentType contentType();
		/**
		 * Retrieves the InputStream for the response.  This inputStream can only be read once, so it should
		 * only be retrieved once.
		 * 
		 * Retriving more than once will generate an IllegalStateException.
		 * 
		 * @return
		 */
		public InputStream data();
		/**
		 * Retrieves the first HTTP header value for this header. 
		 * 
		 * @param header
		 * @return
		 */
		public Optional<String> retrieveHeader(String header);
		
	}
	
//	/**
//	 * This is the base URL 
//	 */
//	public interface AemBaseTarget {
//		public Target target(String path);
//	}
//
//	public interface Target {
//		
//	}
//	
//	/**
//	 * Create an AemBaseTarget from an AemConfig
//	 * 
//	 * @param aemConfig
//	 * @return
//	 */
//	public AemBaseTarget baseTarget(AemConfig aemConfig);
//	
	
	@SuppressWarnings("serial")
	public static class RestClientException extends Exception {

		public RestClientException() {
		}

		public RestClientException(String message, Throwable cause) {
			super(message, cause);
		}

		public RestClientException(String message) {
			super(message);
		}

		public RestClientException(Throwable cause) {
			super(cause);
		}
	}
}
