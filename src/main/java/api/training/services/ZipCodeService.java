package api.training.services;

import api.training.services.end_points.EndPoints;
import api.training.utils.aspects.HealthCheck;
import io.qameta.allure.Step;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.testng.internal.collections.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ZipCodeService extends BaseService {

	private ZipCodeService() {

	}

	@HealthCheck
	@Step("Get all available zip codes in the application")
	public static Pair<Integer, List<String>> getAvailableZipCodes() {
		Header headerToken = getTokenHeader(SCOPE_READ);

		HttpUriRequest request = RequestBuilder.get()
				.setUri(EndPoints.ZIP_CODE)
				.setHeader(headerToken)
				.build();
		Pair<Integer, String[]> pair = requestToModel(String[].class, request);
		return Pair.of(pair.first(), Arrays.stream(pair.second()).collect(Collectors.toList()));
	}

	@HealthCheck
	@Step("Create a list of zip codes: {zipCodes}")
	public static int addZipCodes(List<String> zipCodes) {
		Header headerToken = getTokenHeader(SCOPE_WRITE);
		RequestBuilder request = RequestBuilder.post()
				.setUri(EndPoints.ZIP_CODE_EXPAND)
				.addHeader(headerToken)
				.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
				.setEntity(new StringEntity(zipCodes.toString(), ContentType.APPLICATION_JSON));
		return request(request.build());
	}

	@Step("Check availability for zip codes")
	public static boolean isAnyZipCodeAvailable() {
		return !getAvailableZipCodes().second().isEmpty();
	}
}