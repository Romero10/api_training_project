package api.training.services;

import api.training.client.Client;
import api.training.config.Config;
import api.training.dto.TokenDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicHeader;

public class BaseService {

	protected static Client client;
	protected static ObjectMapper mapper;
	protected static final Config config = Config.getConfig();
	protected static final String SCOPE_READ = "read";
	protected static final String SCOPE_WRITE = "write";

	static {
		CredentialsProvider provider = new BasicCredentialsProvider();
		UsernamePasswordCredentials credentials
				= new UsernamePasswordCredentials(config.getUserName(), config.getUserPassword());
		provider.setCredentials(AuthScope.ANY, credentials);
		client = new Client(provider);
		mapper = new ObjectMapper();
	}

	protected static Header getTokenHeader(String scope) {
		TokenDto token = AuthenticationService.getInstance().getToken(scope);
		return new BasicHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token.getAccessToken());
	}

	protected static int getResponseStatusCode(RequestBuilder request) {
		CloseableHttpResponse response = client.request(request.build());
		closeResponse();
		return response.getStatusLine().getStatusCode();
	}

	protected static void closeResponse() {
		client.closeResponse();
	}
}