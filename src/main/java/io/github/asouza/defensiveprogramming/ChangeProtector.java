package io.github.asouza.defensiveprogramming;

import java.lang.reflect.Method;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import io.github.asouza.support.Mutable;

public class ChangeProtector implements MethodInterceptor {

	private Object original;

	public ChangeProtector(Object original) {
		this.original = original;
	}

	@Override
	public Object intercept(Object proxy, Method method, Object[] args, MethodProxy helper) throws Throwable {
		
		String methodName = method.getName();
		if(methodName.startsWith("set")) {
			throw new IllegalAccessException("You can't invoke a setter in this object("+original.getClass()+"). It is immutable S2");
		}
		
		if(method.isAnnotationPresent(Mutable.class)) {
			throw new IllegalAccessException("You can't invoke a mutable method in this object("+original.getClass()+")");
		}
		
		return ImmutableReference.of(method.invoke(original, args));
	}

}
