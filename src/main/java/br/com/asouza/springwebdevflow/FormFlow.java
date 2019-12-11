package br.com.asouza.springwebdevflow;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.Scope;
import org.springframework.data.repository.support.Repositories;
import org.springframework.util.Assert;

/**
 * 
 * The entry point of a form flow
 * @author alberto
 *
 * @param <T> Type of the Domain Object expecting to be created
 */
@Scope("prototype")
public class FormFlow<T> {

	private BeanFactory ctx;
	private Repositories repositories;
	private FormFlowAsyncExecutor flowAsyncExecutor;
	private Map<Class<? extends Object>, Object> extraArgs = new HashMap<>();

	private static final Logger log = LoggerFactory.getLogger(FormFlow.class);

	public FormFlow(BeanFactory ctx, Repositories repositories, FormFlowAsyncExecutor flowAsyncExecutor) {
		this.ctx = ctx;
		this.repositories = repositories;
		this.flowAsyncExecutor = flowAsyncExecutor;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	/**
	 * 
	 * @param form object that represents a form sent from some client. It must have a toModel method that returns the Domain Object assignable to <T>
	 * @return The ToModelStep holding the DomainObject 
	 */
	public ToModelStep<T> toModel(Object form) {
		Method[] methods = form.getClass().getMethods();
		Set<Method> toModels = Stream.of(methods).filter(method -> method.getName().equals("toModel"))
				.collect(Collectors.toSet());

		Assert.isTrue(!(toModels.size() > 1), "Your form is not allowed to have more than one toModel method");
		Assert.isTrue(toModels.size() == 1, "Your form MUST have a toModel method");

		Method toModelMethod = toModels.iterator().next();
		Class<?>[] parameterTypes = toModelMethod.getParameterTypes();

		List<Object> resolvedParameters = new ArrayList<>();

		Stream.of(parameterTypes).forEach(parameterType -> {
			if (extraArgs.containsKey(parameterType)) {
				log.debug("Resolving extra arg toModel parameter type {}",parameterType);
				resolvedParameters.add(extraArgs.get(parameterType));
			} else {
				log.debug("testing if Spring is able to lookup {}", parameterType);
				try {
					resolvedParameters.add(ctx.getBean(parameterType));
				} catch (NoSuchBeanDefinitionException e) {
					throw new RuntimeException("You probably need to should call extraArgs before "
							+ "to supply the custom application params. Or you are requesting extra param which is not resolvable for the Spring Context", e);
				}
			}
		});

		try {
			T domainObject = (T) toModelMethod.invoke(form, resolvedParameters.toArray());
			Class<T> domainClass = (Class<T>) domainObject.getClass();
			return new ToModelStep(domainObject,FormFlowCrudMethods.create(domainClass, repositories, ctx), flowAsyncExecutor);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * @param params extra args that should be passed for toModel method. 
	 * @return just the formFlow 
	 */
	public FormFlow<T> extraArgs(Object... params) {
		Stream.of(params).forEach(extraArg -> {
			this.extraArgs.put(extraArg.getClass(), extraArg);
		});
		return this;
	}

	/**
	 * 
	 * @param form object that represents a form sent from some client. It must have a toModel method that returns the Domain Object assignable to <T> 
	 * @return {@link FormFlowManagedEntity} with saved instance of the Domain Object created from the form
	 */
	public FormFlowManagedEntity<T> save(Object form) {
		return toModel(form).save();
	}

}