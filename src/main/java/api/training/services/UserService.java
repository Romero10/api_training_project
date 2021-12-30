package api.training.services;

import api.training.dto.Sex;
import api.training.dto.UpdateUserDto;
import api.training.dto.UserDto;
import api.training.exceptions.Exceptions;
import api.training.services.end_points.EndPoints;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.testng.internal.collections.Pair;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UserService extends BaseService {

	private UserService() {
	}

	public static Pair<Integer, List<UserDto>> getUsers() {
		RequestBuilder request = getUserRequest(SCOPE_READ, HttpGet.METHOD_NAME);
		return getUserResponse(request);
	}

	public static Pair<Integer, List<UserDto>> getUsers(AgeParameter ageParameter, int age) {
		RequestBuilder request = getUserRequest(SCOPE_READ, HttpGet.METHOD_NAME);
		request.addParameter(ageParameter.getParameter(), String.valueOf(age));
		return getUserResponse(request);
	}

	public static Pair<Integer, List<UserDto>> getUsers(Sex sex) {
		RequestBuilder request = getUserRequest(SCOPE_READ, HttpGet.METHOD_NAME);
		request.addParameter("sex", sex.getSexName());
		return getUserResponse(request);
	}

	public static int createUser(UserDto userDto) {
		RequestBuilder request = getUserRequest(SCOPE_WRITE, HttpPost.METHOD_NAME);
		request.setEntity(new StringEntity(userDto.toString(), ContentType.APPLICATION_JSON));
		return getResponseStatusCode(request);
	}

	public static int deleteUser(UserDto userDto) {
		RequestBuilder request = getUserRequest(SCOPE_WRITE, HttpDelete.METHOD_NAME);
		request.setEntity(new StringEntity(userDto.toString(), ContentType.APPLICATION_JSON));
		return getResponseStatusCode(request);
	}

	public static int updateUser(UpdateUserDto updateUserDto) {
		RequestBuilder request = getUserRequest(SCOPE_WRITE, HttpPut.METHOD_NAME);
		request.setEntity(new StringEntity(updateUserDto.toString(), ContentType.APPLICATION_JSON));
		return getResponseStatusCode(request);
	}

	public static Pair<Integer, String> uploadUsersFrom(String fileName) {
		Header headerToken = getTokenHeader(SCOPE_WRITE);
		File file = new File(System.getProperty("user.dir") + "/testData/" + fileName);

		HttpEntity httpEntity = MultipartEntityBuilder.create()
				.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
				.addBinaryBody("file", file, ContentType.DEFAULT_BINARY, file.getAbsolutePath())
				.build();

		HttpUriRequest request = RequestBuilder.post()
				.setUri(EndPoints.USERS_UPLOAD)
				.addHeader(headerToken)
				.setEntity(httpEntity)
				.build();

		CloseableHttpResponse response = client.request(request);
		int statusCode = response.getStatusLine().getStatusCode();
		String responseString = client.parseResponse(response);
		closeResponse();
		return Pair.of(statusCode, responseString);
	}

	private static RequestBuilder getUserRequest(String scope, String method) {
		Header headerToken = getTokenHeader(scope);
		return RequestBuilder.create(method)
				.setUri(EndPoints.USERS)
				.addHeader(headerToken)
				.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
	}

	private static Pair<Integer, List<UserDto>> getUserResponse(RequestBuilder request) {
		CloseableHttpResponse response = client.request(request.build());
		int statusCode = response.getStatusLine().getStatusCode();
		List<UserDto> listOfZipCodes = Arrays.stream(client.parseResponseTo(UserDto[].class, response))
				.collect(Collectors.toList());
		closeResponse();
		return Pair.of(statusCode, listOfZipCodes);
	}

	public enum AgeParameter {
		OLDER_THAN("olderThan"),
		YOUNGER_THAN("youngerThan");

		private final String parameter;

		AgeParameter(String parameter) {
			this.parameter = parameter;
		}

		public String getParameter() {
			return parameter;
		}
	}

	public static List<UserDto> findUsersByName(String name) {
		List<UserDto> users = UserService.getUsers().second();
		return users.stream().filter(user -> user.getName().equals(name)).collect(Collectors.toList());
	}

	public static List<UserDto> getUsersFromFile(String fileName) {
		List<UserDto> userDto;
		try {
			File file = new File(System.getProperty("user.dir") + "/testData/" + fileName);
			userDto = Arrays.stream(mapper.readValue(file, UserDto[].class)).collect(Collectors.toList());
		} catch (IOException e) {
			throw new Exceptions.JsonFileParseToUserModelException(e);
		}
		return userDto;
	}
}