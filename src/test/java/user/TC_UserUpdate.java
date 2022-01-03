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

		List<UserDto> users = UserService.findUsersByName(userUpdateDto.getName());
		softAssert.assertFalse(users.isEmpty(), "Updated user does not exist in users.");

		softAssert.assertAll();

		int actualAge = users.get(0).getAge();
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

	@AfterClass
	public void removeUsers() {
		for (UserDto userDto : userDtoList) {
			UserService.deleteUser(userDto);
		}
	}
}