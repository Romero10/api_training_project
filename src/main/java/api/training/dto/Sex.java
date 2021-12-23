package api.training.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Sex {

	FEMALE("FEMALE"),
	MALE("MALE");

	private final String sexName;

	Sex(String sexName) {
		this.sexName = sexName;
	}

	public String getSexName() {
		return sexName;
	}
}