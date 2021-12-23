package api.training.services;

import api.training.requests.PostRequest;
import api.training.services.end_points.EndPoints;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicHeader;
import org.testng.collections.Lists;
import org.testng.internal.collections.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class ZipCodeService extends BaseService {

	private ZipCodeService() {

	}

	public static Pair<Integer, List<String>> getAvailableZipCodes() {
		Header headerToken = getTokenHeader(SCOPE_READ);
		CloseableHttpResponse response = client.getRequest(EndPoints.ZIP_CODE, Lists.newArrayList(headerToken));
		int statusCode = response.getStatusLine().getStatusCode();
		List<String> listOfZipCodes = Arrays.stream(client.parseResponseTo(String[].class, response))
				.collect(Collectors.toList());
		return Pair.of(statusCode, listOfZipCodes);
	}

	public static int addZipCodes(List<String> zipCodes) {
		Header headerToken = getTokenHeader(SCOPE_WRITE);
		Header header = new BasicHeader(HttpHeaders.CONTENT_TYPE, CONTENT_JSON);

		PostRequest postRequest = new PostRequest();
		postRequest.setURI(EndPoints.ZIP_CODE_EXPAND);
		postRequest.setHeader(Lists.newArrayList(headerToken, header));
		postRequest.setParams(zipCodes.toString());

		CloseableHttpResponse response = client.postRequest(postRequest.getHttpPost());
		return response.getStatusLine().getStatusCode();
	}
}