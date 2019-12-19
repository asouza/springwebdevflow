package io.github.asouza.defensiveprogramming;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.cglib.proxy.Enhancer;

/**
 * Transform mutable references in immutable references.
 * @author alberto
 *
 * @param <T>
 */
public class ImmutableReference<T> {

	/**
	 * 
	 * @param <T> type of object
	 * @param instance instance that should be protected for change
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T of(T instance) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(instance.getClass());
		ChangeProtector changeProtector = new ChangeProtector(instance);
		enhancer.setCallback(changeProtector);
		return (T) enhancer.create();
	}

	/**
	 * 
	 * @param <T> type of each object in collection
	 * @param instances instances that should be protected for change 
	 * @return
	 */
	public static <T> Collection<T> of(Collection<T> instances) {
		return instances.stream().map(ImmutableReference::of).collect(Collectors.toList());

	}

}
