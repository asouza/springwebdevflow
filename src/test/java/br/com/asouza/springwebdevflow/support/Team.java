package br.com.asouza.springwebdevflow.support;

import javax.persistence.Entity;

@Entity
public class Team {

	private String name;
	private Long id;	

	public Team(String name) {
		this.name = name;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

}
