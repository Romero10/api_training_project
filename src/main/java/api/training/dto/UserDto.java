package api.training.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
public class UserDto implements IModel {

	@JsonProperty("age")
	private int age;

	@JsonProperty("name")
	private String name;

	@JsonProperty("sex")
	private Sex sex;

	@JsonProperty("zipCode")
	private String zipCode;

	@JsonIgnore
	private Map<String, Sex> namePlusSex = new HashMap<>();

	public String toString() {
		return getJsonString(UserDto.class);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UserDto userDto = (UserDto) o;
		return new EqualsBuilder()
				.append(getComplexKey(), userDto.getComplexKey())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return Objects.hash(age, name, sex, zipCode);
	}

	private Map<String, Sex> getComplexKey() {
		namePlusSex.put(name, sex);
		return namePlusSex;
	}
}