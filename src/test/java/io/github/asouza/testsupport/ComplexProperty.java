package io.github.asouza.testsupport;

import io.github.asouza.support.Mutable;

public class ComplexProperty {
	
	private Object property1;
	
	public void setProperty1(Object property1) {
		this.property1 = property1;
	}
	
	public Object getProperty1() {
		return property1;
	}
	
	@Mutable
	public void changeProperty() {
		
	}
	
	@Override
	@Mutable
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

}
