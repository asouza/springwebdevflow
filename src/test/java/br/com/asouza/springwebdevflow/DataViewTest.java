package br.com.asouza.springwebdevflow;

import java.time.LocalDate;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import br.com.asouza.springwebdevflow.support.ComplexProperty;
import br.com.asouza.springwebdevflow.support.ComplexPropertyDTO;
import br.com.asouza.springwebdevflow.support.Team;

public class DataViewTest {

	@Test
	public void objectMustHaveEmptyConstructor() {
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> DataView.of(new WithoutEmptyConstructorObject("")));
	}
	
	@Test
	public void methodShouldStartWithGetOrIs() {
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> DataView.of(new Team("")).add(Team :: someMethod));		
	}
	
	@Test
	public void shouldExportSimpleProperties() {
		Team team = new Team("bla");
		team.setProperty2("ble");
		Map<String, Object> result = DataView.of(team).add(Team::getName).add(Team :: getProperty2).build();
		Assertions.assertEquals("bla", result.get("name"));
		Assertions.assertEquals("ble", result.get("property2"));
	}
	
	@Test
	public void shouldExportPropertiesWithCustomkeys() {
		Team team = new Team("bla");
		team.setProperty2("ble");
		team.setProperty3(LocalDate.of(2019, 1, 1));
		Map<String, Object> result = DataView.of(team)
				.add(Team::getName)
				.add(Team :: getProperty2)
				.addDate("date",t -> (TemporalAccessor)t.getProperty3(), "dd/MM/yyyy")
				.build();
		Assertions.assertEquals("bla", result.get("name"));
		Assertions.assertEquals("ble", result.get("property2"));
		Assertions.assertEquals("01/01/2019", result.get("date"));
	}
	
	@Test
	public void shouldExportMappedProperties() {
		Team team = new Team("bla");
		team.setProperty2("ble");
		ComplexProperty complexProperty = new ComplexProperty();
		complexProperty.setProperty1("opa");
		team.setProperty3(complexProperty);
		Map<String, Object> result = DataView.of(team)
				.add(Team::getName)
				.add(Team :: getProperty2)
				.add("custom",  t -> (ComplexProperty)t.getProperty3(), ComplexPropertyDTO :: new)
				.build();
		Assertions.assertEquals("bla", result.get("name"));
		Assertions.assertEquals("ble", result.get("property2"));
		Assertions.assertEquals("opa", ((ComplexPropertyDTO)result.get("custom")).getProperty1());
	}
	
	@Test
	public void shouldExportMappedCollectionProperties() {
		Team team = new Team("bla");
		team.setProperty2("ble");
		ComplexProperty complexProperty = new ComplexProperty();
		complexProperty.setProperty1("opa");
		team.setProperty4(Arrays.asList(complexProperty));
		
		Map<String, Object> result = DataView.of(team)
				.add(Team::getName)
				.add(Team :: getProperty2)
				.addCollection("custom", Team :: getProperty4,ComplexPropertyDTO :: new)
				.build();
		Assertions.assertEquals("bla", result.get("name"));
		Assertions.assertEquals("ble", result.get("property2"));
		Assertions.assertEquals("opa", ((List<ComplexPropertyDTO>)result.get("custom")).get(0).getProperty1());
	}
	
	@Test
	public void shouldExportMappedCollectionProperties2() {
		Team team = new Team("bla");
		team.setProperty2("ble");
		ComplexProperty complexProperty = new ComplexProperty();
		complexProperty.setProperty1("opa");
		team.setProperty4(Arrays.asList(complexProperty));
		
		Map<String, Object> result = DataView.of(team)
				.add(Team::getName)
				.add(Team :: getProperty2)
				.addCollection2("custom", Team :: getProperty4, ComplexProperty :: getProperty1)
				.build();
		
		Assertions.assertEquals("bla", result.get("name"));
		Assertions.assertEquals("ble", result.get("property2"));
		Assertions.assertEquals("opa", ((List<Map>)result.get("custom")).get(0).get("property1"));
	}	
}
