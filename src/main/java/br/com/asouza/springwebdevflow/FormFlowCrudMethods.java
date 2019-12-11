package br.com.asouza.springwebdevflow;

import java.util.Optional;
import java.util.function.Function;

import javax.persistence.EntityManager;

import org.hibernate.resource.beans.container.internal.NoSuchBeanException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.util.Assert;


public class FormFlowCrudMethods<T> {
	
	private Function<T, T> save;

	public FormFlowCrudMethods(Function<T, T> save) {
		this.save = save;
	}
	
	public T save(T entity) {
		return save.apply(entity);
	}

	public static <T> FormFlowCrudMethods<T> create(Class<T> domainObjectClass, Repositories repositories, BeanFactory beanFactory) {
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
