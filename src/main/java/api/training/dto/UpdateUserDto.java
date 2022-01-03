package api.training.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateUserDto implements IModel {

	private UserDto userNewValues;
	private UserDto userToChange;

	public String toString() {
		return getJsonString(UpdateUserDto.class);
	}
}