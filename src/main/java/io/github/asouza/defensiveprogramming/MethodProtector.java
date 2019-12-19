package io.github.asouza.defensiveprogramming;

import java.lang.reflect.Method;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.executable.ExecutableValidator;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.MethodType;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.util.Assert;

public class MethodProtector implements MethodInterceptor {

	private Object original;
	private Validator validator;
	private boolean thereAreValidationsToBeApplied;

	public MethodProtector(Object original, Validator validator) {
		this.original = original;
		this.validator = validator;

		BeanDescriptor beanDescriptor = validator.getConstraintsForClass(original.getClass());
		thereAreValidationsToBeApplied = !beanDescriptor.getConstrainedMethods(MethodType.NON_GETTER).isEmpty();
	}

	@Override
	public Object intercept(Object proxy, Method method, Object[] args, MethodProxy helper) throws Throwable {

		if (thereAreValidationsToBeApplied) {
			ExecutableValidator executableValidator = (ExecutableValidator) validator;
			Set<ConstraintViolation<Object>> violations = executableValidator.validateParameters(original, method,
					args);

			Assert.isTrue(violations.isEmpty(),
					String.format("Arguments passed to method[%s] are invalid. %s", method, violations));
		}

		return method.invoke(original, args);
	}

}
