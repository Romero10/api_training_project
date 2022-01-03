package api.training.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.RandomUtils;

@JsonFormat(shape = JsonFormat.Shape.STRING)
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

	public static Sex getRandom() {
		return Sex.values()[RandomUtils.nextInt(0, Sex.values().length)];
	}
}