package br.com.asouza.springwebdevflow.support;

import java.util.List;

import javax.persistence.Entity;

@Entity
public class Team {

	private String name;
	private Long id;
	private Object property2;
	private Object property3;
	private List<ComplexProperty> property4;
	
	@Deprecated
	public Team() {
	}

	public Team(String name) {
		this.name = name;				
	}
	
	public void setProperty2(Object property2) {
		this.property2 = property2;
	}
	
	public Object getProperty2() {
		return property2;
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

	public void setProperty3(Object property3) {
		this.property3 = property3;
		
	}
	
	public Object getProperty3() {
		return property3;
	}

	public void setProperty4(List<ComplexProperty> property4) {
		this.property4 = property4;
		
	}
	
	
	public List<ComplexProperty> getProperty4() {
		return property4;
	}
	

}
