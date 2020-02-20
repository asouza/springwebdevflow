package io.github.asouza.autoconfiguration;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.support.Repositories;

import io.github.asouza.FormFlow;

@Configuration
public class SpringWebDevAutoconfiguration implements ApplicationContextAware{
	
	private ApplicationContext applicationContext;

	@Bean
	public <T> FormFlow<T> create(ApplicationContext context){
		return new FormFlow<>(context, new Repositories(applicationContext));
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
		
	}
}
