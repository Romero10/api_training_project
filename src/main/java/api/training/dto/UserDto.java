package api.training.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class UserDto implements IModel {

	@JsonProperty("age")
	private int age;

	@JsonProperty("name")
	private String name;

	@JsonProperty("sex")
	private Sex sex;

	@JsonProperty("zipCode")
	private String zipCode;

    public String toString() {
        return getJsonString(UserDto.class);
    }
}