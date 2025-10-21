package com._4point.aem.fluentforms.sampleapp.resources;

import org.hamcrest.Description;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class ResponseEntityMatchers {

	private static class StatusMatcher extends FeatureMatcher<ResponseEntity<?>, HttpStatusCode> {
		public StatusMatcher(Matcher<? super HttpStatusCode> subMatcher) {
			super(subMatcher, "status", "status");
		}

		@Override
		protected HttpStatusCode featureValueOf(ResponseEntity<?> actual) {
			return actual.getStatusCode();
		}
	}

	public static Matcher<ResponseEntity<?>> hasStatus(Matcher<? super HttpStatusCode> statusMatcher) {
		return new StatusMatcher(statusMatcher);
	}
	
	public static Matcher<ResponseEntity<?>> isStatus(HttpStatus status) {
		return new TypeSafeMatcher<ResponseEntity<?>>() {

			@Override
			protected boolean matchesSafely(ResponseEntity<?> actual) {
				return actual.getStatusCode().value() == status.value();
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("a response entity with status ").appendValue(status);
			}
			
		};
	}
	
	public static class MediaTypeMatcher extends FeatureMatcher<ResponseEntity<?>, MediaType> {
		public MediaTypeMatcher(Matcher<? super MediaType> subMatcher) {
			super(subMatcher, "media type", "media type");
		}

		@Override
		protected MediaType featureValueOf(ResponseEntity<?> actual) {
			return MediaType.valueOf(actual.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));
		}
	}

	public static Matcher<ResponseEntity<?>> hasMediaType(Matcher<? super MediaType> mediaTypeMatcher) {
		return new MediaTypeMatcher(mediaTypeMatcher);
	}

	public static Matcher<ResponseEntity<?>> isMediaType(MediaType mediaType) {
		return new MediaTypeMatcher(org.hamcrest.Matchers.equalTo(mediaType));
	}
	
	public static class MediaTypeCompatibilityMatcher extends TypeSafeMatcher<MediaType> {
		private final MediaType expected;

		public MediaTypeCompatibilityMatcher(MediaType expected) {
			super(MediaType.class);
			this.expected = expected;
		}

		@Override
		protected boolean matchesSafely(MediaType actual) {
			return actual.isCompatibleWith(expected);
		}

		@Override
		public void describeTo(Description description) {
			description.appendText("a media type compatible with ").appendValue(expected);
		}
	}

	public static Matcher<MediaType> isCompatibleWith(MediaType mediaType) {
		return new MediaTypeCompatibilityMatcher(mediaType);
	}
	
	// HasEntity matcher
	public static <T> Matcher<ResponseEntity<T>> hasEntity() {
		return new FeatureMatcher<ResponseEntity<T>, T>(
				org.hamcrest.Matchers.notNullValue(), "entity", "entity") {

			@Override
			protected T featureValueOf(ResponseEntity<T> actual) {
				return actual.getBody();
			}
		};
	}
	
	public static Matcher<ResponseEntity<String>> hasStringEntityMatching(Matcher<? super String> stringMatcher) {
		return new FeatureMatcher<ResponseEntity<String>, String>(
				stringMatcher, "string entity", "string entity") {

			@Override
			protected String featureValueOf(ResponseEntity<String> actual) {
				return actual.getBody();
			}
		};
	}
}
