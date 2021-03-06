package user;

import api.training.dto.Sex;
import api.training.dto.UserDto;
import api.training.services.UserService;
import api.training.services.ZipCodeService;
import com.beust.jcommander.internal.Lists;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.TmsLink;
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

@Feature("Users")
public class TC_UserAddTest {

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

	@TmsLink("Task 30 - Scenario #1")
	@Test
	@Description("Verify that user can be added and zip code is removed from available zip codes of application.")
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

		softAssert.assertEquals(UserService.findUsersBy(uniqueName).size(),
				1, "User is NOT added to application.");

		availableZipCodes = ZipCodeService.getAvailableZipCodes();

		softAssert.assertEquals(availableZipCodes.second().stream().filter(zipCode -> zipCode.equals(randomZipCode)).count(),
				0, "Zip code is NOT removed from available zip codes of application.");

		softAssert.assertAll();
	}

	@TmsLink("Task 30 - Scenario #2")
	@Test
	@Description("Verify that user can be added with required fields.")
	public void verifyAddUserWithRequiredFieldsTest() {
		userDto.setName(uniqueName);
		userDto.setSex(Sex.getRandom());

		int statusCode = UserService.createUser(userDto);
		softAssert.assertEquals(statusCode, HttpStatus.SC_CREATED,
				"Response code is NOT 201 when creating a new user with required fields.");
		userDtoList.add(userDto);

		softAssert.assertEquals(UserService.findUsersBy(uniqueName).size(),
				1, "User with required fields is NOT added to application.");

		softAssert.assertAll();
	}

	@TmsLink("Task 30 - Scenario #3")
	@Test
	@Description("Verify that user can NOT be added with incorrect zip code.")
	public void verifyAddUserWithIncorrectZipCodeTest() {
		userDto.setName(uniqueName);
		userDto.setAge(RandomUtils.nextInt(1, 99));
		userDto.setSex(Sex.getRandom());
		userDto.setZipCode(RandomStringUtils.randomAlphabetic(6));

		int statusCode = UserService.createUser(userDto);
		softAssert.assertEquals(statusCode, HttpStatus.SC_FAILED_DEPENDENCY,
				"Response code is NOT 424 when creating a new user with incorrect zip code.");

		softAssert.assertEquals(UserService.findUsersBy(uniqueName).size(),
				0, "User with incorrect zip code is added to application.");

		softAssert.assertAll();
	}

	@Issue("IS-11111")
	@TmsLink("Task 30 - Scenario #4")
	@Test
	@Description("Verify that user can NOT be added with the same name and sex as existing user in the system.")
	public void verifyAddNotUniqueUserTest() {
		userDto.setName(uniqueName);
		userDto.setSex(Sex.getRandom());

		UserService.createUser(userDto);
		int statusCode = UserService.createUser(userDto);
		softAssert.assertEquals(statusCode, HttpStatus.SC_BAD_REQUEST,
				"Response code is NOT 400 when creating a new user " +
						"with the same name and sex as existing user in the system.");
		userDtoList.add(userDto);

		softAssert.assertEquals(UserService.findUsersBy(uniqueName).size(),
				1, "User with the same name and sex as existing user in the system is added to application.");

		softAssert.assertAll();
	}

	@Issue("IS-11112")
	@TmsLink("Task 30 - Additional scenario")
	@Test
	@Description("Verify that user can NOT be added without required fields and zip code is NOT removed from available zip codes of application.")
	public void verifyAddUserWithoutRequiredFieldsTest() {
		String addZipCode = ZipCodeService.getAvailableZipCodes().second().get(0);
		userDto.setAge(RandomUtils.nextInt(1, 99));
		userDto.setZipCode(addZipCode);

		int statusCode = UserService.createUser(userDto);
		softAssert.assertEquals(statusCode, HttpStatus.SC_CONFLICT,
				"Response code is NOT 409 when creating a new user without required fields.");

		softAssert.assertEquals(UserService.findUsersBy(uniqueName).size(),
				0, "User without required fields is added to application.");

		Pair<Integer, List<String>> availableZipCodes = ZipCodeService.getAvailableZipCodes();
		softAssert.assertEquals(availableZipCodes.second().stream().filter(zipCode -> zipCode.equals(addZipCode)).count(),
				1, "Zip code is removed from available zip codes of application when creating a user without required fields.");

		softAssert.assertAll();
	}

	@AfterClass
	public void removeUsers() {
		for (UserDto userDto : userDtoList) {
			UserService.deleteUser(userDto);
		}
	}
}