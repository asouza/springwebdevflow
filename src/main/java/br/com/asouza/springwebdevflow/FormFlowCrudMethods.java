package br.com.asouza.springwebdevflow;

import java.util.Optional;
import java.util.function.Function;

import javax.persistence.Entity;
import javax.persistence.EntityManager;

import org.hibernate.resource.beans.container.internal.NoSuchBeanException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.util.Assert;


/**
 * Abstraction to work with crud methods. Just a wrapper
 * @author alberto
 *
 * @param <T> type of the entity which crud methods should work
 */
public class FormFlowCrudMethods<T> {
	
	private Function<T, T> save;

	/**
	 * 
	 * @param save function to save instance of T
	 */
	public FormFlowCrudMethods(Function<T, T> save) {
		this.save = save;
	}
	
	/**
	 * 
	 * @param entity save entity
	 * @return saved entity
	 */
	public T save(T entity) {
		return save.apply(entity);
	}

	/**
	 * 
	 * @param domainObjectClass class of instance of the domain object which we should work with
	 * @param repositories {@link Repositories}
	 * @param beanFactory {@link BeanFactory}
	 * @return {@link FormFlowCrudMethods} prepared to work on top of the current domain object
	 */
	public static <T> FormFlowCrudMethods<T> create(Class<T> domainObjectClass, Repositories repositories, BeanFactory beanFactory) {
		Assert.notNull(domainObjectClass.getAnnotation(Entity.class),"Your domain object must be annotaded with @Entity");
		Optional<Object> possibleDomainRepository = repositories.getRepositoryFor(domainObjectClass);
		
		if(possibleDomainRepository.isPresent()) {		
			Assert.state(possibleDomainRepository.get() instanceof CrudRepository,
					"Your repository MUST be a CrudRepository. If you don't like, don't use this flow :)");
			
			return new FormFlowCrudMethods<T>(entity -> {
				CrudRepository<T, ?> crudRepository = (CrudRepository<T, ?>) possibleDomainRepository.get();
				return crudRepository.save(entity);
			});			
		}

		
		try {
			EntityManager manager = beanFactory.getBean(EntityManager.class);
			return new FormFlowCrudMethods<T>(entity -> {
					manager.persist(entity);
					return entity;
					
			});
		} catch (NoSuchBeanException e) {
			throw new RuntimeException("Probably you don't have neither JPA or Spring Data JPA enabled");
		}		
		
		
	}

}
