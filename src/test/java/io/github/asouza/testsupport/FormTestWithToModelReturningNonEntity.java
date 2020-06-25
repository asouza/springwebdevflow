package io.github.asouza.testsupport;

public class FormTestWithToModelReturningNonEntity {

	private String name;

	public void setName(String name) {
		this.name = name;

	}

	public NonEntity toModel(TeamRepository teamRepository) {
		NonEntity nonEntity = new NonEntity();
		nonEntity.setName(name);
		return nonEntity;
	}

}