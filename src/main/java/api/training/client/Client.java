package api.training.client;

import api.training.config.Config;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public class Client {

	private static volatile Client instance;
	private static final ThreadLocal<CloseableHttpClient> client = new ThreadLocal<>();

	public static Client getInstance() {
		Client localInstance = instance;
		if (localInstance == null) {
			synchronized (Client.class) {
				localInstance = instance;
				if (localInstance == null) {
					instance = localInstance = new Client();
				}
			}
		}
		return instance;
	}

	public boolean isClientNull() {
		return client.get() == null;
	}

	public CloseableHttpClient getClient() {
		if (isClientNull()) {
			client.set(getConfiguredClient());
		}
		return client.get();
	}

	private CloseableHttpClient getConfiguredClient() {
		CredentialsProvider provider = new BasicCredentialsProvider();
		UsernamePasswordCredentials credentials
				= new UsernamePasswordCredentials(Config.getConfig().getUserName(), Config.getConfig().getUserPassword());
		provider.setCredentials(AuthScope.ANY, credentials);
		return HttpClientBuilder.create()
				.setDefaultCredentialsProvider(provider)
				.build();
	}
}