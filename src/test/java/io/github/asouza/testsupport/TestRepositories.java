package io.github.asouza.testsupport;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.support.Repositories;

public class TestRepositories extends Repositories {
	
	private CrudRepository customCrudRepository;
	private Set<Class> nonRepositoryClasses = new HashSet<>();

	public TestRepositories(ListableBeanFactory factory) {
		super(factory);
		this.customCrudRepository = new FakeCrudRepository();
	}
	
	public TestRepositories(ListableBeanFactory factory,CrudRepository customCrudRepository,Class... nonRepositoryClasses) {
		super(factory);
		this.customCrudRepository = customCrudRepository;
		this.nonRepositoryClasses.addAll(Stream.of(nonRepositoryClasses).collect(Collectors.toSet()));
	}

	@Override
	public Optional<Object> getRepositoryFor(Class<?> domainClass) {
		if(nonRepositoryClasses.contains(domainClass)) {
			return Optional.empty();
		}
		return Optional.of(customCrudRepository);
	}
}