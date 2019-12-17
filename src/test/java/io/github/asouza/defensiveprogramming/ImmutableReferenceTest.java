package io.github.asouza.defensiveprogramming;

import java.util.Arrays;
import java.util.Collection;

import io.github.asouza.support.ComplexProperty;
import io.github.asouza.support.FormTest;

public class ImmutableReferenceTest {

	public static void main(String[] args) {
		ComplexProperty complex = new ComplexProperty();
		complex.setProperty1(new FormTest());
		
		ComplexProperty allImmutable = ImmutableReference.of(complex);
//		allImmutable.setProperty1("bla");
//		allImmutable.changeProperty();
		
		FormTest complex2 = (FormTest) allImmutable.getProperty1();
//		complex2.setName("...");
		
		Collection<ComplexProperty> list = ImmutableReference.of(Arrays.asList(complex));
		list.iterator().next().changeProperty();
		
		
		

	}
}
