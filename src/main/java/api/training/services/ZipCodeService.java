package api.training.services;

import api.training.services.end_points.EndPoints;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.testng.internal.collections.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class ZipCodeService extends BaseService {

	private ZipCodeService() {

	}

	public static Pair<Integer, List<String>> getAvailableZipCodes() {
		Header headerToken = getTokenHeader(SCOPE_READ);

		HttpUriRequest request = RequestBuilder.get()
				.setUri(EndPoints.ZIP_CODE)
				.setHeader(headerToken)
				.build();

		CloseableHttpResponse response = client.request(request);
		int statusCode = response.getStatusLine().getStatusCode();
		List<String> listOfZipCodes = Arrays.stream(client.parseResponseTo(String[].class, response))
				.collect(Collectors.toList());
		closeResponse();
		return Pair.of(statusCode, listOfZipCodes);
	}

	public static int addZipCodes(List<String> zipCodes) {
		Header headerToken = getTokenHeader(SCOPE_WRITE);
		RequestBuilder request = RequestBuilder.post()
				.setUri(EndPoints.ZIP_CODE_EXPAND)
				.addHeader(headerToken)
				.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
				.setEntity(new StringEntity(zipCodes.toString(), ContentType.APPLICATION_JSON));
		return getResponseStatusCode(request);
	}

	public static boolean isAnyZipCodeAvailable() {
		return !ZipCodeService.getAvailableZipCodes().second().isEmpty();
	}
}