package user;

import api.training.dto.Sex;
import api.training.dto.UserDto;
import api.training.services.UserService;
import api.training.services.UserService.AgeParameter;
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

public class TC_UserFilter {

	private SoftAssert softAssert;

	private List<UserDto> userDtoList;

	@BeforeClass
	public void createZipCodesAndUsers() {
		userDtoList = new ArrayList<>();
		int defaultZipCode = RandomUtils.nextInt(10000, 99999);
		List<String> zipCodes = Lists.newArrayList(defaultZipCode + "1", defaultZipCode + "2", defaultZipCode + "3");
		ZipCodeService.addZipCodes(zipCodes);

		for (int i = 0; i < 3; i++) {
			UserDto userDto = new UserDto();
			userDto.setName(RandomStringUtils.randomAlphabetic(6));
			userDto.setAge(RandomUtils.nextInt(1, 99));
			userDto.setSex(Sex.getRandom());
			userDto.setZipCode(zipCodes.get(i));
			UserService.createUser(userDto);
			userDtoList.add(userDto);
		}
	}

	@BeforeMethod
	public void initSoftAssert() {
		softAssert = new SoftAssert();
	}

	@Test
	public void verifyGetAllStoredUsersTest() {
		Pair<Integer, List<UserDto>> storedUsers = UserService.getUsers();
		System.out.printf("Users stored in the application: %s%n", storedUsers.second());
		Assert.assertEquals(storedUsers.first().intValue(), HttpStatus.SC_OK,
				"Response code is NOT 200 when getting all users stored in the application for now.");
	}

	@Test
	public void verifyGetAllUsersOlderThanTest() {
		int parameterValue = RandomUtils.nextInt(1, 99);
		Pair<Integer, List<UserDto>> filteredUsers = UserService.getUsers(AgeParameter.OLDER_THAN, parameterValue);

		softAssert.assertEquals(filteredUsers.first().intValue(), HttpStatus.SC_OK,
				"Response code is NOT 200 when getting all users older than value of parameter.");

		filteredUsers.second().forEach(user -> softAssert.assertTrue(user.getAge() > parameterValue,
				String.format("User %s (age: %s) older than value of parameter - %s.",
						user.getName(), user.getAge(), parameterValue)));
		softAssert.assertAll();
	}

	@Test
	public void verifyGetAllUsersYoungerThanTest() {
		int parameterValue = RandomUtils.nextInt(1, 99);
		Pair<Integer, List<UserDto>> filteredUsers = UserService.getUsers(AgeParameter.YOUNGER_THAN, parameterValue);

		softAssert.assertEquals(filteredUsers.first().intValue(), HttpStatus.SC_OK,
				"Response code is NOT 200 when getting all users younger than value of parameter.");

		filteredUsers.second().forEach(user -> softAssert.assertTrue(user.getAge() < parameterValue,
				String.format("User %s (age: %s) younger than value of parameter - %s.",
						user.getName(), user.getAge(), parameterValue)));
		softAssert.assertAll();
	}

	@Test
	public void verifyGetAllUsersBySexTest() {
		Sex parameterValue = Sex.getRandom();
		Pair<Integer, List<UserDto>> filteredUsers = UserService.getUsers(parameterValue);

		softAssert.assertEquals(filteredUsers.first().intValue(), HttpStatus.SC_OK,
				"Response code is NOT 200 when getting all users with sex value of parameter.");

		filteredUsers.second().forEach(user -> softAssert.assertEquals(user.getSex(), parameterValue,
				String.format("User %s (sex: %s) is filtered by %s parameter.",
						user.getName(), user.getSex(), parameterValue)));

		softAssert.assertAll();
	}

	@AfterClass
	public void removeUsers() {
		for (UserDto userDto : userDtoList) {
			UserService.deleteUser(userDto);
		}
	}
}