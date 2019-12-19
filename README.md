# Why?

After having participated in several web projects with Spring, I feel like I have a template that I kind of always follow
for building all endpoints. This is even stronger if we are in that early phase of the project. I will leave
Some examples here:

## Save a new object and return some information from that object

My current flow is:

* I create a class that represents the form that will be filled
* In this class, which I like to call Form, I usually declare a method called `` `toModel```.
* I implement toModel by returning a new instance of the domain object as a function of the form itself.
* It may be that my form has ids from other objects. I do the simplest think I can, I get the repository as argument
from toModel and load the referring object inside.
* ToModel may need an extra application object, such as a logged in user. I get as argument too
* I get injected Spring Data JPA repository for domain object in controller
* Also gets the other Spring managed dependencies that toModel needs
* I may or may not receive the Repository regarding toModel depictions (this sucks and generates distraction in the class)
* I call save

## I need to generate a JSON as a result of a loaded object

* I create a class that represents that dataset. I usually put a DTO suffix and that's it
* I receive the domain object that contains the information I need as constructor parameter 
I invoke the public methods of the domain object and populate the attributes of the DTO
* Occasionally I need to use formatters of date, money etc.
* I often need to generate collections of that DTO as a result of the domain object collection. There I go
   ```collection.stream (). map (...)```
  
# This is a template that I find efficient, but I got tired of repeating

After doing this many times, I started to find myself kind of like a robot. I could program these endpoints
even when I was distracted. I even talked with a friend of mine, Mauricio Aniche, to think about a possibility to create a
programming language for web development. I mean, this language would have the reserved word `` `Controller```,
`` `Form``` and various facilities for our daily lives. 

As I have no competence at this time to create a language, I decided to create a mini lib in order to facilitate
some of these tasks. 

# Some usage examples

## JSON generation

```
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

```

## Saving an object using a form that follows my development template. Just to remember, this form has the ```toModel``` method.

```
public class FormFlowTest {

	@Test
	public void shouldSaveFormSync() {
		TeamFormTest form = new TeamFormTest();
		form.setName("test");
		formFlow.save(form).andThen(team -> {
			//do what you need
		});
	}
	
	@Test
	public void shouldSaveFormSyncWithExtraArgs() {
		TeamFormTest form = new TeamFormTest();
		form.setName("test");
		formFlow.save(form,new CustomObject()).andThen(team -> {
			//do what you need
		});
	}	
	
	@Test
	public void shouldSaveFormAsync() {
		TeamFormTest form = new TeamFormTest();
		form.setName("test");
		formFlow.save(form).andThenAsync(team -> {
			//do what you need
		});
	}	
	
}

```

## Using defensive programming

```

public class ImmutableReferenceTest {

	@Test
	public void shouldNotAllowSetterForImmutableReference() {
		ComplexProperty complex = new ComplexProperty();
		complex.setProperty1("bla");

		ComplexProperty immutable = ImmutableReference.of(complex);
		Assertions.assertThrows(IllegalAccessException.class, () -> immutable.setProperty1("change"));
	}
	
	@Test
	public void shouldNotAllowMutableAnnnotatedMethodForImmutableReference() {
		ComplexProperty complex = new ComplexProperty();
		complex.changeProperty();
		
		ComplexProperty immutable = ImmutableReference.of(complex);
		Assertions.assertThrows(IllegalAccessException.class, () -> immutable.changeProperty());
	}
	
	@Test
	public void shouldBuildCollectionWithImmutableObjects() {
		ComplexProperty complex = new ComplexProperty();
		
		Collection<ComplexProperty> list = ImmutableReference.of(Arrays.asList(complex));
		Assertions.assertThrows(IllegalAccessException.class, () -> list.iterator().next().changeProperty());
	}
}

```

```

public class ExecutePreconditionsTest {

	@Test
	void shouldAddBeanValidationAnnotationAtConstructor() throws Exception {
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> Preconditions.newInstance(UnprotectedEntity.class, ""));

	}

	@Test
	void shouldValidateAnnotatedConstructorArgs() throws Exception {
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> Preconditions.newInstance(ProtectedEntity.class, ""));

	}

	@Test
	void shouldCreateValidAnnotatedConstructorArgs() throws Exception {
		ProtectedEntity instance = Preconditions.newInstance(ProtectedEntity.class, "bla bla");
		Assertions.assertEquals("bla bla", instance.getName());

	}

	@Test
	void shouldValidateAnnotatedMethodArgs() throws Exception {
		ProtectedEntity instance = Preconditions.newInstance(ProtectedEntity.class, "bla bla");
		Assertions.assertThrows(IllegalArgumentException.class, () -> instance.logic(0));

	}

	@Test
	void shouldExecuteMethodWithValidArgs() throws Exception {
		ProtectedEntity instance = Preconditions.newInstance(ProtectedEntity.class, "bla bla");
		instance.logic(2);

		Assertions.assertEquals(2, instance.getValue());
	}

	@Test
	void shouldNotCreateProtectedInstanceWithoutEmptyConstructors() throws Exception {
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> Preconditions.newInstance(ProtectedEntityWithoutEmptyConstructor.class, "bla bla"));
	}		
}

public class UnprotectedEntity {
	public UnprotectedEntity(String name) {

	}
}

public class ProtectedEntity {

	private @NotBlank String name;
	private @Min(1) Integer value;

	public ProtectedEntity() {
		// TODO Auto-generated constructor stub
	}

	public ProtectedEntity(@NotBlank String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void logic(@Min(1) Integer value) {
		this.value = value;

	}

	public Integer getValue() {
		return value;
	}
}

```

## Find more examples in the test code

You can find more examples in the test code. I also tried to leave javadoc everywhere, all aiming
to facilitate the use of lib. We'll see :). 

If you enjoyed and would like to collaborate, talk to me on

## If you want to test for now

* Clone the repository
* Install the project as local dependency on your computer (mvn install from springwebdev folder)
* Add dependency to your project
* If you are going to use DataView, look at the examples :)
* If you are going to use FormFlow, get it injected into your `` `@Autowired FormFlow <SomeName> formFlow`` controller and look at the examples.

```
		<dependency>
			<groupId>br.com.asouza</groupId>
			<artifactId>springwebdevflow</artifactId>
			<version>0.0.1-SNAPSHOT</version>			
		</dependency>

```  
  

  

