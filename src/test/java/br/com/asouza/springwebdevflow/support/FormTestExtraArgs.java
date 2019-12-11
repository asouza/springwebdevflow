package br.com.asouza.springwebdevflow.support;

public class FormTestExtraArgs {

	private String name;

	public void setName(String name) {
		this.name = name;

	}

	public Team toModel(TeamRepository teamRepository, TeamRepository2 teamRepository2,Team team) {
		return new Team(name);
	}

}