package br.com.asouza.springwebdevflow.support;

import java.util.Optional;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.support.Repositories;

public class TestRepositories extends Repositories {
	
	public TestRepositories(ListableBeanFactory factory) {
		super(factory);
	}

	@Override
	public Optional<Object> getRepositoryFor(Class<?> domainClass) {
		return Optional.of(new CrudRepository() {

			@Override
			public Object save(Object entity) {
				return entity;
			}

			@Override
			public Iterable saveAll(Iterable entities) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Optional findById(Object id) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean existsById(Object id) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public Iterable findAll() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Iterable findAllById(Iterable ids) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public long count() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public void deleteById(Object id) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void delete(Object entity) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void deleteAll(Iterable entities) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void deleteAll() {
				// TODO Auto-generated method stub
				
			}
		});
	}
}