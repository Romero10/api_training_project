package api.training.services;

import api.training.dto.TokenDto;
import api.training.services.end_points.EndPoints;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;

import java.util.HashMap;
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
			HttpUriRequest request = RequestBuilder.post()
					.setUri(EndPoints.AUTH)
					.addParameter("grant_type", "client_credentials")
					.addParameter("scope", scope)
					.build();

			CloseableHttpResponse response = client.request(request);
			tokens.put(scope, client.parseResponseTo(TokenDto.class, response));
		}
		return tokens.get(scope);
	}
}