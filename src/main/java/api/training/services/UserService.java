package api.training.services;

import api.training.dto.UserDto;
import api.training.services.end_points.EndPoints;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.testng.internal.collections.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UserService extends BaseService {

	private UserService() {
	}

	public static Pair<Integer, List<UserDto>> getUsers() {
		Header headerToken = getTokenHeader(SCOPE_READ);

		HttpUriRequest request = RequestBuilder.get()
				.setUri(EndPoints.USERS)
				.setHeader(headerToken)
				.build();

		CloseableHttpResponse response = client.request(request);
		int statusCode = response.getStatusLine().getStatusCode();
		List<UserDto> listOfZipCodes = Arrays.stream(client.parseResponseTo(UserDto[].class, response))
				.collect(Collectors.toList());
		return Pair.of(statusCode, listOfZipCodes);
	}

	public static int createUser(UserDto userDto) {
		Header headerToken = getTokenHeader(SCOPE_WRITE);
		HttpUriRequest request = RequestBuilder.post()
				.setUri(EndPoints.USERS)
				.addHeader(headerToken)
				.addHeader(HttpHeaders.CONTENT_TYPE, CONTENT_JSON)
				.setEntity(new StringEntity(userDto.toString(), ContentType.APPLICATION_JSON))
				.build();
		CloseableHttpResponse response = client.request(request);
		return response.getStatusLine().getStatusCode();
	}
}