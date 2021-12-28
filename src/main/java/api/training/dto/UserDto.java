package api.training.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

	@JsonProperty("age")
	private int age;

	@JsonProperty("name")
	private String name;

	@JsonProperty("sex")
	private Sex sex;

	@JsonProperty("zipCode")
	private String zipCode;

	public String toString() {
		return "{\"age\":\"" + age + "\"," +
				"\"name\":\"" + name + "\"," +
				"\"sex\":\"" + sex.getSexName() + "\"," +
				"\"zipCode\":\"" + zipCode + "\"}";
	}
}