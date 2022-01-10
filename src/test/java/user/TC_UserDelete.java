package user;

import api.training.dto.Sex;
import api.training.dto.UserDto;
import api.training.services.UserService;
import api.training.services.ZipCodeService;
import com.beust.jcommander.internal.Lists;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.http.HttpStatus;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.internal.collections.Pair;

import java.util.ArrayList;
import java.util.List;

public class TC_UserDelete {

	private final static String NAME = "Anton";

	private String zipCode;

	private SoftAssert softAssert;
	private UserDto userDto;

	private List<UserDto> userDtoList;

	@BeforeClass
	public void initUserListForRemoving() {
		userDtoList = new ArrayList<>();
	}

	@BeforeMethod
	public void initSoftAssert() {
		softAssert = new SoftAssert();
		userDto = new UserDto();

		zipCode = RandomUtils.nextInt(10000, 99999) + "4";
		ZipCodeService.addZipCodes(Lists.newArrayList(zipCode));
	}

	@Test
	public void verifyRemoveUserTest() {
		String uniqueName = NAME + RandomStringUtils.randomAlphabetic(4);
		userDto.setName(uniqueName);
		userDto.setAge(RandomUtils.nextInt(1, 99));
		userDto.setSex(Sex.getRandom());
		userDto.setZipCode(zipCode);

		UserService.createUser(userDto);

		int statusCode = UserService.deleteUser(userDto);
		softAssert.assertEquals(statusCode, HttpStatus.SC_NO_CONTENT,
				"Response code is NOT 204 when removing a user.");

		List<UserDto> users = UserService.getUsers().second();
		softAssert.assertFalse(users.contains(userDto), "User is NOT deleted.");

		Pair<Integer, List<String>> availableZipCodes = ZipCodeService.getAvailableZipCodes();
		softAssert.assertTrue(availableZipCodes.second().contains(zipCode),
				"Zip code is NOT returned in list of available zip codes when removing a user.");

		softAssert.assertAll();
	}

	@Test
	public void verifyRemoveUserWithRequiredFieldsTest() {
		String uniqueName = NAME + RandomStringUtils.randomAlphabetic(4);
		userDto.setName(uniqueName);
		userDto.setAge(RandomUtils.nextInt(1, 99));
		userDto.setSex(Sex.getRandom());
		userDto.setZipCode(zipCode);

		UserDto deleteUser = new UserDto();
		deleteUser.setName(userDto.getName());
		deleteUser.setSex(userDto.getSex());

		UserService.createUser(userDto);

		int statusCode = UserService.deleteUser(deleteUser);
		softAssert.assertEquals(statusCode, HttpStatus.SC_NO_CONTENT,
				"Response code is NOT 204 when removing a user with required fields.");

		softAssert.assertEquals(UserService.findUsersBy(uniqueName).size(), 0,
				"User with required fields is NOT deleted.");

		Pair<Integer, List<String>> availableZipCodes = ZipCodeService.getAvailableZipCodes();
		softAssert.assertTrue(availableZipCodes.second().contains(zipCode),
				"Zip code is NOT returned in list of available zip codes when removing a user with required fields.");

		softAssert.assertAll();
	}

	@Test
	public void verifyRemoveUserAnyRequiredFieldMissedTest() {
		String uniqueName = NAME + RandomStringUtils.randomAlphabetic(4);
		userDto.setName(uniqueName);
		userDto.setAge(RandomUtils.nextInt(1, 99));
		userDto.setSex(Sex.MALE);
		userDto.setZipCode(zipCode);

		UserDto deleteUser = new UserDto();
		deleteUser.setName(userDto.getName());
		deleteUser.setAge(userDto.getAge());

		UserService.createUser(userDto);
		userDtoList.add(userDto);

		int statusCode = UserService.deleteUser(deleteUser);
		softAssert.assertEquals(statusCode, HttpStatus.SC_CONFLICT,
				"Response code is NOT 409 when removing a user (any required field is missed).");

		softAssert.assertEquals(UserService.findUsersBy(uniqueName).size(),
				1, "User is deleted when removing a user (any required field is missed).");

		Pair<Integer, List<String>> availableZipCodes = ZipCodeService.getAvailableZipCodes();
		softAssert.assertFalse(availableZipCodes.second().contains(zipCode),
				"Zip code is returned in list of available zip codes when removing a user (any required field is missed).");

		softAssert.assertAll();
	}

	@AfterClass
	public void removeUsers() {
		for (UserDto userDto : userDtoList) {
			UserService.deleteUser(userDto);
		}
	}
}