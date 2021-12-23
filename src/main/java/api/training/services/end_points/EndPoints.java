package api.training.services.end_points;

import api.training.config.Config;

public class EndPoints {

	private static final String BASE_URL = Config.getConfig().getUrl();

	public static final String AUTH = BASE_URL + "/oauth/token";

	public static final String ZIP_CODE = BASE_URL + "/zip-codes";
	public static final String ZIP_CODE_EXPAND = ZIP_CODE + "/expand";

	public static final String USERS = BASE_URL + "/users";
	public static final String USERS_UPLOAD = USERS + "/upload";
}