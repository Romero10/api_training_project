package api.training.config;

import ru.yandex.qatools.properties.PropertyLoader;
import ru.yandex.qatools.properties.annotations.Property;
import ru.yandex.qatools.properties.annotations.Resource;

@Resource.Classpath("training.properties")
public class Config {

	private static Config instance = null;

	@Property("main.host")
	private String host;

	@Property("main.port")
	private int port;

	@Property("credentials.username")
	private String userName;

	@Property("credentials.password")
	private String userPassword;

	private Config() {
		PropertyLoader.populate(this);
	}

	public static Config getConfig() {
		if (instance == null) {
			instance = new Config();
		}
		return instance;
	}

	public String getUrl() {
		return "http://" + getHost() + ":" + getPort();
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getUserName() {
		return userName;
	}

	public String getUserPassword() {
		return userPassword;
	}
}