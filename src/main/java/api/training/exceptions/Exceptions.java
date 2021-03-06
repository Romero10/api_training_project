package api.training.exceptions;

import java.io.IOException;

public class Exceptions {

	private Exceptions() {
		throw new IllegalStateException("Static exception class");
	}

	//Client
	public static class RequestException extends RuntimeException {
		public RequestException(String methodName, IOException e) {
			super("The HTTP client cannot execute the " + methodName + " request.", e);
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

	public static class CloseResponseException extends RuntimeException {
		public CloseResponseException(Exception e) {
			super("The response cannot be closed.", e);
		}
	}

	//Models
	public static class JsonParseModelToStringException extends RuntimeException {
		public JsonParseModelToStringException(Exception e) {
			super("DTO model cannot be parsed to json string.", e);
		}
	}

	//User service
	public static class JsonFileParseToUserModelException extends RuntimeException {
		public JsonFileParseToUserModelException(Exception e) {
			super("JSON file cannot be parsed to user model.", e);
		}
	}

	//Allure
	public static class AllureAddAttachmentException extends RuntimeException {
		public AllureAddAttachmentException(Exception e) {
			super("Allure cannot add attachment.", e);
		}
	}

	//Connection
	public static class ConnectionException extends RuntimeException {
		public ConnectionException() {
			super("Lost connection to tested app.");
		}
	}
}