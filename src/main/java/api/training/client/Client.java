package api.training.client;

import api.training.exceptions.Exceptions;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class Client {

	private final CloseableHttpClient httpClient;
	private final ObjectMapper mapper;

	public Client(CredentialsProvider provider) {
		httpClient = HttpClientBuilder.create()
				.setDefaultCredentialsProvider(provider)
				.build();
		mapper = new ObjectMapper();
	}

	public CloseableHttpResponse getRequest(String url) {
		CloseableHttpResponse response;
		HttpGet httpGet = new HttpGet(url);
		try {
			response = httpClient.execute(httpGet);
		} catch (IOException e) {
			throw new Exceptions.GetRequestException();
		}
		return response;
	}

	public CloseableHttpResponse postRequest(HttpPost httpPost) {
		CloseableHttpResponse response;
		try {
			response = httpClient.execute(httpPost);
		} catch (IOException e) {
			throw new Exceptions.PostRequestException();
		}
		return response;
	}

	public String parseResponse(CloseableHttpResponse httpResponse) {
		String response;
		try {
			HttpEntity entity = httpResponse.getEntity();
			response = EntityUtils.toString(entity);
		} catch (Exception e) {
			throw new Exceptions.ResponseParseToStringException();
		}
		return response;
	}

	public <T> T parseResponseTo(Class<T> aClass, CloseableHttpResponse httpResponse) {
		T obj;
		try {
			String jsonString = EntityUtils.toString(httpResponse.getEntity());
			obj = mapper.reader().forType(aClass).readValue(jsonString);
		} catch (Exception e) {
			throw new Exceptions.ResponseMappingToModelException();
		}
		return obj;
	}
}