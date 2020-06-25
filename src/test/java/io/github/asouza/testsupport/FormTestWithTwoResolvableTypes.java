package io.github.asouza.testsupport;

public class FormTestWithTwoResolvableTypes {

	private String name;

	public void setName(String name) {
		this.name = name;

	}

	public Team toModel(TeamRepository teamRepository,TeamRepository2 teamRepository2) {
		return new Team(name);
	}

}