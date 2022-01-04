package user;

import api.training.dto.Sex;
import api.training.dto.UpdateUserDto;
import api.training.dto.UserDto;
import api.training.services.UserService;
import api.training.services.ZipCodeService;
import com.beust.jcommander.internal.Lists;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.internal.collections.Pair;

import java.util.ArrayList;
import java.util.List;

public class TC_UserUpdate {

	private final static String NAME = "Kirill";

	private SoftAssert softAssert;
	private UserDto userDto;

	private List<UserDto> userDtoList;

	@BeforeClass
	public void initUserListForRemoving() {
		userDtoList = new ArrayList<>();
	}

	@BeforeMethod
	public void createZipCodeAndUser() {
		softAssert = new SoftAssert();
		userDto = new UserDto();

		String zipCode = RandomUtils.nextInt(10000, 99999) + "5";
		ZipCodeService.addZipCodes(Lists.newArrayList(zipCode));

		String uniqueName = NAME + RandomStringUtils.randomAlphabetic(4);
		userDto.setName(uniqueName);
		userDto.setAge(RandomUtils.nextInt(1, 99));
		userDto.setSex(Sex.getRandom());
		userDto.setZipCode(zipCode);

		UserService.createUser(userDto);
	}

	@Test
	public void verifyUpdateUserTest() {
		UserDto userUpdateDto = new UserDto();
		int newAge = RandomUtils.nextInt(1, 99);
		userUpdateDto.setName(userDto.getName());
		userUpdateDto.setAge(newAge);
		userUpdateDto.setSex(userDto.getSex());
		userUpdateDto.setZipCode(userDto.getZipCode());

		UpdateUserDto updateUser = new UpdateUserDto();
		updateUser.setUserToChange(userDto);
		updateUser.setUserNewValues(userUpdateDto);

		int statusCode = UserService.updateUser(updateUser);
		userDtoList.add(userUpdateDto);
		softAssert.assertEquals(statusCode, HttpStatus.SC_OK,
				"Response code is NOT 200 when updating a user.");

		int actualAge = UserService.findUsersByName(userUpdateDto.getName()).get(0).getAge();
		softAssert.assertEquals(actualAge, newAge, "User is NOT updated.");
		softAssert.assertAll();
	}

	@Test
	public void verifyUpdateUserWithIncorrectZipCodeTest() {
		UserDto userUpdateDto = new UserDto();
		int newAge = RandomUtils.nextInt(1, 99);
		String incorrectZipCode = RandomUtils.nextInt(10000, 99999) + "61";
		userUpdateDto.setName(userDto.getName());
		userUpdateDto.setAge(userDto.getAge());
		userUpdateDto.setSex(userDto.getSex());
		userUpdateDto.setZipCode(incorrectZipCode);

		UpdateUserDto updateUser = new UpdateUserDto();
		updateUser.setUserToChange(userDto);
		updateUser.setUserNewValues(userUpdateDto);

		int statusCode = UserService.updateUser(updateUser);
		userDtoList.add(userUpdateDto);
		softAssert.assertEquals(statusCode, HttpStatus.SC_FAILED_DEPENDENCY,
				"Response code is NOT 424 when updating a user with incorrect zip code.");

		List<UserDto> updatedUser = UserService.findUsersByName(userUpdateDto.getName());
		softAssert.assertFalse(updatedUser.isEmpty(), "Updated user exists in users when updating a user with incorrect zip code.");

		List<UserDto> users = UserService.findUsersByName(userDto.getName());
		softAssert.assertEquals(users.size(), 1, "Initial user was deleted from application when updating a user with incorrect zip code.");

		Pair<Integer, List<String>> availableZipCodes = ZipCodeService.getAvailableZipCodes();
		softAssert.assertEquals(availableZipCodes.second().stream().filter(zipCode -> zipCode.equals(userDto.getZipCode())).count(),
				0, "Zip code is in the available list for initial user.");

		softAssert.assertAll();

		int actualAge = updatedUser.get(0).getAge();
		Assert.assertEquals(actualAge, newAge, "User is updated when updating a user with incorrect zip code.");
	}

	@Test
	public void verifyUpdateUserWithRequiredFieldsMissedTest() {
		UserDto userUpdateDto = new UserDto();
		int newAge = RandomUtils.nextInt(1, 99);
		userUpdateDto.setName(userDto.getName());
		userUpdateDto.setAge(newAge);
		userUpdateDto.setZipCode(userDto.getZipCode());

		UpdateUserDto updateUser = new UpdateUserDto();
		updateUser.setUserToChange(userDto);
		updateUser.setUserNewValues(userUpdateDto);

		int statusCode = UserService.updateUser(updateUser);
		userDtoList.add(userUpdateDto);
		softAssert.assertEquals(statusCode, HttpStatus.SC_CONFLICT,
				"Response code is NOT 409 when updating a user with required fields are missed.");

		softAssert.assertEquals(UserService.findUsersByName(userDto.getName()).size(), 0,
				"User is updated when updating a user with required fields are missed.");

		softAssert.assertAll();
	}

	@Test
	public void verifyInitialUserWithoutRequiredFieldsTest() {
		UserDto initialUser = new UserDto();
		initialUser.setAge(userDto.getAge());
		initialUser.setZipCode(userDto.getZipCode());

		UserDto userUpdateDto = new UserDto();
		int newAge = RandomUtils.nextInt(1, 99);
		userUpdateDto.setName(userDto.getName());
		userUpdateDto.setSex(userDto.getSex());
		userUpdateDto.setAge(newAge);
		userUpdateDto.setZipCode(userDto.getZipCode());

		UpdateUserDto updateUser = new UpdateUserDto();
		updateUser.setUserToChange(initialUser);
		updateUser.setUserNewValues(userUpdateDto);

		int statusCode = UserService.updateUser(updateUser);
		userDtoList.add(userUpdateDto);
		softAssert.assertEquals(statusCode, HttpStatus.SC_BAD_REQUEST,
				"Response code is NOT 400 when updating a user to change without required fields.");

		int actualAge = UserService.findUsersByName(userDto.getName()).get(0).getAge();
		softAssert.assertNotEquals(actualAge, newAge,
				"User is updated when updating a user to change without required fields.");

		softAssert.assertAll();
	}

	@AfterClass
	public void removeUsers() {
		for (UserDto userDto : userDtoList) {
			UserService.deleteUser(userDto);
		}
	}
}