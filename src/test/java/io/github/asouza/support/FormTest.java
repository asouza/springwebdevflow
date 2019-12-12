package io.github.asouza.support;

public class FormTest {

	private String name;

	public void setName(String name) {
		this.name = name;

	}

	public Team toModel(TeamRepository teamRepository) {
		return new Team(name);
	}

}