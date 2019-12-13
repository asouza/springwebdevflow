package io.github.asouza;

import javax.persistence.Entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	private static final Logger log = LoggerFactory.getLogger(ToModelStep.class);



	public ToModelStep(T domainObject,FormFlowCrudMethods<T> crudMethods,FormFlowAsyncExecutor flowAsyncExecutor) {
		if(domainObject.getClass().getAnnotation(Entity.class) == null) {
			log.info("You are building a ToModelStep without a @Entity annotated object.",domainObject);
		}
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
