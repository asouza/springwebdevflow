package io.github.asouza.defensiveprogramming;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ExecutePreconditionsTest {

	@Test
	void shouldAddBeanValidationAnnotationAtConstructor() throws Exception {
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> Preconditions.newInstance(UnprotectedEntity.class, ""));

	}

	static class UnprotectedEntity {
		public UnprotectedEntity(String name) {
			
		}
	}

	static class ProtectedEntity {

		public ProtectedEntity(@NotBlank String name) {
		}

		public void logic(@NotNull Integer value) {

		}
	}
}
