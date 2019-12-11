package br.com.asouza.springwebdevflow;

import java.lang.reflect.Constructor;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.util.Assert;

public class DataView<T> {

	private T proxy;
	private Trace trace;
	private T original;
	private Map<String, Object> complexValues = new HashMap<>();

	@SuppressWarnings("unchecked")
	private DataView(T instance) {
		Optional<Constructor<?>> constructorWithoutArgs = Stream.of(instance.getClass().getConstructors())
					.filter(constructor -> constructor.getParameterCount() == 0).findFirst();
		
		Assert.isTrue(constructorWithoutArgs.isPresent(),"In order to transform an object to a DataView you need to provide an empty constructor. "
				+ "Just use the Deprecated annotation and everything will be fine :)");
		
		
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(instance.getClass());
		this.trace = new Trace(instance);
		enhancer.setCallback(this.trace);
		this.proxy = (T) enhancer.create();
		this.original = instance;
	}
	
	public static <T> DataView<T> of(T instance) {
		return new DataView<T>(instance);
	}	

	/**
	 * 
	 * @param <ReturnType> type to be used as input for the mapper method
	 * @param <MapperType> type which will be returned by the mapper function
	 * @param key custom key to be generated
	 * @param function to call the method which follows the Java Beans pattern.
	 * @param mapper function to map the original property value to the new Object
	 * @return
	 */
	public <ReturnType, MapperType> DataView<T> add(String key, Function<T, ReturnType> supplier,
			Function<ReturnType, MapperType> mapper) {
		MapperType finalObject = supplier.andThen(mapper).apply(original);
		complexValues.put(key, finalObject);
		return this;
	}

	/**
	 * 
	 * @param <ReturnType> type to be used as input for the mapper method
	 * @param <MapperType> type which will be returned by the mapper function
	 * @param key custom key to be generated
	 * @param function to call the method which follows the Java Beans pattern.
	 * @param mapper function to map the original property value to new collection of objects
	 * @return
	 */
	public <ReturnType, MapperType> DataView<T> addCollection(String key, Function<T, Collection<ReturnType>> function,
			Function<ReturnType, MapperType> mapper) {

		Collection<ReturnType> collection = function.apply(original);
		// using list to keep the original order
		List<MapperType> resultList = collection.stream().map(mapper).collect(Collectors.toList());
		complexValues.put(key, resultList);

		return this;
	}

	/**
	 * 
	 * @param function to call the method which follows the Java Beans pattern. 
	 * @return
	 */
	public DataView<T> add(Function<T, Object> function) {
		function.apply(proxy);
		return this;
	}

	/**
	 * 
	 * @param key custom key to be generated
	 * @param function function to call the method which follows the Java Beans pattern.
	 * @return
	 */
	public DataView<T> add(String key, Function<T, Object> function) {
		complexValues.put(key, function.apply(original));
		return this;
	}

	/**
	 * 
	 * @param key custom key to be generated
	 * @param function function function to call the method which follows the Java Beans pattern.
	 * @param pattern pattern supported for {@link DateTimeFormatter}
	 * @return
	 */
	public DataView<T> addDate(String key, Function<T, TemporalAccessor> function, String pattern) {

		Function<TemporalAccessor, Object> formatter = (temporal) -> {
			return DateTimeFormatter.ofPattern(pattern).format(temporal);
		};

		return this.addFormatted(key, function, formatter);
	}

	/**
	 * 
	 * @param key custom key to be generated
	 * @param function to call the method which follows the Java Beans pattern.
	 * @return number formatted using ##.##%
	 */
	public DataView<T> addPercentual(String key, Function<T, Number> function) {

		Function<Number, Object> formatter = (number) -> {
			DecimalFormat df = new DecimalFormat("##.##%");
			return df.format(number.doubleValue() / 100);
		};

		return this.addFormatted(key, function, formatter);
	}

	/**
	 * 
	 * @param <ReturnType> custom return type to be passed to formatter parameter
	 * @param key custom key to be generated
	 * @param function to call the method which follows the Java Beans pattern.
	 * @param formatter function to apply some transformation on the property
	 * @return
	 */
	public <ReturnType> DataView<T> addFormatted(String key, Function<T, ReturnType> function,
			Function<ReturnType, Object> formatter) {
		ReturnType result = function.apply(original);
		complexValues.put(key, formatter.apply(result));
		return this;
	}
	
	/**
	 * 
	 * @return map build with collected values and keys
	 */
	public Map<String, Object> build() {
		Map<String, Object> json = trace.getJson();

		complexValues.entrySet().forEach(entry -> {
			json.put(entry.getKey(), entry.getValue());
		});
		return json;
	}

}