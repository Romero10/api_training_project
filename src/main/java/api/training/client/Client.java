package api.training.client;

import api.training.exceptions.Exceptions;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class Client {

	private final CloseableHttpClient httpClient;
	private final ObjectMapper mapper;
	private CloseableHttpResponse response;

	public Client(CredentialsProvider provider) {
		httpClient = HttpClientBuilder.create()
				.setDefaultCredentialsProvider(provider)
				.build();
		mapper = new ObjectMapper();
	}

	public CloseableHttpResponse request(HttpUriRequest request) {
		try {
			response = httpClient.execute(request);
		} catch (IOException e) {
			throw new Exceptions.RequestException(request.getMethod(), e);
		}
		return response;
	}

	public void closeResponse() {
		try {
			if (response != null) {
				response.close();
			}
		} catch (IOException e) {
			throw new Exceptions.CloseResponseException(e);
		}
	}

	public String parseResponse(CloseableHttpResponse httpResponse) {
		try {
			HttpEntity entity = httpResponse.getEntity();
			return EntityUtils.toString(entity);
		} catch (Exception e) {
			throw new Exceptions.ResponseParseToStringException(e);
		}
	}

	public <T> T parseResponseTo(Class<T> aClass, CloseableHttpResponse httpResponse) {
		try {
			String jsonString = EntityUtils.toString(httpResponse.getEntity());
			return mapper.reader().forType(aClass).readValue(jsonString);
		} catch (Exception e) {
			throw new Exceptions.ResponseMappingToModelException(e);
		}
	}
}