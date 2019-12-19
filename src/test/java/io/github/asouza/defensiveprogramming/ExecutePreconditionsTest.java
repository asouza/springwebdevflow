package io.github.asouza.defensiveprogramming;

import static org.assertj.core.api.Assertions.in;

import javax.validation.constraints.Min;
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

	@Test
	void shouldValidateAnnotatedConstructorArgs() throws Exception {
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> Preconditions.newInstance(ProtectedEntity.class, ""));

	}

	@Test
	void shouldCreateValidAnnotatedConstructorArgs() throws Exception {
		ProtectedEntity instance = Preconditions.newInstance(ProtectedEntity.class, "bla bla");
		Assertions.assertEquals("bla bla", instance.getName());

	}

	@Test
	void shouldValidateAnnotatedMethodArgs() throws Exception {
		ProtectedEntity instance = Preconditions.newInstance(ProtectedEntity.class, "bla bla");
		Assertions.assertThrows(IllegalArgumentException.class, () -> instance.logic(0));

	}

	@Test
	void shouldExecuteMethodWithValidArgs() throws Exception {
		ProtectedEntity instance = Preconditions.newInstance(ProtectedEntity.class, "bla bla");
		instance.logic(2);

		Assertions.assertEquals(2, instance.getValue());
	}

	@Test
	void shouldNotCreateProtectedInstanceWithoutEmptyConstructors() throws Exception {
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> Preconditions.newInstance(ProtectedEntityWithoutEmptyConstructor.class, "bla bla"));
	}

	static class UnprotectedEntity {
		public UnprotectedEntity(String name) {

		}
	}

	static class ProtectedEntityWithoutEmptyConstructor {

		public ProtectedEntityWithoutEmptyConstructor(@NotBlank String name) {
		}

	}

	static class ProtectedEntity {

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
}
