package com._4point.aem.docservices.rest_services.client.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.function.Function;
import java.util.function.Supplier;

import com._4point.aem.docservices.rest_services.client.RestClient.Response;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;

public abstract class RestServicesServiceAdapter {
	private static final String PAGE_COUNT_HEADER = "com._4point.aem.rest_services.page_count";
	protected static final String CORRELATION_ID_HTTP_HDR = "X-Correlation-ID";
	protected final Supplier<String> correlationIdFn;

	protected RestServicesServiceAdapter(Supplier<String> correlationIdFn) {
		this.correlationIdFn = correlationIdFn;
	}

	protected static Document responseToDoc(Response result)  {
		return responseToDoc(result, is->is);
	}

	protected static Document responseToDoc(Response result, Function<InputStream, InputStream> filter) {
		Document resultDoc = SimpleDocumentFactoryImpl.getFactory().create(filter.apply(result.data()));
		result.retrieveHeader(PAGE_COUNT_HEADER).ifPresent(pageCount->resultDoc.setPageCount(Long.valueOf(pageCount)));;
		resultDoc.setContentType(result.contentType().contentType());
		return resultDoc;
	}

	@FunctionalInterface
	protected interface Function_WithExceptions<T, R, E extends IOException> {
		R apply(T t) throws E;
	}

	protected static <T, R, E extends IOException> Function<T, R> uncheck(Function_WithExceptions<T, R, E> fe) {
		return arg -> {
			try {
				return fe.apply(arg);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		};
	}

	@SuppressWarnings("serial")
	protected static class RestServicesServiceException extends Exception {

		private RestServicesServiceException() {
			super();
		}

		private RestServicesServiceException(String arg0, Throwable arg1) {
			super(arg0, arg1);
		}

		private RestServicesServiceException(String arg0) {
			super(arg0);
		}

		private RestServicesServiceException(Throwable arg0) {
			super(arg0);
		}
	}
}
