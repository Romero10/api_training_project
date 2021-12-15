package api.training;

import api.training.services.AuthenticationService;

public class ApiTraining {

	public static void main(String[] args) {
		String readToken = AuthenticationService.getInstance().getToken("read");
		System.out.println("Token: " + readToken);
		String writeToken = AuthenticationService.getInstance().getToken("write");
		System.out.println("Token: " + writeToken);
	}
}