package io.github.asouza.defensiveprogramming;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;
import javax.validation.metadata.BeanDescriptor;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.util.Assert;

import io.github.asouza.shared.AssertEmptyConstructor;

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

		Class<?>[] parameterTypes = Stream.of(args).map(Object::getClass).collect(Collectors.toList())
				.toArray(new Class[] {});

		try {
			Constructor<T> constructor = klass.getConstructor(parameterTypes);
			ExecutableValidator executableValidator = (ExecutableValidator) validator;
			Set<ConstraintViolation<T>> violations = executableValidator.validateConstructorParameters(constructor,
					args);
			Assert.isTrue(violations.isEmpty(),
					String.format("This object[%s] can't be createad because constructor parameters are invalid. %s",
							klass, violations));

			// should I do this here?
			return protectMethods(constructor.newInstance(args));

		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T protectMethods(T instance) {
		AssertEmptyConstructor.test(instance.getClass(), "Class[" + instance.getClass()
				+ "] In order to protect your methods from invalid arguments you need to provide empty constructor. Proxies are made on top of classes with empty constructors");

		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(instance.getClass());
		enhancer.setCallback(new MethodProtector(instance, validator));
		return (T) enhancer.create();
	}

}
