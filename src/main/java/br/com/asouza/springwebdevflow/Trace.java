package br.com.asouza.springwebdevflow;

import java.beans.Introspector;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

class Trace implements MethodInterceptor {

	private Map<String, Object> json = new HashMap<>();
	private Object instance;

	public Trace(Object instance) {
		this.instance = instance;
	}

	@Override
	public Object intercept(Object proxy, Method currentMethod, Object[] args, MethodProxy proxyMethod)
			throws Throwable {
		String methodName = currentMethod.getName();
		String propertyName = Introspector.decapitalize(methodName.substring(methodName.startsWith("is") ? 2 : 3));
		Object methodResult = currentMethod.invoke(instance, args);
		json.put(propertyName, methodResult);
		return methodResult;
	}

	public Map<String, Object> getJson() {
		return json;
	}
}