package io.github.asouza.testsupport;

import javax.validation.constraints.NotBlank;

public class ProtectedEntityWithoutEmptyConstructor {

	public ProtectedEntityWithoutEmptyConstructor(@NotBlank String name) {
	}

}