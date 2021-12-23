package api.training.requests;

import api.training.exceptions.Exceptions;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class PostRequest {

	private final HttpPost httpPost;

	public PostRequest() {
		httpPost = new HttpPost();
	}

	public void setURI(String uri) {
		try {
			httpPost.setURI(new URI(uri));
		} catch (URISyntaxException e) {
			throw new Exceptions.SetURIException(e);
		}
	}

	public void setParams(List<NameValuePair> params) {
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(params));
		} catch (UnsupportedEncodingException e) {
			throw new Exceptions.SetEntityException(e);
		}
	}

	public HttpPost getHttpPost() {
		return httpPost;
	}
}