package br.com.asouza.springwebdevflow.support;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public class FakeCrudRepository implements CrudRepository {
	
	private boolean saveWasCalled;
	
	public boolean isSaveWasCalled() {
		return saveWasCalled;
	}
	
	@Override
	public Object save(Object entity) {
		saveWasCalled = true;
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
}