package api.training.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
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
		StringBuilder builder = new StringBuilder();
		builder.append("{\"age\":").append(age).append(",").append("\"name\":");

		if (name != null) {
			builder.append("\"").append(name).append("\",");
		} else {
			builder.append(name).append(",");
		}

		builder.append("\"sex\":");
		if (sex != null) {
			builder.append("\"").append(sex.getSexName()).append("\",");
		} else {
			builder.append(sex).append(",");
		}

		builder.append("\"zipCode\":");
		if (zipCode != null) {
			builder.append("\"").append(zipCode).append("\"");
		} else {
			builder.append(zipCode);
		}

		return builder.append("}").toString();
	}
}