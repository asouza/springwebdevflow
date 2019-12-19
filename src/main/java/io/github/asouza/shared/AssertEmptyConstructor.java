package io.github.asouza.shared;

import java.lang.reflect.Constructor;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.util.Assert;

/**
 * Internal class to verify empty constructors
 * @author alberto
 *
 */
public class AssertEmptyConstructor {

	public static void test(Class<?> klass,String message) {
		Optional<Constructor<?>> constructorWithoutArgs = Stream.of(klass.getConstructors())
				.filter(constructor -> constructor.getParameterCount() == 0).findFirst();
		Assert.isTrue(constructorWithoutArgs.isPresent(),message);		
	}
}
