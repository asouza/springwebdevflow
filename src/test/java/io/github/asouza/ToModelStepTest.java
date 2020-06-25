package io.github.asouza;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.asouza.formflow.FormFlowCrudMethods;
import io.github.asouza.formflow.ToModelStep;
import io.github.asouza.support.NonEntity;
import io.github.asouza.support.Team;

public class ToModelStepTest {

	@Test
	public void shouldNotAcceptNonEntityAsConstructorParameter() {
		FormFlowCrudMethods<Team> crudMethods = new FormFlowCrudMethods<Team>((team) -> {
			team.setId(1000l);
			return team;
		}); 
		
		Team newTeam = new Team("bla");
		new ToModelStep<>(newTeam,crudMethods).save();
		Assertions.assertEquals(1000l, newTeam.getId());
	}
	
	@Test
	public void shouldNotAcceptNonEntityConstructorParameter() {
		FormFlowCrudMethods<NonEntity> crudMethods = new FormFlowCrudMethods<NonEntity>((nonEntity) -> {
			return nonEntity;
		});		
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> new ToModelStep<>(new NonEntity(), crudMethods));
	}
	
	
}
