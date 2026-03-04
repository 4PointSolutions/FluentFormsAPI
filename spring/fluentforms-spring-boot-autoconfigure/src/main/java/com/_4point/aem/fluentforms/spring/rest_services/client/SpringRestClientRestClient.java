package com._4point.aem.fluentforms.spring.rest_services.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpCookie;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jspecify.annotations.Nullable;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient.RequestHeadersSpec;
import org.springframework.web.util.UriBuilder;

import com._4point.aem.docservices.rest_services.client.RestClient;
import com._4point.aem.docservices.rest_services.client.RestClient.MultipartPayload.Builder;
import com._4point.aem.docservices.rest_services.client.helpers.AemConfig;
import com._4point.aem.docservices.rest_services.client.helpers.Builder.RestClientFactory;

public class SpringRestClientRestClient implements RestClient {
	private final org.springframework.web.client.RestClient springRestClient;
	private final String target;
	private final Supplier<String> correlationIdFn;
	
	private SpringRestClientRestClient(org.springframework.web.client.RestClient springRestClient, String target,	Supplier<String> correlationIdFn) {
		this.springRestClient = springRestClient;
		this.target = target;
		this.correlationIdFn = correlationIdFn;
	}

	@Override
	public String target() {
		return target;
	}

	@Override
	public Builder multipartPayloadBuilder() {
		return new RestClientMultipartPayloadBuilder();
	}

	@Override
	public RestClient.GetRequest.Builder getRequestBuilder() {
		return new SpringClientGetRequestBuilder();
	}

	@Override
	public RestClient.GetRequest.Builder getRequestBuilder(String additionalPath) {
		return new SpringClientGetRequestBuilder(additionalPath);
	}

	public static RestClientFactory factory() {
		return factory(org.springframework.web.client.RestClient.builder());
	}

	public static RestClientFactory factory(org.springframework.web.client.RestClient.Builder builder) {
		return (aemConfig, target, correlationIdFn) -> 
					new SpringRestClientRestClient(createSpringRestClient(aemConfig, target, builder), target, correlationIdFn);
	}

	private static org.springframework.web.client.RestClient createSpringRestClient(
			AemConfig aemConfig, 
			String target, 
			org.springframework.web.client.RestClient.Builder builder
			) {
		ClientHttpRequestInterceptor basicAuth = new BasicAuthenticationInterceptor(aemConfig.user(), aemConfig.password());

		return builder.baseUrl(getTarget(aemConfig, target))
					  .requestInterceptor(basicAuth)
					  .build();
	}
	
	private static String getTarget(AemConfig aemConfig, String additionalPath) {
		return aemConfig.url() + (additionalPath.startsWith("/") ? additionalPath.substring(1) : additionalPath);
	}

	private abstract class SpringClientRequestBuilder {
		protected Function<UriBuilder, UriBuilder> uriBuilder = Function.identity();
		protected Consumer<org.springframework.http.HttpHeaders> headerBuilder = correlationIdFn != null ? h->h.put(RestClient.CORRELATION_ID_HTTP_HDR, List.of(correlationIdFn.get())) 
        																	    : __->{};	// if correlationIdFn is available, use it as the base headerBuilder function.
        protected Consumer<MultiValueMap<String, String>> cookiesConsumer = __->{};

        public void addQueryParam(String name, String value) {
            this.uriBuilder = uriBuilder.andThen(u->u.queryParam(name, value));
        }

        public void addHeaderValue(String name, String value) {
        	this.headerBuilder = headerBuilder.andThen(h->h.put(name, List.of(value)));
        }
        
        public void addCookieValues(Cookies cookies) {
        	if (cookies == null || cookies.isEmpty() || !(cookies instanceof SpringClientResponse.SpringRestClientResponseCookies srcCookies)) {
				return;
			}
        	this.cookiesConsumer = cookiesConsumer.andThen(mvm->srcCookies.cookies.stream().forEach(c->mvm.add(c.getName(), c.getValue())));
        }
	}

	private abstract static class SpringClientRequest {
	    private final Function<UriBuilder, UriBuilder> uriBuilder;
	    private final Consumer<org.springframework.http.HttpHeaders> headerBuilder;
		private final Consumer<MultiValueMap<String, String>> cookiesConsumer;

        protected SpringClientRequest(Function<UriBuilder, UriBuilder> uriBuilder, Consumer<org.springframework.http.HttpHeaders> headerBuilder, Consumer<MultiValueMap<String, String>> cookiesConsumer) {
			this.uriBuilder = uriBuilder;
			this.headerBuilder = headerBuilder;
			this.cookiesConsumer = cookiesConsumer;
		}

