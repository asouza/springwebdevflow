package io.github.asouza.support;

public class ComplexPropertyDTO {

	private Object property1;

	public ComplexPropertyDTO(ComplexProperty complexProperty) {
		this.property1 = complexProperty.getProperty1();
	}
	
	public Object getProperty1() {
		return property1;
	}
}
