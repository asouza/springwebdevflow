package io.github.asouza.formflow;

public class FormFlowManagedEntity<T> {

	private T entity;

	public FormFlowManagedEntity(T entity) {
		this.entity = entity;
	}
	
	public T getEntity() {
		return entity;
	}
	

}
