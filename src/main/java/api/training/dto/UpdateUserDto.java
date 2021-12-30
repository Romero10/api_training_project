package api.training.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateUserDto {

	private UserDto userNewValues;
	private UserDto userToChange;

	public String toString() {
		return "{\"userNewValues\":"
				+ userNewValues.toString()
				+ ",\"userToChange\":"
				+ userToChange.toString()
				+ "}";
	}
}