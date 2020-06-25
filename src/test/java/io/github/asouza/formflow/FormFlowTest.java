package io.github.asouza.formflow;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.data.repository.support.Repositories;

import io.github.asouza.formflow.FormFlow;
import io.github.asouza.formflow.ToModelStep;
import io.github.asouza.testsupport.FormTest;
import io.github.asouza.testsupport.FormTestExtraArgs;
import io.github.asouza.testsupport.FormTestWithToModelReturningNonEntity;
import io.github.asouza.testsupport.FormTestWithToModelWithoutArgs;
import io.github.asouza.testsupport.FormTestWithTwoResolvableTypes;
import io.github.asouza.testsupport.FormTestWithTwoToModels;
import io.github.asouza.testsupport.FormTestWithoutToModel;
import io.github.asouza.testsupport.MyBeanFactory;
import io.github.asouza.testsupport.NonEntity;
import io.github.asouza.testsupport.Team;
import io.github.asouza.testsupport.TestRepositories;

public class FormFlowTest {

	private FormFlow<Team> formFlow;
	private FormFlow<NonEntity> formFlowNonEntity;

	@BeforeEach
	public void setup() {
		ListableBeanFactory beanFactory = new MyBeanFactory();
		Repositories repositories = new TestRepositories(beanFactory);
		formFlow = new FormFlow<>(beanFactory, repositories);
		formFlowNonEntity = new FormFlow<>(beanFactory, repositories);
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

		ToModelStep<Team> toModelResult = formFlow.toModel(form, new Team("extraArg"));

		Team team = toModelResult.getDomainObject();
		Assertions.assertEquals("test", team.getName());
	}

	@Test
	public void shouldBuildJustTheDomainObjectReturnedByTheToModel() {
		FormTestWithToModelReturningNonEntity form = new FormTestWithToModelReturningNonEntity();
		form.setName("test");

		NonEntity toModelResult = formFlowNonEntity.justBuildDomainObject(form);

		Assertions.assertEquals("test", toModelResult.getName());
	}
}
