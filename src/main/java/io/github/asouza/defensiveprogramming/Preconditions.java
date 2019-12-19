package io.github.asouza.defensiveprogramming;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstructorDescriptor;

import org.springframework.util.Assert;

public class Preconditions {

	private static Validator validator;

	static {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	public static <T> T newInstance(Class<T> klass, Object... args) {
		BeanDescriptor beanDescriptor = validator.getConstraintsForClass(klass);
		Assert.isTrue(beanDescriptor.getConstrainedConstructors().size() > 0,
				String.format("Constructor of %s must have Bean Validation annotations at the parameters", klass));
		Class<?>[] parameterTypes = Stream.of(args).map(Object::getClass)
				.collect(Collectors.toList()).toArray(new Class[] {});
		ConstructorDescriptor constructorDescriptor = beanDescriptor.getConstraintsForConstructor(parameterTypes);
		System.out.println(constructorDescriptor);
		return null;
	}

}
