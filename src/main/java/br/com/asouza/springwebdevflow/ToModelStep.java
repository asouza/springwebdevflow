package br.com.asouza.springwebdevflow;

import javax.persistence.Entity;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.repository.support.Repositories;
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


	@SuppressWarnings("unchecked")
	public ToModelStep(T domainObject, Repositories repositories,BeanFactory beanFactory, FormFlowAsyncExecutor flowAsyncExecutor) {
		Assert.notNull(domainObject.getClass().getAnnotation(Entity.class),"Your domain object must be annotaded with @Entity");
		
		this.flowAsyncExecutor = flowAsyncExecutor;
		this.domainObject = domainObject;
		Class<T> clazz = (Class<T>) domainObject.getClass();
		this.crudMethods = FormFlowCrudMethods.create(clazz,repositories,beanFactory);
	}
	
	public FormFlowManagedEntity<T> save() {				
		return new FormFlowManagedEntity<T>(crudMethods.save(domainObject),flowAsyncExecutor);		
	}
	
	public T getDomainObject() {
		return domainObject;
	}
	
	

}
