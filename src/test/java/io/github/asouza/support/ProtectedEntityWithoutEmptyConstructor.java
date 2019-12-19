package io.github.asouza.support;

import javax.validation.constraints.NotBlank;

public class ProtectedEntityWithoutEmptyConstructor {

	public ProtectedEntityWithoutEmptyConstructor(@NotBlank String name) {
	}

}