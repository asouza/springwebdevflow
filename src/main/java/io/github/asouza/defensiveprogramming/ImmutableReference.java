package io.github.asouza.defensiveprogramming;

import org.springframework.cglib.proxy.Enhancer;


public class ImmutableReference<T> {
	

	public static <T> T of(T instance) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(instance.getClass());
		ChangeProtector changeProtector = new ChangeProtector(instance);
		enhancer.setCallback(changeProtector);
		return (T) enhancer.create();
	}


	
}

