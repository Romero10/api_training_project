package api.training.services;

import api.training.dto.Sex;
import api.training.dto.UpdateUserDto;
import api.training.dto.UserDto;
import api.training.exceptions.Exceptions;
import api.training.services.end_points.EndPoints;
import api.training.utils.AllureUtils;
import api.training.utils.aspects.HealthCheck;
import io.qameta.allure.Step;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.testng.internal.collections.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UserService extends BaseService {

	private UserService() {
	}

	@Step("Get all users in the application")
	public static Pair<Integer, List<UserDto>> getUsers() {
		return getUsersByParams();
	}

	@Step("Get all users filtered by {ageParameter}={age} in the application")
	public static Pair<Integer, List<UserDto>> getUsers(AgeParameter ageParameter, int age) {
		return getUsersByParams(new BasicNameValuePair(ageParameter.getParameter(), String.valueOf(age)));
	}

	@Step("Get all users filtered by Sex={sex} in the application")
	public static Pair<Integer, List<UserDto>> getUsers(Sex sex) {
		return getUsersByParams(new BasicNameValuePair("sex", sex.getSexName()));
	}

	private static Pair<Integer, List<UserDto>> getUsersByParams(NameValuePair... nameValuePairs) {
		RequestBuilder request = getUserRequest(SCOPE_READ, HttpGet.METHOD_NAME);
		request.addParameters(nameValuePairs);
		return getUserResponse(request);
	}

	@Step("Create a new user: {userDto}")
	public static int createUser(UserDto userDto) {
		RequestBuilder request = getUserRequest(SCOPE_WRITE, HttpPost.METHOD_NAME);
		request.setEntity(new StringEntity(userDto.toString(), ContentType.APPLICATION_JSON));
		return request(request.build());
	}

	@Step("Remove user: {userDto}")
	public static int deleteUser(UserDto userDto) {
		RequestBuilder request = getUserRequest(SCOPE_WRITE, HttpDelete.METHOD_NAME);
		request.setEntity(new StringEntity(userDto.toString(), ContentType.APPLICATION_JSON));
		return request(request.build());
	}

	@Step("Update user: {updateUserDto}")
	public static int updateUser(UpdateUserDto updateUserDto) {
		return updateUser(updateUserDto, HttpPut.METHOD_NAME);
	}

	@Step("Update user: {updateUserDto} using {methodName} method name")
	public static int updateUser(UpdateUserDto updateUserDto, String methodName) {
		RequestBuilder request = getUserRequest(SCOPE_WRITE, methodName);
		request.setEntity(new StringEntity(updateUserDto.toString(), ContentType.APPLICATION_JSON));
		return request(request.build());
	}

	@HealthCheck
	@Step("Upload some users from {fileName} file")
	public static Pair<Integer, String> uploadUsersFrom(String fileName) {
		Header headerToken = getTokenHeader(SCOPE_WRITE);
		File file = new File(System.getProperty("user.dir") + "/testData/" + fileName);
		AllureUtils.addAllureAttachment("json file with users for uploading", file);

		HttpEntity httpEntity = MultipartEntityBuilder.create()
				.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
				.addBinaryBody("file", file, ContentType.DEFAULT_BINARY, file.getAbsolutePath())
				.build();

		HttpUriRequest request = RequestBuilder.post()
				.setUri(EndPoints.USERS_UPLOAD)
				.addHeader(headerToken)
				.setEntity(httpEntity)
				.build();

		return requestToString(request);
	}

	@HealthCheck
	private static RequestBuilder getUserRequest(String scope, String method) {
		Header headerToken = getTokenHeader(scope);
		return RequestBuilder.create(method)
				.setUri(EndPoints.USERS)
				.addHeader(headerToken)
				.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
	}

	private static Pair<Integer, List<UserDto>> getUserResponse(RequestBuilder request) {
		Pair<Integer, UserDto[]> pair = requestToModel(UserDto[].class, request.build());
		return Pair.of(pair.first(), Arrays.stream(pair.second()).collect(Collectors.toList()));
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

	@Step("Find user by {name} name and {sex} sex")
	public static List<UserDto> findUsersBy(String name, Sex sex) {
		List<UserDto> users = getUsers().second();
		return users.stream().filter(user -> user.getName().equals(name) && user.getSex().equals(sex))
				.collect(Collectors.toList());
	}

	@Step("Find user by {name} name")
	public static List<UserDto> findUsersBy(String name) {
		List<UserDto> users = getUsers().second();
		return users.stream().filter(user -> user.getName().equals(name)).collect(Collectors.toList());
	}

	@Step("Get list of users from {fileName} file")
	public static List<UserDto> getUsersFromFile(String fileName) {
		try {
			File file = new File(System.getProperty("user.dir") + "/testData/" + fileName);
			AllureUtils.addAllureAttachment("json file with users", file);
			return Arrays.stream(mapper.readValue(file, UserDto[].class)).collect(Collectors.toList());
		} catch (IOException e) {
			throw new Exceptions.JsonFileParseToUserModelException(e);
		}
	}
}