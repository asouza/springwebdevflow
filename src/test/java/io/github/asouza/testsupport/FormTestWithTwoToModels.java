package io.github.asouza.testsupport;

public class FormTestWithTwoToModels {

	private String name;

	public void setName(String name) {
		this.name = name;

	}

	public Team toModel(TeamRepository teamRepository) {
		return new Team(name);
	}
	
	public Team toModel() {
		return new Team("bla");
	}

}