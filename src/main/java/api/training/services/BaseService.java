package api.training.services;

import api.training.client.Client;
import api.training.dto.TokenDto;
import api.training.exceptions.Exceptions;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.testng.internal.collections.Pair;

import java.io.IOException;

public class BaseService {

	protected static CloseableHttpClient client;
	protected static final ObjectMapper mapper;
	protected static final String SCOPE_READ = "read";
	protected static final String SCOPE_WRITE = "write";

	static {
		client = Client.getInstance().getClient();
		mapper = new ObjectMapper();
	}

	protected static Header getTokenHeader(String scope) {
		TokenDto token = AuthenticationService.getInstance().getToken(scope);
		return new BasicHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token.getAccessToken());
	}

	protected static int request(HttpUriRequest request) {
		CloseableHttpResponse response = null;
		try {
			response = client.execute(request);
			return response.getStatusLine().getStatusCode();
		} catch (IOException e) {
			throw new Exceptions.RequestException(request.getMethod(), e);
		} finally {
			try {
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				throw new Exceptions.CloseResponseException(e);
			}
		}
	}

	protected static Pair<Integer, String> requestToString(HttpUriRequest request) {
		CloseableHttpResponse response = null;
		try {
			response = client.execute(request);
			return new Pair<>(response.getStatusLine().getStatusCode(), parseResponse(response));
		} catch (IOException e) {
			throw new Exceptions.RequestException(request.getMethod(), e);
		} finally {
			try {
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				throw new Exceptions.CloseResponseException(e);
			}
		}
	}

	protected static <T> Pair<Integer, T> requestToModel(Class<T> model, HttpUriRequest request) {
		CloseableHttpResponse response = null;
		try {
			response = client.execute(request);
			return new Pair<>(response.getStatusLine().getStatusCode(), parseResponse(model, response));
		} catch (IOException e) {
			throw new Exceptions.RequestException(request.getMethod(), e);
		} finally {
			try {
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				throw new Exceptions.CloseResponseException(e);
			}
		}
	}

	private static <T> T parseResponse(Class<T> aClass, CloseableHttpResponse httpResponse) {
		try {
			String jsonString = EntityUtils.toString(httpResponse.getEntity());
			return mapper.reader().forType(aClass).readValue(jsonString);
		} catch (Exception e) {
			throw new Exceptions.ResponseMappingToModelException(e);
		}
	}

	private static String parseResponse(CloseableHttpResponse httpResponse) {
		try {
			HttpEntity entity = httpResponse.getEntity();
			return EntityUtils.toString(entity);
		} catch (Exception e) {
			throw new Exceptions.ResponseParseToStringException(e);
		}
	}
}