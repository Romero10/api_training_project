package api.training.exceptions;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

public class Exceptions {

	private Exceptions() {
		throw new IllegalStateException("Static exception class");
	}

	//Client
	public static class GetRequestException extends RuntimeException {
		public GetRequestException(IOException e) {
			super("The HTTP client cannot execute the get request.", e);
		}
	}

	public static class PostRequestException extends RuntimeException {
		public PostRequestException(IOException e) {
			super("The HTTP client cannot execute the post request.", e);
		}
	}

	public static class ResponseParseToStringException extends RuntimeException {
		public ResponseParseToStringException(Exception e) {
			super("Response value cannot be parsed to string.", e);
		}
	}

	public static class ResponseMappingToModelException extends RuntimeException {
		public ResponseMappingToModelException(Exception e) {
			super("Response value cannot be mapped to model.", e);
		}
	}

	//PostRequest
	public static class SetURIException extends RuntimeException {
		public SetURIException(URISyntaxException e) {
			super("Unable to set URI for HTTP post request.", e);
		}
	}

	public static class SetEntityException extends RuntimeException {
		public SetEntityException(UnsupportedEncodingException e) {
			super("Unable to set entity for HTTP post request.", e);
		}
	}
}