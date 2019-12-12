package io.github.asouza;

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
	private FormFlowAsyncExecutor flowAsyncExecutor;
	private FormFlowCrudMethods<T> crudMethods;


	public ToModelStep(T domainObject,FormFlowCrudMethods<T> crudMethods,FormFlowAsyncExecutor flowAsyncExecutor) {
		Assert.notNull(domainObject.getClass().getAnnotation(Entity.class),"Your domain object must be annotaded with @Entity");
		this.domainObject = domainObject;
		this.crudMethods = crudMethods;
		this.flowAsyncExecutor = flowAsyncExecutor;		
	}
	
	public FormFlowManagedEntity<T> save() {				
		return new FormFlowManagedEntity<T>(crudMethods.save(domainObject),flowAsyncExecutor);		
	}
	
	public T getDomainObject() {
		return domainObject;
	}
	
	

}
