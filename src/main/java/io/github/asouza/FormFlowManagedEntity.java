package io.github.asouza;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class FormFlowManagedEntity<T> {

	private T entity;
	private FormFlowAsyncExecutor flowAsyncExecutor;

	public FormFlowManagedEntity(T entity, FormFlowAsyncExecutor flowAsyncExecutor) {
		this.entity = entity;
		this.flowAsyncExecutor = flowAsyncExecutor;		
	}
	
	public <ReturnType> CompletableFuture<ReturnType> andThenAsync(Function<T, CompletableFuture<ReturnType>> consumer) {
		return flowAsyncExecutor.executeAsync(() -> {
			return consumer.apply(entity);
		});
	}

	public <ReturnType> FormFlowManagedEntity<ReturnType> andThen(Function<T,ReturnType> function) {
		ReturnType objectToNewFlow = function.apply(entity);
		return new FormFlowManagedEntity<ReturnType>(objectToNewFlow, flowAsyncExecutor);
	}
	
	public T getEntity() {
		return entity;
	}
	

}
