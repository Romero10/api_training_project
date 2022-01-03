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

public class TC_UserAdd {

	private final static String NAME = "Anton";

	private SoftAssert softAssert;
	private UserDto userDto;

	private String uniqueName;

	private List<UserDto> userDtoList;

	@BeforeClass
	public void initUserListForRemoving() {
		userDtoList = new ArrayList<>();
	}

	@BeforeMethod
	public void createZipCode() {
		softAssert = new SoftAssert();
		userDto = new UserDto();
		uniqueName = NAME + RandomStringUtils.randomAlphabetic(4);

		if (!ZipCodeService.isAnyZipCodeAvailable()) {
			String zipCode = RandomUtils.nextInt(10000, 99999) + "7";
			ZipCodeService.addZipCodes(Lists.newArrayList(zipCode));
		}
	}

	@Test
	public void verifyAddUserAndRemoveZipCodeTest() {
		Pair<Integer, List<String>> availableZipCodes = ZipCodeService.getAvailableZipCodes();
		String randomZipCode = availableZipCodes.second().get(RandomUtils.nextInt(0, availableZipCodes.second().size() - 1));

		userDto.setName(uniqueName);
		userDto.setAge(RandomUtils.nextInt(1, 99));
		userDto.setSex(Sex.getRandom());
		userDto.setZipCode(randomZipCode);

		int statusCode = UserService.createUser(userDto);
		softAssert.assertEquals(statusCode, HttpStatus.SC_CREATED,
				"Response code is NOT 201 when creating a new user.");
		userDtoList.add(userDto);

		softAssert.assertEquals(UserService.findUsersByName(uniqueName).size(),
				1, "User is NOT added to application.");

		availableZipCodes = ZipCodeService.getAvailableZipCodes();

		softAssert.assertEquals(availableZipCodes.second().stream().filter(zipCode -> zipCode.equals(randomZipCode)).count(),
				0, "Zip code is NOT removed from available zip codes of application.");

		softAssert.assertAll();
	}

	@Test
	public void verifyAddUserWithRequiredFieldsTest() {
		userDto.setName(uniqueName);
		userDto.setSex(Sex.getRandom());

		int statusCode = UserService.createUser(userDto);
		softAssert.assertEquals(statusCode, HttpStatus.SC_CREATED,
				"Response code is NOT 201 when creating a new user with required fields.");
		userDtoList.add(userDto);

		softAssert.assertEquals(UserService.findUsersByName(uniqueName).size(),
				1, "User with required fields is NOT added to application.");

		softAssert.assertAll();
	}

	@Test
	public void verifyAddUserWithIncorrectZipCodeTest() {
		userDto.setName(uniqueName);
		userDto.setAge(RandomUtils.nextInt(1, 99));
		userDto.setSex(Sex.getRandom());
		userDto.setZipCode(RandomStringUtils.randomAlphabetic(6));

		int statusCode = UserService.createUser(userDto);
		softAssert.assertEquals(statusCode, HttpStatus.SC_FAILED_DEPENDENCY,
				"Response code is NOT 424 when creating a new user with incorrect zip code.");

		softAssert.assertEquals(UserService.findUsersByName(uniqueName).size(),
				0, "User with incorrect zip code is added to application.");

		softAssert.assertAll();
	}

	@Test
	public void verifyAddNotUniqueUserTest() {
		userDto.setName(uniqueName);
		userDto.setSex(Sex.getRandom());

		UserService.createUser(userDto);
		int statusCode = UserService.createUser(userDto);
		softAssert.assertEquals(statusCode, HttpStatus.SC_BAD_REQUEST,
				"Response code is NOT 400 when creating a new user " +
						"with the same name and sex as existing user in the system.");
		userDtoList.add(userDto);

		softAssert.assertEquals(UserService.findUsersByName(uniqueName).size(),
				1, "User with the same name and sex as existing user in the system is added to application.");

		softAssert.assertAll();
	}

	@AfterClass
	public void removeUsers() {
		for (UserDto userDto : userDtoList) {
			UserService.deleteUser(userDto);
		}
	}
}