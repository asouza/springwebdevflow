package br.com.asouza.springwebdevflow;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.data.repository.support.Repositories;

import br.com.asouza.springwebdevflow.support.FakeCrudRepository;
import br.com.asouza.springwebdevflow.support.FakeEntityManager;
import br.com.asouza.springwebdevflow.support.Goal;
import br.com.asouza.springwebdevflow.support.MyBeanFactory;
import br.com.asouza.springwebdevflow.support.NonEntity;
import br.com.asouza.springwebdevflow.support.Team;
import br.com.asouza.springwebdevflow.support.TestRepositories;
import io.github.asouza.FormFlowCrudMethods;

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
