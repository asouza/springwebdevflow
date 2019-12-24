package io.github.asouza.defensiveprogramming;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
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

	/**
	 * 
	 * @param <T> type of the new instance
	 * @param klass klass of the instance which should be created
	 * @param args constructor args
	 * @return a protected instance of this class
	 */
	public static <T> T newInstance(Class<T> klass, Object... args) {
		AssertEmptyConstructor.test(klass, "Class[" + klass
		+ "] In order to protect your methods from invalid arguments you need to provide empty constructor. Proxies are made on top of classes with empty constructors");
		
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
			return newProtectedInstance(constructor.newInstance(args),args);

		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T newProtectedInstance(T rawInstance,Object...args) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(rawInstance.getClass());
		enhancer.setCallback(new MethodProtector(rawInstance, validator));
		
		//FIXME I really don't know the best solution here. So I am duplicating the construction. I need to provide access to field values in the proxy
		List<Class<?>> constructorArgumentTypes = Stream.of(args).map(Object :: getClass).collect(Collectors.toList());
		return (T) enhancer.create(constructorArgumentTypes.toArray(new Class[]{}), args);
	}	
	
	@SuppressWarnings("unchecked")
	/**
	 * Do not use this method if you have a intention to pass the returned object to some framework which uses reflection to access field directly. 
	 * You will be dealing with a proxy and, right now, the proxy is created on top of a empty object using the original 
	 * one to invoke public methods!
	 * 
	 * @param <T> type of current instance
	 * @param instance instance which should be protected
	 * @return protected instance
	 */
	public static <T> T protectMethods(T instance) {
		AssertEmptyConstructor.test(instance.getClass(), "Class[" + instance.getClass()
				+ "] In order to protect your methods from invalid arguments you need to provide empty constructor. Proxies are made on top of classes with empty constructors");

		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(instance.getClass());
		enhancer.setCallback(new MethodProtector(instance, validator));
		return (T) enhancer.create();
	}

}
