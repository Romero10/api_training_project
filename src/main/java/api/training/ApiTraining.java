package api.training;

import api.training.dto.TokenDto;
import api.training.services.AuthenticationService;

public class ApiTraining {

	public static void main(String[] args) {
		TokenDto readToken = AuthenticationService.getInstance().getToken("read");
		System.out.println("Token: " + readToken.toString());
		TokenDto writeToken = AuthenticationService.getInstance().getToken("write");
		System.out.println("Token: " + writeToken.toString());
	}
}