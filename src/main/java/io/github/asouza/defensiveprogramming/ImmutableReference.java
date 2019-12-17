package io.github.asouza.defensiveprogramming;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.cglib.proxy.Enhancer;

public class ImmutableReference<T> {

	@SuppressWarnings("unchecked")
	public static <T> T of(T instance) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(instance.getClass());
		ChangeProtector changeProtector = new ChangeProtector(instance);
		enhancer.setCallback(changeProtector);
		return (T) enhancer.create();
	}

	public static <T> Collection<T> of(Collection<T> instances) {
		return instances.stream().map(ImmutableReference::of).collect(Collectors.toList());

	}

}
