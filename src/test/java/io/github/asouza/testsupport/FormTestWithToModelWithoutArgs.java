package io.github.asouza.testsupport;

public class FormTestWithToModelWithoutArgs {
	
	private String name;
	
	public void setName(String name) {
		this.name = name;
	}

	public Team toModel() {
		return new Team(name);
	}
}
