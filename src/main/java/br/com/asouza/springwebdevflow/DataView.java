package br.com.asouza.springwebdevflow;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.cglib.proxy.Enhancer;

public class DataView<T> {

	private T proxy;
	private Trace trace;
	private T original;
	private Map<String, Object> complexValues = new HashMap<>();

	@SuppressWarnings("unchecked")
	private DataView(T instance) {
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

	public <ReturnType, MapperType> DataView<T> add(String key, Function<T, ReturnType> supplier,
			Function<ReturnType, MapperType> mapper) {
		MapperType finalObject = supplier.andThen(mapper).apply(original);
		complexValues.put(key, finalObject);
		return this;
	}

	public <ReturnType, MapperType> DataView<T> addCollection(String key, Function<T, Collection<ReturnType>> supplier,
			Function<ReturnType, MapperType> mapper) {

		Collection<ReturnType> collection = supplier.apply(original);
		// using list to keep the original order
		List<MapperType> resultList = collection.stream().map(mapper).collect(Collectors.toList());
		complexValues.put(key, resultList);

		return this;
	}

	public DataView<T> add(Function<T, Object> supplier) {
		supplier.apply(proxy);
		return this;
	}

	public DataView<T> add(String key, Function<T, Object> supplier) {
		complexValues.put(key, supplier.apply(original));
		return this;
	}

	public DataView<T> addDate(String key, Function<T, TemporalAccessor> supplier, String pattern) {

		Function<TemporalAccessor, Object> formatter = (temporal) -> {
			return DateTimeFormatter.ofPattern(pattern).format(temporal);
		};

		return this.addFormatted(key, supplier, formatter);
	}

	public DataView<T> addPercentual(String key, Function<T, Number> supplier) {

		Function<Number, Object> formatter = (number) -> {
			DecimalFormat df = new DecimalFormat("##.##%");
			return df.format(number.doubleValue() / 100);
		};

		return this.addFormatted(key, supplier, formatter);
	}

	public <ReturnType> DataView<T> addFormatted(String key, Function<T, ReturnType> supplier,
			Function<ReturnType, Object> formatter) {
		ReturnType result = supplier.apply(original);
		complexValues.put(key, formatter.apply(result));
		return this;
	}

	public Map<String, Object> build() {
		Map<String, Object> json = trace.getJson();

		complexValues.entrySet().forEach(entry -> {
			json.put(entry.getKey(), entry.getValue());
		});
		return json;
	}

}