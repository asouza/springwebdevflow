package io.github.asouza;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.data.repository.support.Repositories;

import io.github.asouza.FormFlow;
import io.github.asouza.FormFlowAsyncExecutor;
import io.github.asouza.ToModelStep;
import io.github.asouza.support.FormTest;
import io.github.asouza.support.FormTestExtraArgs;
import io.github.asouza.support.FormTestWithToModelWithoutArgs;
import io.github.asouza.support.FormTestWithTwoResolvableTypes;
import io.github.asouza.support.FormTestWithTwoToModels;
import io.github.asouza.support.FormTestWithoutToModel;
import io.github.asouza.support.MyBeanFactory;
import io.github.asouza.support.Team;
import io.github.asouza.support.TestRepositories;

public class FormFlowTest {

	private FormFlow<Team> formFlow;

	@BeforeEach
	public void setup() {
		ListableBeanFactory beanFactory = new MyBeanFactory();
		Repositories repositories = new TestRepositories(beanFactory);
		FormFlowAsyncExecutor asyncExecutor = new FormFlowAsyncExecutor();
		formFlow = new FormFlow<>(beanFactory, repositories, asyncExecutor);
	}

	@Test
	public void mustNotHaveMoreThanOneToModelMethod() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> formFlow.toModel(new FormTestWithTwoToModels()));
	}

	@Test
	public void mustNotHaveZeroToModelMethod() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> formFlow.toModel(new FormTestWithoutToModel()));
	}

	@Test
	public void mustPassExtraArgsForApplicationCustomArgs() {
		Assertions.assertThrows(RuntimeException.class, () -> formFlow.toModel(new FormTestExtraArgs()));
	}
	
	@Test
	public void shouldReturnNewDomainObjectWithToModelWithoutArgs() {
		FormTestWithToModelWithoutArgs form = new FormTestWithToModelWithoutArgs();
		form.setName("test");
		ToModelStep<Team> toModelResult = formFlow.toModel(form);
		
		Team team = toModelResult.getDomainObject();
		Assertions.assertEquals("test", team.getName());
	}
	
	@Test
	public void shouldReturnNewDomainObjectWithToModelWithSpringResolvableTypes() {
		FormTest form = new FormTest();
		form.setName("test");
		ToModelStep<Team> toModelResult = formFlow.toModel(form);
		
		Team team = toModelResult.getDomainObject();
		Assertions.assertEquals("test", team.getName());
	}
	
	@Test
	public void shouldReturnNewDomainObjectWithToModelWithMoreThanOneSpringResolvableTypes() {
		FormTestWithTwoResolvableTypes form = new FormTestWithTwoResolvableTypes();
		form.setName("test");
		ToModelStep<Team> toModelResult = formFlow.toModel(form);
		
		Team team = toModelResult.getDomainObject();
		Assertions.assertEquals("test", team.getName());
	}
	
	@Test
	public void shouldReturnNewDomainObjecWithExtraArgs() {
		FormTestExtraArgs form = new FormTestExtraArgs();
		form.setName("test");
		
		ToModelStep<Team> toModelResult = formFlow.toModel(form,new Team("extraArg"));
		
		Team team = toModelResult.getDomainObject();
		Assertions.assertEquals("test", team.getName());		
	}

}
