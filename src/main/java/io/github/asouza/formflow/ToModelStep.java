package io.github.asouza.formflow;

import javax.persistence.Entity;

import org.springframework.util.Assert;

/**
 * 
 * @author alberto
 *
 * @param <T> type of the Domain Object expected to be saved
 */
public class ToModelStep<T> {

	private T domainObject;
	private FormFlowCrudMethods<T> crudMethods;


	public ToModelStep(T domainObject, FormFlowCrudMethods<T> crudMethods) {
		Assert.notNull(domainObject.getClass().getAnnotation(Entity.class),
				String.format("You must build ToModelStep with @Entity annotated class. %s",domainObject.getClass()));
		this.domainObject = domainObject;
		this.crudMethods = crudMethods;
	}

	public FormFlowManagedEntity<T> save() {
		return new FormFlowManagedEntity<T>(crudMethods.save(domainObject));
	}

	public T getDomainObject() {
		return domainObject;
	}

}
