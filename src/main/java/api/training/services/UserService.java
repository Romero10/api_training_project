package api.training.services;

import api.training.dto.UserDto;
import api.training.requests.PostRequest;
import api.training.services.end_points.EndPoints;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicHeader;
import org.testng.collections.Lists;
import org.testng.internal.collections.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UserService extends BaseService {

	private UserService() {
	}

	public static Pair<Integer, List<UserDto>> getUsers() {
		Header headerToken = getTokenHeader(SCOPE_READ);
		CloseableHttpResponse response = client.getRequest(EndPoints.USERS,
				Lists.newArrayList(headerToken));
		int statusCode = response.getStatusLine().getStatusCode();
		List<UserDto> listOfZipCodes = Arrays.stream(client.parseResponseTo(UserDto[].class, response))
				.collect(Collectors.toList());
		return Pair.of(statusCode, listOfZipCodes);
	}

	public static int createUser(UserDto userDto) {
		Header headerToken = getTokenHeader(SCOPE_WRITE);
		Header header = new BasicHeader(HttpHeaders.CONTENT_TYPE, CONTENT_JSON);

		PostRequest postRequest = new PostRequest();
		postRequest.setURI(EndPoints.USERS);
		postRequest.setHeader(Lists.newArrayList(headerToken, header));
		postRequest.setParams(userDto.toString());

		CloseableHttpResponse response = client.postRequest(postRequest.getHttpPost());
		return response.getStatusLine().getStatusCode();
	}
}