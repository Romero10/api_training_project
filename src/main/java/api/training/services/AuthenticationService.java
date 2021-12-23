package api.training.services;

import api.training.dto.TokenDto;
import api.training.requests.PostRequest;
import api.training.services.end_points.EndPoints;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthenticationService extends BaseService {

	private static AuthenticationService instance = null;

	private static volatile Map<String, TokenDto> tokens = new HashMap<>();

	private AuthenticationService() {
	}

	public static AuthenticationService getInstance() {
		if (instance == null) {
			instance = new AuthenticationService();
		}
		return instance;
	}

	public synchronized TokenDto getToken(String scope) {
		if (!tokens.containsKey(scope)) {
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair("grant_type", "client_credentials"));
			params.add(new BasicNameValuePair("scope", scope));

			PostRequest postRequest = new PostRequest();
			postRequest.setURI(config.getUrl() + EndPoints.AUTH);
			postRequest.setParams(params);

			CloseableHttpResponse response = client.postRequest(postRequest.getHttpPost());
			tokens.put(scope, client.parseResponseTo(TokenDto.class, response));
		}
		return tokens.get(scope);
	}
}