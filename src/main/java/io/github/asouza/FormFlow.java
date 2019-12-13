package io.github.asouza;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.data.repository.support.Repositories;
import org.springframework.util.Assert;

/**
 * 
 * The entry point of a form flow
 * 
 * @author alberto
 *
 * @param <T> Type of the Domain Object expecting to be created
 */
public class FormFlow<T> {

	private BeanFactory ctx;
	private Repositories repositories;
	private FormFlowAsyncExecutor flowAsyncExecutor;

	private static final Logger log = LoggerFactory.getLogger(FormFlow.class);

	public FormFlow(BeanFactory ctx, Repositories repositories, FormFlowAsyncExecutor flowAsyncExecutor) {
		this.ctx = ctx;
		this.repositories = repositories;
		this.flowAsyncExecutor = flowAsyncExecutor;
	}

	@SuppressWarnings({ "unchecked" })
	private T buildModel(Object form, Object... extraArgs) {
		Method[] methods = form.getClass().getMethods();
		Set<Method> toModels = Stream.of(methods).filter(method -> method.getName().equals("toModel"))
				.collect(Collectors.toSet());

		Assert.isTrue(!(toModels.size() > 1), "Your form is not allowed to have more than one toModel method");
		Assert.isTrue(toModels.size() == 1, "Your form MUST have a toModel method");

		Method toModelMethod = toModels.iterator().next();
		Class<?>[] parameterTypes = toModelMethod.getParameterTypes();

		List<Object> resolvedParameters = new ArrayList<>();

		Map<Class<? extends Object>, Object> extraArgsParameters = populateExtraArgsIfExists(extraArgs);

		Stream.of(parameterTypes).forEach(parameterType -> {
			if (extraArgsParameters.containsKey(parameterType)) {
				log.debug("Resolving extra arg toModel parameter type {}", parameterType);
				resolvedParameters.add(extraArgsParameters.get(parameterType));
			} else {
				log.debug("testing if Spring is able to lookup {}", parameterType);
				try {
					resolvedParameters.add(ctx.getBean(parameterType));
				} catch (NoSuchBeanDefinitionException e) {
					throw new RuntimeException(
							"You probably are requesting extra param which is not resolvable for the Spring Context",
							e);
				}
			}
		});

		try {
			T domainObject = (T) toModelMethod.invoke(form, resolvedParameters.toArray());
			return domainObject;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * @param form object that represents a form sent from some client. It must have
	 *             a toModel method that returns the Domain Object assignable to <T>
	 * @return The ToModelStep holding the DomainObject
	 */
	public ToModelStep<T> toModel(Object form, Object... extraArgs) {
		T domainObject = buildModel(form, extraArgs);
		@SuppressWarnings("unchecked")
		Class<T> klass = (Class<T>) domainObject.getClass();
		return new ToModelStep<T>(domainObject, FormFlowCrudMethods.create(klass, repositories, ctx),
				flowAsyncExecutor);
	}

	/**
	 * 
	 * @param form object that represents a form sent from some client. It must have
	 *             a toModel method that returns the Domain Object assignable to <T>
	 * @return The domain object built by the form
	 */
	public T justBuildDomainObject(Object form, Object... extraArgs) {
		return buildModel(form, extraArgs);
	}

	private Map<Class<? extends Object>, Object> populateExtraArgsIfExists(Object... extraArgs) {
		Map<Class<? extends Object>, Object> extraArgsParameters = new HashMap<>();
		Stream.of(extraArgs).forEach(extraArg -> {
			extraArgsParameters.put(extraArg.getClass(), extraArg);
		});
		return extraArgsParameters;
	}

	/**
	 * 
	 * @param form      object that represents a form sent from some client. It must
	 *                  have a toModel method that returns the Domain Object
	 *                  assignable to <T>
	 * @param extraArgs custom application arguments for toModel. Ex: current logged
	 *                  user
	 * @return {@link FormFlowManagedEntity} with saved instance of the Domain
	 *         Object created from the form
	 */
	public FormFlowManagedEntity<T> save(Object form, Object... extraArgs) {
		return toModel(form, extraArgs).save();
	}

}