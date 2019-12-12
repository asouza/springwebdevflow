package io.github.asouza;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.springframework.scheduling.annotation.Async;

public class FormFlowAsyncExecutor {

	@Async
	public <ReturnType> CompletableFuture<ReturnType> executeAsync(Supplier<CompletableFuture<ReturnType>> supplier) {
		return supplier.get();
	}
}