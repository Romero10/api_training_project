package api.training.services;

import api.training.client.Client;
import api.training.config.Config;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;

public class BaseService {

	protected static Client client;
	protected static final Config config = Config.getConfig();

	static {
		CredentialsProvider provider = new BasicCredentialsProvider();
		UsernamePasswordCredentials credentials
				= new UsernamePasswordCredentials(config.getUserName(), config.getUserPassword());
		provider.setCredentials(AuthScope.ANY, credentials);
		client = new Client(provider);
	}
}