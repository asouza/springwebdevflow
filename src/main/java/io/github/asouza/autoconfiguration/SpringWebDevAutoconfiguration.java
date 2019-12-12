package io.github.asouza.autoconfiguration;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.support.Repositories;

import io.github.asouza.FormFlow;
import io.github.asouza.FormFlowAsyncExecutor;

@Configuration
public class SpringWebDevAutoconfiguration implements ApplicationContextAware{
	
	private ApplicationContext applicationContext;

	@Bean
	public <T> FormFlow<T> create(ApplicationContext context,FormFlowAsyncExecutor asyncExecutor){
		return new FormFlow<>(context, new Repositories(applicationContext), asyncExecutor);
	}
	
	@Bean
	public FormFlowAsyncExecutor createFormFlowAsyncExecutor() {
		return new FormFlowAsyncExecutor();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
		
	}
}
