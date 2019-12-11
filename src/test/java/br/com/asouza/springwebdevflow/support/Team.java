package br.com.asouza.springwebdevflow.support;

import javax.persistence.Entity;

@Entity
public class Team {

	private String name;

	public Team(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

}
