package io.github.asouza.defensiveprogramming;

import io.github.asouza.support.ComplexProperty;

public class ImmutableReferenceTest {

	public static void main(String[] args) {
		ComplexProperty complex = new ComplexProperty();
		complex.setProperty1(new ComplexProperty());
		
		ComplexProperty allImmutable = ImmutableReference.of(complex);
//		allImmutable.setProperty1("bla");
		allImmutable.changeProperty();
		System.out.println(allImmutable.getProperty1());
	}
}