        protected Function<UriBuilder, URI> uriFunction() {
        	return u->uriBuilder.apply(u).build();
        }
		
		protected Optional<Response> processRequest(RequestHeadersSpec<?> request, ContentType acceptContentType) throws RestClientException {
			ResponseEntity<byte[]> result = request.accept(toMediaType(acceptContentType))
												   .headers(headerBuilder)
												   .cookies(cookiesConsumer)
												   .retrieve()
												   .toEntity(byte[].class);
			
			if (result.getStatusCode().isSameCodeAs(HttpStatus.NO_CONTENT)) {
				return Optional.empty();
			}
			if (result.getBody() == null) {
				throw new RestClientException("Call to server succeeded but server failed to return content.  This should never happen.");
			}
			MediaType responseContentType = result.getHeaders().getContentType();
			if (responseContentType == null || !responseContentType.isCompatibleWith(toMediaType(acceptContentType))) {
				String msg = "Response from AEM server was not of expected type (" + acceptContentType.contentType() + ").  " + (responseContentType != null ? "content-type='" + responseContentType + "'" : "content-type was null") + ".";
				msg += "\n" + new String(result.getBody());
				throw new RestClientException(msg);
			}
			
			return Optional.of(new SpringClientResponse(result));
		}
	}
	
	private static class SpringClientResponse implements RestClient.Response {
		private final ResponseEntity<byte[]> responseEntity;
		private final org.springframework.http.HttpHeaders headers;

		private SpringClientResponse(ResponseEntity<byte[]> responseEntity) {
			this(responseEntity, responseEntity.getHeaders());
		}

		private SpringClientResponse(ResponseEntity<byte[]> responseEntity, org.springframework.http.HttpHeaders headers) {
			this.responseEntity = responseEntity;
			this.headers = headers;
		}

		@Override
		public InputStream data() {
			return new ByteArrayInputStream(responseEntity.getBody());
		}

		@Override
		public Optional<String> retrieveHeader(String header) {
			List<String> list = headers.get(header);
			return list == null || list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
		}

		@Override
		public ContentType contentType() {
			return toContentType(headers.getContentType());
		}

		@Override
		public HttpHeaders headers() {
			return new SpringRestClientHttpHeaders(headers);
		}

		@Override
		public Cookies getCookies() {
			@Nullable List<String> list = headers.get(org.springframework.http.HttpHeaders.SET_COOKIE);
			return SpringRestClientResponseCookies.from(list);
		}

		private static class SpringRestClientResponseCookies implements Cookies {
			private final List<HttpCookie> cookies;

			private SpringRestClientResponseCookies(List<HttpCookie> cookies) {
				this.cookies = Collections.unmodifiableList(cookies);
			}

			private static SpringRestClientResponseCookies from(@Nullable List<String> cookieHeaderValues) {
				List<HttpCookie> cookiesList = cookieHeaderValues == null ? List.of() 
																		  : cookieHeaderValues.stream()
																		  					  .map(HttpCookie::parse)
																		  					  .flatMap(List::stream)	
																		  					  .toList();
				return new SpringRestClientResponseCookies(cookiesList);
			}
			
			@Override
			public boolean isEmpty() {
				return cookies.isEmpty();
			}

			@Override
			public boolean isPresent() {
				return !isEmpty();
			}
			
		}
	}
	
	private static ContentType toContentType(MediaType mediaType) {
		return new ContentType(mediaType.toString());
	}
	
	private static MediaType toMediaType(ContentType contentType) {
		return MediaType.parseMediaType(contentType.contentType());
	}
	
	public final class SpringRestClientMultipartPayload extends SpringClientRequest implements MultipartPayload {
		private final MultiValueMap<String, HttpEntity<?>> multipartBody;
		
		private SpringRestClientMultipartPayload(Function<UriBuilder, UriBuilder> uriBuilder, 
												 Consumer<org.springframework.http.HttpHeaders> headerBuilder, 
												 Consumer<MultiValueMap<String, String>> cookiesConsumer,
												 MultiValueMap<String, HttpEntity<?>> multipartBody
												 ) {
			super(uriBuilder, headerBuilder, cookiesConsumer);
			this.multipartBody = multipartBody;
		}

		@Override
		public Optional<Response> postToServer(ContentType acceptContentType) throws RestClientException {
			try {
				var request = springRestClient.post()
											  .uri(uriFunction())
											  .body(multipartBody);
				return processRequest(request, acceptContentType);
			} catch (org.springframework.web.client.RestClientException e) {
				String msg = e.getMessage();
				throw new RestClientException("Call to server failed: Error when getting from '" + target + "'" + (msg != null ? " (" + msg + ")" : "") + ".", e); 
			}
		}

