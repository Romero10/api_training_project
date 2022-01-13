package zip_code;

import api.training.dto.Sex;
import api.training.dto.UserDto;
import api.training.services.UserService;
import api.training.services.ZipCodeService;
import com.beust.jcommander.internal.Lists;
import io.qameta.allure.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.internal.collections.Pair;

import java.util.ArrayList;
import java.util.List;

@Feature("Zip Codes")
public class TC_ZipCodeTest {

	private List<String> zipCodes;
	private String randomUsedZipCode;

	private SoftAssert softAssert;

	@BeforeClass
	public void createZipCodeAndUser() {
		List<UserDto> usersList = UserService.getUsers().second();
		ZipCodeService.addZipCodes(Lists.newArrayList(RandomUtils.nextInt(100000, 999999)));

		if (usersList.size() == 0) {
			Pair<Integer, List<String>> availableZipCodes = ZipCodeService.getAvailableZipCodes();
			randomUsedZipCode = availableZipCodes.second()
					.get(RandomUtils.nextInt(0, availableZipCodes.second().size() - 1));

			UserDto userDto = new UserDto();
			userDto.setName(RandomStringUtils.randomAlphabetic(6));
			userDto.setAge(RandomUtils.nextInt(1, 99));
			userDto.setSex(Sex.values()[RandomUtils.nextInt(0, Sex.values().length - 1)]);
			userDto.setZipCode(randomUsedZipCode);

			UserService.createUser(userDto);
		} else {
			randomUsedZipCode = usersList.get(RandomUtils.nextInt(0, usersList.size() - 1)).getZipCode();
		}
	}

	@BeforeMethod()
	public void generateRandomZipCodeList() {
		int randomValue = RandomUtils.nextInt(10000, 99999);
		zipCodes = new ArrayList<>();
		zipCodes.add(randomValue + "1");
		zipCodes.add(randomValue + "2");

		softAssert = new SoftAssert();
	}

	@Issue("IS-12234")
	@TmsLink("Task 20 - Scenario #1")
	@Test
	@Description("Test gets all available zip codes in the application for now.")
	public void availableZipCodesTest() {
		Pair<Integer, List<String>> availableZipCodes = ZipCodeService.getAvailableZipCodes();
		System.out.println(availableZipCodes.second());
		Assert.assertEquals(availableZipCodes.first().intValue(), HttpStatus.SC_OK,
				"Response code is NOT 200 when getting a list of zip codes.");
	}

	@TmsLink("Task 20 - Scenario #2")
	@Test
	@Description("Verify that zip codes from request body are added to available zip codes of application.")
	public void verifyAddZipCodesTest() {
		int responseStatusCode = ZipCodeService.addZipCodes(zipCodes);
		softAssert.assertEquals(responseStatusCode, HttpStatus.SC_CREATED,
				"Response code is NOT 201 when adding a list of zip codes.");

		Pair<Integer, List<String>> availableZipCodes = ZipCodeService.getAvailableZipCodes();

		zipCodes.forEach(zipCode -> softAssert.assertTrue(availableZipCodes.second().contains(zipCode),
				String.format("Zip Code '%s' are NOT added to available zip codes of application.", zipCode)));

		softAssert.assertAll();
	}

	@Issue("IS-12235")
	@TmsLink("Task 20 - Scenario #3")
	@Test
	@Description("Verify that zip codes from request body are added to available zip code " +
			"and there are no duplications in available zip code.")
	public void verifyAddDuplicationsZipCodesTest() {
		Pair<Integer, List<String>> availableZipCodes = ZipCodeService.getAvailableZipCodes();

		String duplicateZipCode = availableZipCodes.second().get(0);
		zipCodes.add(duplicateZipCode);

		int responseStatusCode = ZipCodeService.addZipCodes(zipCodes);
		softAssert.assertEquals(responseStatusCode, HttpStatus.SC_CREATED,
				"Response code is NOT 201 when adding a list of zip codes " +
						"which have duplicates for available zip code.");

		availableZipCodes = ZipCodeService.getAvailableZipCodes();

		softAssert.assertEquals(availableZipCodes.second().stream().filter(zipCode -> zipCode.equals(duplicateZipCode)).count(), 1,
				"There are duplicates in available zip codes when adding a list of zip codes.");

		softAssert.assertAll();
	}

	@Issue("IS-12236")
	@TmsLink("Task 20 - Scenario #4")
	@Test
	@Description("Verify that zip codes from request body are added to available zip codes " +
			"and there are no duplications between available zip codes and already used zip codes.")
	public void verifyAddDuplicationsUsedZipCodesTest() {
		zipCodes.add(randomUsedZipCode);

		int responseStatusCode = ZipCodeService.addZipCodes(zipCodes);
		softAssert.assertEquals(responseStatusCode, HttpStatus.SC_CREATED,
				"Response code is NOT 201 when adding a list of zip codes " +
						"which have duplicates for already used zip code.");

		Pair<Integer, List<String>> availableZipCodes = ZipCodeService.getAvailableZipCodes();

		softAssert.assertEquals(availableZipCodes.second().stream().filter(zipCode -> zipCode.equals(randomUsedZipCode)).count(), 0,
				"There are duplicates between available zip codes and already used zip codes " +
						"when adding a list of zip codes.");

		softAssert.assertAll();
	}
}