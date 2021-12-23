package api.training.exceptions;

public class Exceptions {

	private Exceptions() {
		throw new IllegalStateException("Static exception class");
	}

	//Client
	public static class GetRequestException extends RuntimeException {
		public GetRequestException() {
			super("The HTTP client cannot execute the get request.");
		}
	}

	public static class PostRequestException extends RuntimeException {
		public PostRequestException() {
			super("The HTTP client cannot execute the post request.");
		}
	}

	public static class ResponseParseToStringException extends RuntimeException {
		public ResponseParseToStringException() {
			super("Response value cannot be parsed to string.");
		}
	}

	public static class ResponseMappingToModelException extends RuntimeException {
		public ResponseMappingToModelException() {
			super("Response value cannot be mapped to model.");
		}
	}

	//PostRequest
	public static class SetURIException extends RuntimeException {
		public SetURIException() {
			super("Unable to set URI for HTTP post request.");
		}
	}

	public static class SetEntityException extends RuntimeException {
		public SetEntityException() {
			super("Unable to set entity for HTTP post request.");
		}
	}
}