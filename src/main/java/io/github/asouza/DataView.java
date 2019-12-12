package io.github.asouza;

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

public final class DataView<T> {

	private T proxy;
	private Trace trace;
	private T original;
	private Map<String, Object> complexValues = new HashMap<>();

	@SuppressWarnings("unchecked")
	private DataView(T instance) {
		Optional<Constructor<?>> constructorWithoutArgs = Stream.of(instance.getClass().getConstructors())
				.filter(constructor -> constructor.getParameterCount() == 0).findFirst();

		Assert.isTrue(constructorWithoutArgs.isPresent(),
				"In order to transform an object to a DataView you need to provide an empty constructor. "
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
	 * @param function to call the method which follows the Java Beans pattern.
	 * @return
	 */
	public final DataView<T> add(Function<T, ? extends Object> function) {
		function.apply(proxy);
		return this;
	}

	/**
	 * 
	 * @param key      custom key to be generated
	 * @param function function to call the method which follows the Java Beans
	 *                 pattern.
	 * @return
	 */
	public final DataView<T> add(String key, Function<T, ? extends  Object> function) {
		complexValues.put(key, function.apply(original));
		return this;
	}

	/**
	 * 
	 * @param key      custom key to be generated
	 * @param function function function to call the method which follows the Java
	 *                 Beans pattern.
	 * @param pattern  pattern supported for {@link DateTimeFormatter}
	 * @return
	 */
	public final DataView<T> addDate(String key, Function<T, TemporalAccessor> function, String pattern) {

		Function<TemporalAccessor, Object> formatter = (temporal) -> {
			return DateTimeFormatter.ofPattern(pattern).format(temporal);
		};

		return this.addFormatted(key, function, formatter);
	}

	/**
	 * 
	 * @param key      custom key to be generated
	 * @param function to call the method which follows the Java Beans pattern.
	 * @return number formatted using ##.##%
	 */
	public final DataView<T> addPercentual(String key, Function<T, Number> function) {

		Function<Number, Object> formatter = (number) -> {
			DecimalFormat df = new DecimalFormat("##.##%");
			return df.format(number.doubleValue() / 100);
		};

		return this.addFormatted(key, function, formatter);
	}

	/**
	 * 
	 * @param <ReturnType> custom return type to be passed to formatter parameter
	 * @param key          custom key to be generated
	 * @param function     to call the method which follows the Java Beans pattern.
	 * @param formatter    function to apply some transformation on the property
	 * @return
	 */
	public final <ReturnType> DataView<T> addFormatted(String key, Function<T, ReturnType> function,
			Function<ReturnType, Object> formatter) {
		ReturnType result = function.apply(original);
		complexValues.put(key, formatter.apply(result));
		return this;
	}

	/**
	 * 
	 * @return map build with collected values and keys
	 */
	public final Map<String, Object> build() {
		Map<String, Object> json = trace.getJson();

		complexValues.entrySet().forEach(entry -> {
			json.put(entry.getKey(), entry.getValue());
		});
		return json;
	}

	/**
	 * 
	 * @param <ReturnType> type to be used as input for the mapper method
	 * @param <MapperType> type which will be returned by the mapper function
	 * @param key          custom key to be generated
	 * @param function     to call the method which follows the Java Beans pattern.
	 * @param mapper       function to map the original property value to the new
	 *                     Object
	 * @return
	 */
	public final <ReturnType, MapperType> DataView<T> add(String key, Function<T, ReturnType> supplier,
			Function<ReturnType, MapperType> mapper) {
		MapperType finalObject = supplier.andThen(mapper).apply(original);
		complexValues.put(key, finalObject);
		return this;
	}

	/**	 
	 * 
	 * @param <ReturnType>    type to be used as input for the mapper method
	 * @param key             custom key to be generated
	 * @param function        to call the method which follows the Java Beans
	 *                        pattern.
	 * @param propertyMappers mappers to each property of the returned object. Do not map here to collection or a application objects  
	 * @return
	 */
	@SafeVarargs
	public final <ReturnType> DataView<T> add2(String key, Function<T, ReturnType> supplier,
			Function<ReturnType, Object>... propertyMappers) {
		ReturnType complexProperty = supplier.apply(original);

		DataView<ReturnType> viewOfComplexProperty = dataViewOfAComplextProperty(complexProperty, propertyMappers);

		complexValues.put(key, viewOfComplexProperty.build());

		return this;
	}

	@SafeVarargs
	private final <ReturnType> DataView<ReturnType> dataViewOfAComplextProperty(ReturnType complexProperty,
			Function<ReturnType, Object>... propertyMappers) {
		DataView<ReturnType> viewOfComplexProperty = DataView.of(complexProperty);
		for (Function<ReturnType, Object> mapper : propertyMappers) {
			
			Function<ReturnType, String> onlyAllowedSimpleJavaTypesMapper = object -> {
				Object mappedProperty = mapper.apply(object);
				String packageName = mappedProperty.getClass().getPackage().getName();
				
				boolean isJavaSimpleType = packageName.startsWith("java") && !(mappedProperty instanceof Collection);
				Assert.isTrue(isJavaSimpleType,"Your mapping is too complex already. Create your custom DTO and use the add method");
				
				return mapper.apply(object).toString();
			};
			
			viewOfComplexProperty.add(onlyAllowedSimpleJavaTypesMapper);
		}
		return viewOfComplexProperty;
	}

	/**
	 * 
	 * @param <ReturnType> type to be used as input for the mapper method
	 * @param <MapperType> type which will be returned by the mapper function
	 * @param key          custom key to be generated
	 * @param function     to call the method which follows the Java Beans pattern.
	 * @param mapper       function to map the original property value to new
	 *                     collection of objects
	 * @return
	 */
	public final <ReturnType, MapperType> DataView<T> addCollection(String key,
			Function<T, Collection<ReturnType>> function, Function<ReturnType, MapperType> mapper) {

		Collection<ReturnType> collection = function.apply(original);
		// using list to keep the original order
		List<MapperType> resultList = collection.stream().map(mapper).collect(Collectors.toList());
		complexValues.put(key, resultList);

		return this;
	}

	/**
	 * 
	 * @param <ReturnType>    type of the getterMethod for the complex object
	 * @param key             custom key to be generated
	 * @param function        to call the method which follows the Java Beans
	 *                        pattern.
	 * @param propertyMappers mappers to each property of the returned object. Do not map here to collection or a application objects
	 * @return
	 */
	@SafeVarargs
	public final <ReturnType> DataView<T> addCollection2(String key, Function<T, Collection<ReturnType>> function,
			Function<ReturnType, Object>... propertyMappers) {

		Collection<ReturnType> collection = function.apply(original);

		List<Map<String, Object>> collectionObjectProperties = collection.stream().map(object -> {
			return dataViewOfAComplextProperty(object, propertyMappers).build();
		}).collect(Collectors.toList());

		complexValues.put(key, collectionObjectProperties);
		return this;
	}

}