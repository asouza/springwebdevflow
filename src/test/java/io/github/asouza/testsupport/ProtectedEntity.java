package io.github.asouza.testsupport;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public class ProtectedEntity {

	private @NotBlank String name;
	private @Min(1) Integer value;

	public ProtectedEntity() {
		// TODO Auto-generated constructor stub
	}

	public ProtectedEntity(@NotBlank String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void logic(@Min(1) Integer value) {
		this.value = value;

	}

	public Integer getValue() {
		return value;
	}
}