		@Override
		public void close() throws IOException {
		}
	}

	private final class RestClientMultipartPayloadBuilder extends SpringClientRequestBuilder implements MultipartPayload.Builder {
		private final MultiValueMap<String, HttpEntity<?>> parts = new LinkedMultiValueMap<>();
		
		private void internalAdd(String fieldName, Object fieldData, MediaType contentType) {
			parts.add(fieldName, new HttpEntity<>(fieldData, new org.springframework.http.HttpHeaders() {
				{
					setContentType(contentType);
				}
			}));
		}
		
		@Override
		public Builder add(String fieldName, String fieldData) {
			internalAdd(fieldName, fieldData, MediaType.TEXT_PLAIN);
			return this;
		}

		@Override
		public Builder add(String fieldName, byte[] fieldData, ContentType contentType) {
			internalAdd(fieldName, new ByteArrayResource(fieldData), MediaType.parseMediaType(contentType.contentType()));
			return this;
		}

		@Override
		public Builder add(String fieldName, InputStream fieldData, ContentType contentType) {
			internalAdd(fieldName, new InputStreamResource(fieldData), MediaType.parseMediaType(contentType.contentType()));
			return this;
		}

		@Override
		public MultipartPayload build() {
			return new SpringRestClientMultipartPayload(uriBuilder, headerBuilder, cookiesConsumer, parts);
		}

		@Override
		public Builder queryParam(String name, String value) {
            addQueryParam(name, value);
            return this;
		}

		@Override
		public Builder addHeader(String name, String value) {
        	addHeaderValue(name, value);
            return this;
		}

		@Override
		public Builder addCookies(Cookies cookies) {
			addCookieValues(cookies);
			return this;
		}
	}

	private class SpringClientGetRequest extends SpringClientRequest implements RestClient.GetRequest {
        
		private SpringClientGetRequest(Function<UriBuilder, UriBuilder> uriBuilder,	
									   Consumer<org.springframework.http.HttpHeaders> headerBuilder, 
									   Consumer<MultiValueMap<String, String>> cookiesConsumer
									   ) {
			super(uriBuilder, headerBuilder, cookiesConsumer);
		}

		@Override
		public Optional<Response> getFromServer(ContentType acceptContentType) throws RestClientException {
			try {
				var request = springRestClient.get()
										  	  .uri(uriFunction());
				return processRequest(request, acceptContentType);
			} catch (org.springframework.web.client.RestClientException e) {
				String msg = e.getMessage();
				throw new RestClientException("Call to server failed: Error when getting from '" + target + "'" + (msg != null ? " (" + msg + ")" : "") + ".", e); 
			}
		}	
	}

	private class SpringClientGetRequestBuilder extends SpringClientRequestBuilder implements RestClient.GetRequest.Builder {
        
        private SpringClientGetRequestBuilder() {
        }

        private SpringClientGetRequestBuilder(String additionalPath) {
            this.uriBuilder = uriBuilder.andThen(u->u.path(additionalPath.startsWith("/") ? additionalPath : "/" + additionalPath));
        }
        
        @Override
        public GetRequest build() {
			return new SpringClientGetRequest(uriBuilder, headerBuilder, cookiesConsumer);
        }
        
        @Override
        public SpringClientGetRequestBuilder queryParam(String name, String value) {
            addQueryParam(name, value);
            return this;
        }

        @Override
        public SpringClientGetRequestBuilder addHeader(String name, String value) {
        	addHeaderValue(name, value);
            return this;
        }

		@Override
		public GetRequest.Builder addCookies(Cookies cookies) {
			addCookieValues(cookies);
			return this;
		}
    }
	
	private static class SpringRestClientHttpHeaders implements RestClient.HttpHeaders {
		private final org.springframework.http.HttpHeaders headers;

		private SpringRestClientHttpHeaders(org.springframework.http.HttpHeaders headers) {
			this.headers = headers;
		}

		@Override
		public CaseHandling caseHandling() {
			return CaseHandling.UPSHIFTS;
		}

		@Override
		public List<HttpHeader> getHeaders(String headerName) {
			return headers.headerSet().stream()
									  .filter(e->e.getKey().equalsIgnoreCase(headerName))
									  .mapMulti(SpringRestClientHttpHeaders::mapHeaderValues)
									  .toList();
		}
		
		private static void mapHeaderValues(Map.Entry<String, List<String>> headerEntry, Consumer<HttpHeader> valueConsumer) {
			String headerName = headerEntry.getKey();
			for(String headerValue : headerEntry.getValue()) {
				valueConsumer.accept(new HttpHeader(headerName, headerValue));
			}
		}

	}
	
}
