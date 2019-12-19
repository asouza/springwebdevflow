package io.github.asouza.defensiveprogramming;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.asouza.support.ProtectedEntity;
import io.github.asouza.support.ProtectedEntityWithoutEmptyConstructor;
import io.github.asouza.support.UnprotectedEntity;

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
}
