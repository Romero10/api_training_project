package api.training.client;

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

	public Client(CredentialsProvider provider) {
		httpClient = HttpClientBuilder.create()
				.setDefaultCredentialsProvider(provider)
				.build();
	}

	public CloseableHttpResponse getRequest(String url) {
		CloseableHttpResponse response = null;
		HttpGet httpget = new HttpGet(url);
		try {
			response = httpClient.execute(httpget);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	public CloseableHttpResponse postRequest(HttpPost httpPost) {
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(httpPost);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	public String parseResponse(CloseableHttpResponse httpResponse) {
		String response = "";
		try {
			HttpEntity entity = httpResponse.getEntity();
			response = EntityUtils.toString(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
}