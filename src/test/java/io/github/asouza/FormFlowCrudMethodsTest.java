package io.github.asouza;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.data.repository.support.Repositories;

import io.github.asouza.formflow.FormFlowCrudMethods;
import io.github.asouza.support.FakeCrudRepository;
import io.github.asouza.support.FakeEntityManager;
import io.github.asouza.support.Goal;
import io.github.asouza.support.MyBeanFactory;
import io.github.asouza.support.NonEntity;
import io.github.asouza.support.Team;
import io.github.asouza.support.TestRepositories;

public class FormFlowCrudMethodsTest {

	private ListableBeanFactory beanFactory;
	private Repositories repositories;
	private FakeCrudRepository fakeCrudRepository;
	private FakeEntityManager fakeEntityManager;

	@BeforeEach
	public void setup() {
		fakeEntityManager = new FakeEntityManager();
		beanFactory = new MyBeanFactory(fakeEntityManager);
		fakeCrudRepository = new FakeCrudRepository();
		repositories = new TestRepositories(beanFactory,fakeCrudRepository,Goal.class);
	}

	@Test
	public void shouldNotCreateWithNonEntityArg() {
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> FormFlowCrudMethods.create(NonEntity.class, repositories, beanFactory));
	}
	
	@Test
	public void shouldCreateCrudMethodsForSpringDataJpaRepository() {
		Team team = new Team("bla");
		FormFlowCrudMethods<Team> crudMethods = FormFlowCrudMethods.create(Team.class, repositories, beanFactory);		
		crudMethods.save(team);
		
		Assertions.assertTrue(fakeCrudRepository.isSaveWasCalled());
	}
	
	@Test
	public void shouldCreateCrudMethodsEvenWithoutSpringDataJpaRepository() {
		Goal goal = new Goal();
		FormFlowCrudMethods<Goal> crudMethods = FormFlowCrudMethods.create(Goal.class, repositories, beanFactory);
		
		crudMethods.save(goal);	
		
		Assertions.assertTrue(fakeEntityManager.isPersistWasCalled());		
	}
}
