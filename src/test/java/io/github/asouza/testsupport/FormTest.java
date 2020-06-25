package io.github.asouza.testsupport;

public class FormTest {

	private String name;

	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public Team toModel(TeamRepository teamRepository) {
		return new Team(name);
	}

	public void changeName(String name) {
		this.name = name;
	}

}