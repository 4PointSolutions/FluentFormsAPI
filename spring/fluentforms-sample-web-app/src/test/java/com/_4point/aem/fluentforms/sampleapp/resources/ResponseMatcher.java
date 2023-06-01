package com._4point.aem.fluentforms.sampleapp.resources;

import java.util.Objects;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.Response.StatusType;

public class ResponseMatcher {

	private static class IsStatus extends TypeSafeDiagnosingMatcher<Response> {
		
		private final Response.Status expected;
		
		private IsStatus(Status expectedStatus) {
			this.expected = Objects.requireNonNull(expectedStatus);
		}

		@Override
		public void describeTo(Description description) {
			description.appendText("Response HTTP status code should be %s (%d)".formatted(expected.toString(), expected.getStatusCode()));
		}

		@Override
		protected boolean matchesSafely(Response item, Description mismatchDescription) {
			StatusType actual = item.getStatusInfo();
			boolean result = actual.equals(expected);
			if (!result) {
				mismatchDescription.appendText(", but Response HTTP status code was %s (%d)".formatted(actual.toString(), actual.getStatusCode()));
			}
			return result;
		}
		
	}
	
	public static TypeSafeDiagnosingMatcher<Response> isStatus(Status expectedStatus) {
		return new IsStatus(expectedStatus);
	}

	private static class HasMediaType extends TypeSafeDiagnosingMatcher<Response> {
		
		private final MediaType expected;
		
		public HasMediaType(MediaType expectedMediaType) {
			this.expected = Objects.requireNonNull(expectedMediaType);
		}

		@Override
		public void describeTo(Description description) {
			description.appendText("Response MediaType should be '%s'".formatted(expected.toString()));
		}

		@Override
		protected boolean matchesSafely(Response item, Description mismatchDescription) {
			MediaType actual = item.getMediaType();
			boolean result = expected.isCompatible(actual);
			if (!result) {
				mismatchDescription.appendText(", but Response MediaType was '%s'.".formatted(actual == null ? "null" : actual.toString()));
			}
			return result;
		}
	}
	
	public static TypeSafeDiagnosingMatcher<Response> hasMediaType(MediaType expectedMediaType) {
		return new HasMediaType(expectedMediaType); 
	}

	private static class HasEntity extends TypeSafeDiagnosingMatcher<Response> {
		
		@Override
		public void describeTo(Description description) {
			description.appendText("Response should hava an entity.");
		}

		@Override
		protected boolean matchesSafely(Response item, Description mismatchDescription) {
			boolean result = item.hasEntity();
			if (!result) {
				mismatchDescription.appendText(", but Response did not have entity.");
			}
			return result;
		}
	}
	
	public static TypeSafeDiagnosingMatcher<Response> hasEntity() {
		return new HasEntity(); 
	}


}
