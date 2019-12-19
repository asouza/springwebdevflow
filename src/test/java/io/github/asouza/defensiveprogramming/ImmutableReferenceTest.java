package io.github.asouza.defensiveprogramming;

import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.asouza.support.ComplexProperty;

public class ImmutableReferenceTest {

	@Test
	public void shouldNotAllowSetterForImmutableReference() {
		ComplexProperty complex = new ComplexProperty();
		complex.setProperty1("bla");

		ComplexProperty immutable = ImmutableReference.of(complex);
		Assertions.assertThrows(IllegalAccessException.class, () -> immutable.setProperty1("change"));
	}
	
	@Test
	public void shouldNotAllowMutableAnnnotatedMethodForImmutableReference() {
		ComplexProperty complex = new ComplexProperty();
		complex.changeProperty();
		
		ComplexProperty immutable = ImmutableReference.of(complex);
		Assertions.assertThrows(IllegalAccessException.class, () -> immutable.changeProperty());
	}
	
	@Test
	public void shouldBuildCollectionWithImmutableObjects() {
		ComplexProperty complex = new ComplexProperty();
		
		Collection<ComplexProperty> list = ImmutableReference.of(Arrays.asList(complex));
		Assertions.assertThrows(IllegalAccessException.class, () -> list.iterator().next().changeProperty());
	}
}
