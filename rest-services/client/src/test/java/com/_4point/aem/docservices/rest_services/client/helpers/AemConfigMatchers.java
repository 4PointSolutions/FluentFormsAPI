package com._4point.aem.docservices.rest_services.client.helpers;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

/**
 * Hamcrest Matchers for testing AemConfig
 */
public class AemConfigMatchers {

	// Prevent Instantiation
	private AemConfigMatchers() {
	}

	public static Matcher<AemConfig> useSsl(Matcher<? super Boolean> subMatcher) {
		return new UseSslMatcher(subMatcher);
	}
	
	public static Matcher<AemConfig> user(Matcher<? super String> subMatcher) {
		return new UserMatcher(subMatcher);
	}
	
	public static Matcher<AemConfig> password(Matcher<? super String> subMatcher) {
		return new PasswordMatcher(subMatcher);
	}

	public static Matcher<AemConfig> servername(Matcher<? super String> subMatcher) {
		return new ServerNameMatcher(subMatcher);
	}

	public static Matcher<AemConfig> port(Matcher<? super Integer> subMatcher) {
		return new PortMatcher(subMatcher);
	}

	private static class UseSslMatcher extends FeatureMatcher<AemConfig, Boolean> {

		public UseSslMatcher(Matcher<? super Boolean> subMatcher) {
			super(subMatcher, "UseSsl", "UseSsl");
		}

		@Override
		protected Boolean featureValueOf(AemConfig actual) {
			return actual.useSsl();
		}
	}
	
	private static class UserMatcher extends FeatureMatcher<AemConfig, String> {

		public UserMatcher(Matcher<? super String> subMatcher) {
			super(subMatcher, "User", "User");
		}

		@Override
		protected String featureValueOf(AemConfig actual) {
			return actual.user();
		}
	}
	
	private static class PasswordMatcher extends FeatureMatcher<AemConfig, String> {

		public PasswordMatcher(Matcher<? super String> subMatcher) {
			super(subMatcher, "Password", "Password");
		}

		@Override
		protected String featureValueOf(AemConfig actual) {
			return actual.password();
		}
	}

	private static class ServerNameMatcher extends FeatureMatcher<AemConfig, String> {

		public ServerNameMatcher(Matcher<? super String> subMatcher) {
			super(subMatcher, "ServerName", "ServerName");
		}

		@Override
		protected String featureValueOf(AemConfig actual) {
			return actual.servername();
		}
	}

	private static class PortMatcher extends FeatureMatcher<AemConfig, Integer> {

		public PortMatcher(Matcher<? super Integer> subMatcher) {
			super(subMatcher, "Port", "Port");
		}

		@Override
		protected Integer featureValueOf(AemConfig actual) {
			return actual.port();
		}
	}
}
