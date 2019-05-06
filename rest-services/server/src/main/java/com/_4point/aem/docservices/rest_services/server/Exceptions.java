package com._4point.aem.docservices.rest_services.server;

public class Exceptions {

	private Exceptions() { // private constructor so that this class cannot be instantiated.
	}

	@SuppressWarnings("serial")
	// SC_BAD_REQUEST / status code 400
	public static class BadRequestException extends Exception {
	
		public BadRequestException(String message, Throwable cause) {
			super(message, cause);
		}
	
		public BadRequestException(String message) {
			super(message);
		}
	
		public BadRequestException(Throwable cause) {
			super(cause);
		}
		
	}

	@SuppressWarnings("serial")
	// SC_INTERNAL_SERVER_ERROR / status code 500
	public static class InternalServerErrorException extends Exception {
	
		public InternalServerErrorException(String message, Throwable cause) {
			super(message, cause);
		}
	
		public InternalServerErrorException(String message) {
			super(message);
		}
	
		public InternalServerErrorException(Throwable cause) {
			super(cause);
		}
		
	}

	@SuppressWarnings("serial")
	// SC_NOT_ACCEPTABLE / status code 406
	public static class NotAcceptableException extends Exception {

		public NotAcceptableException(String message, Throwable cause) {
			super(message, cause);
		}

		public NotAcceptableException(String message) {
			super(message);
		}

		public NotAcceptableException(Throwable cause) {
			super(cause);
		}
		
	}

	@SuppressWarnings("serial")
	// SC_UNSUPPORTED_MEDIA_TYPE / status code 415
	public static class UnsupportedMediaException extends Exception {

		public UnsupportedMediaException(String message, Throwable cause) {
			super(message, cause);
		}

		public UnsupportedMediaException(String message) {
			super(message);
		}

		public UnsupportedMediaException(Throwable cause) {
			super(cause);
		}
		
	}

}
