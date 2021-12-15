package api.training.config;

import ru.yandex.qatools.properties.PropertyLoader;
import ru.yandex.qatools.properties.annotations.Property;
import ru.yandex.qatools.properties.annotations.Resource;

@Resource.Classpath("training.properties")
public class Config {

	private static Config instance = null;

	@Property("main.url")
	private String url;

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
		return url;
	}

	public String getUserName() {
		return userName;
	}

	public String getUserPassword() {
		return userPassword;
	}
}