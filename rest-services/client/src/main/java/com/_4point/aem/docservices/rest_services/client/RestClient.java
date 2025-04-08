package com._4point.aem.docservices.rest_services.client;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import com._4point.aem.fluentforms.api.Document;

public interface RestClient {
	public static final String CORRELATION_ID_HTTP_HDR = "X-Correlation-ID";
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
		public static final ContentType APPLICATION_XML = ContentType.of("application/xml");
		public static final ContentType APPLICATION_JSON = ContentType.of("application/json");
		public static final ContentType TEXT_PLAIN = ContentType.of("text/plain");
		public static final ContentType TEXT_HTML = ContentType.of("text/html");
		public static final ContentType APPLICATION_OCTET_STREAM = ContentType.of("application/octet-stream");
		public static final ContentType APPLICATION_DPL = ContentType.of("application/vnd.datamax-dpl");
		public static final ContentType APPLICATION_IPL = ContentType.of("application/vnd.intermec-ipl");
		public static final ContentType APPLICATION_PCL = ContentType.of("application/vnd.hp-pcl");
		public static final ContentType APPLICATION_PS = ContentType.of("application/postscript");
		public static final ContentType APPLICATION_TPCL = ContentType.of("application/vnd.toshiba-tpcl");
		public static final ContentType APPLICATION_ZPL = ContentType.of("x-application/zpl");
		public static final ContentType IMAGE_JPEG = ContentType.of("image/jpeg");
		public static final ContentType IMAGE_PNG = ContentType.of("image/png");
		public static final ContentType IMAGE_TIFF = ContentType.of("image/tiff");
		
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
	
	/**
	 * Represents a payload for a multipart/form-data POST
	 */
	public interface MultipartPayload extends Payload, Closeable {
		
		public interface Builder {
			Builder add(String fieldName, String fieldData);
			Builder add(String fieldName, byte[] fieldData, ContentType contentType);
			Builder add(String fieldName, InputStream fieldData, ContentType contentType);
			default Builder add(String fieldName, Document document) {
				try {
					String contentType = document.getContentType();
					return add(fieldName, document.getInputStream(), contentType != null ? ContentType.of(contentType) : ContentType.APPLICATION_OCTET_STREAM);
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			}
			default Builder add(String fieldName, Document document, ContentType contentType) {
				try {
					return add(fieldName, document.getInputStream(), contentType);
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			}
			default Builder addStrings(String fieldName, List<String> fieldData) {
				for (String obj : fieldData) {
					add(fieldName, obj);
				}
				return this;
			}
			default Builder addDocs(String fieldName, List<Document> fieldData) {
				for (Document obj : fieldData) {
					add(fieldName, obj);
				}
				return this;
			}
			default Builder addStrings(String fieldName, Stream<String> fieldData) {
				fieldData.forEach(fieldValue->add(fieldName, fieldValue));
				return this;
			}
			default Builder addDocs(String fieldName, Stream<Document> fieldData) {
				fieldData.forEach(fieldValue->add(fieldName, fieldValue));
				return this;
			}
			default Builder addIfNotNull(String fieldName, String fieldData) {
				return fieldData != null ? add(fieldName, fieldData) : this;
			}
			default Builder addIfNotNull(String fieldName, byte[] fieldData, ContentType contentType) {
				return fieldData != null ? add(fieldName, fieldData, contentType) : this;
			}
			default Builder addIfNotNull(String fieldName, InputStream fieldData, ContentType contentType) {
				return fieldData != null ? add(fieldName, fieldData, contentType) : this;
			}
			default Builder addIfNotNull(String fieldName, Document document) {
				return document != null ? add(fieldName, document) : this;
			}
			default Builder addIfNotNull(String fieldName, Document document, ContentType contentType) {
				return document != null ? add(fieldName, document, contentType) : this;
			}
			default Builder addDocsIfNotNull(String fieldName, List<Document> fieldData) {
				return fieldData != null ? addDocs(fieldName, fieldData) : this;
			}
			default <T> Builder transformAndAdd(String fieldName, T fieldData, Function<T, String> fn) {
				return fieldData != null ? addIfNotNull(fieldName, fn.apply(fieldData)) : this;
			}
			default <T> Builder transformAndAddBytes(String fieldName, T fieldData, ContentType contentType, Function<T, byte[]> fn) {
				return fieldData != null ? addIfNotNull(fieldName, fn.apply(fieldData), contentType) : this;
			}
			default <T> Builder transformAndAddInputStream(String fieldName, T fieldData, ContentType contentType, Function<T, InputStream> fn) {
				return fieldData != null ? addIfNotNull(fieldName, fn.apply(fieldData), contentType) : this;
			}
			default <T> Builder addStringVersion(String fieldName, T fieldData) {
				return fieldData != null ? addIfNotNull(fieldName, fieldData.toString()) : this;
			}
			default <T, O> Builder transformAndAddStringVersion(String fieldName, T fieldData, Function<T, O> fn) {
				return fieldData != null ? addStringVersion(fieldName, fn.apply(fieldData)) : this;
			}
			default <T> Builder transformAndAddDocs(String fieldName, T fieldData, Function<T, List<Document>> fn) {
				return fieldData != null ? addDocsIfNotNull(fieldName, fn.apply(fieldData)) : this;
			}
			default Builder addStringVersion(String fieldName, List<?> fieldData) {
				if (fieldData != null) {
					for (Object obj : fieldData) {
						addStringVersion(fieldName, obj);
					}
				}
				return this;
			}
			Builder queryParam(String name, String value);
			Builder addHeader(String name, String value);
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
	 * Represents an HTTP GET Request
	 */
	public interface GetRequest {
		
		/**
		 * Performs an HTTP GET Request from a server
		 * 
		 * @param acceptContentType - expected content type
		 * @return Response from the server (Optional.isEMpty() if server returns "No Content"
		 * @throws RestClientException if an error occurs
		 */
		public Optional<Response> getFromServer(ContentType acceptContentType) throws RestClientException;
		
		/**
		 * Used for building a GET request
		 */
		public interface Builder {
			Builder queryParam(String name, String value);
			Builder addHeader(String name, String value);
			GetRequest build();
		}
	}
	
	/**
	 * Returns a builder that is used to construct a GET request.
	 * 
	 * @return
	 */
	public GetRequest.Builder getRequestBuilder();
	
	/**
	 * Returns a builder that is used to construct a GET request.
	 * 
	 * @param additionalPath - additional path segments
	 * @return
	 */
	public GetRequest.Builder getRequestBuilder(String additionalPath);
	
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
