package user;

import api.training.dto.Sex;
import api.training.dto.UserDto;
import api.training.services.UserService;
import api.training.services.ZipCodeService;
import com.beust.jcommander.internal.Lists;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.http.HttpStatus;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.internal.collections.Pair;

import java.util.ArrayList;
import java.util.List;

public class TC_UserUpload {

	private final static String NAME = "Ivan";

	private SoftAssert softAssert;

	private List<UserDto> userDtoList;

	@BeforeMethod
	public void createUsers() {
		softAssert = new SoftAssert();
		userDtoList = new ArrayList<>();

		for (int i = 0; i < 3; i++) {
			String zipCode = RandomUtils.nextInt(10000, 99999) + "5";
			ZipCodeService.addZipCodes(Lists.newArrayList(zipCode));

			String uniqueName = NAME + RandomStringUtils.randomAlphabetic(4);
			UserDto userDto = new UserDto();
			userDto.setName(uniqueName);
			userDto.setAge(RandomUtils.nextInt(1, 99));
			userDto.setSex(Sex.getRandom());
			userDto.setZipCode(zipCode);
			UserService.createUser(userDto);
			userDtoList.add(userDto);
		}
	}

	@Test
	public void verifyUserUploadTest() {
		String jsonFileName = "users.json";
		Pair<Integer, String> usersUpload = UserService.uploadUsersFrom(jsonFileName);
		softAssert.assertEquals(usersUpload.first().intValue(), HttpStatus.SC_CREATED,
				"Response code is NOT 201 when uploading a few users.");
		softAssert.assertEquals(usersUpload.second(), String.format("Number of users = %s", 2),
				"Response doesn't contain number of uploaded users.");

		Pair<Integer, List<UserDto>> users = UserService.getUsers();
		userDtoList.addAll(users.second());
		softAssert.assertEquals(users.second(), UserService.getUsersFromFile(jsonFileName),
				"All users are NOT replaced with users from file.");

		softAssert.assertAll();
	}

	@Test
	public void verifyUserUploadWithIncorrectZipCodeTest() {
		String jsonFileName = "usersIncorrectZipCode.json";
		Pair<Integer, String> usersUpload = UserService.uploadUsersFrom(jsonFileName);
		softAssert.assertEquals(usersUpload.first().intValue(), HttpStatus.SC_FAILED_DEPENDENCY,
				"Response code is NOT 424 when uploading a few users with incorrect zip code.");

		Pair<Integer, List<UserDto>> users = UserService.getUsers();
		softAssert.assertNotEquals(users.second(), UserService.getUsersFromFile(jsonFileName),
				"Users are uploaded from file when at least one user has incorrect (unavailable) zip code.");

		softAssert.assertAll();
	}

	@Test
	public void verifyUserUploadWithRequiredFieldsMissedTest() {
		String jsonFileName = "usersMissedRequiredField.json";
		Pair<Integer, String> usersUpload = UserService.uploadUsersFrom(jsonFileName);
		softAssert.assertEquals(usersUpload.first().intValue(), HttpStatus.SC_CONFLICT,
				"Response code is NOT 409 when uploading a few users with required fields are missed.");

		Pair<Integer, List<UserDto>> users = UserService.getUsers();
		softAssert.assertNotEquals(users.second(), UserService.getUsersFromFile(jsonFileName),
				"Users are uploaded from file when at least one user has missed required field.");

		softAssert.assertAll();
	}

	@Test
	public void verifyUserUploadWithInvalidFileTest() {
		String jsonFileName = "incorrectUsersFile.json";
		Pair<Integer, List<UserDto>> usersBefore = UserService.getUsers();
		Pair<Integer, String> usersUpload = UserService.uploadUsersFrom(jsonFileName);
		softAssert.assertEquals(usersUpload.first().intValue(), HttpStatus.SC_BAD_REQUEST,
				"Response code is NOT 400 when JSON file is invalid.");

		Pair<Integer, List<UserDto>> usersAfter = UserService.getUsers();
		softAssert.assertEquals(usersAfter, usersBefore,
				"All users are replaced with users from file when JSON file is invalid.");

		softAssert.assertAll();
	}

	@AfterMethod
	public void removeUsers() {
		for (UserDto userDto : userDtoList) {
			UserService.deleteUser(userDto);
		}
	}
}