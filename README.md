# Por que?

Depois de ter participado de diversos projetos web com Spring, eu sinto que tenho um template que eu meio que sempre sigo
para a construção de todos os endpoints. Isso é ainda mais forte se estamos naquela fase inicial do projeto. Vou deixar
alguns exemplos aqui:

## Salvar um novo objeto e retornar alguma informação daquele objeto

Meu fluxo é:

* Crio uma classe que representa o formulário que vai ser preenchido
* Nessa classe, que eu gosto de chamar de Form, eu geralmente declaro um método chamado ```toModel```. 
* Implemento o toModel retornando uma nova instância do objeto de dominio em função dos seus atributos.
* Pode ser que esse meu formulário tenha ids de outros objetos. Não conto conversa, recebo o repository como argumento
do toModel e carrego o objeto referente lá dentro.
* Pode ser que o toModel precise de um objeto extra da aplicação, como um usuário logado. Recebo como argumento também
* Recebo injetado o Spring Data JPA repository relativo ao objeto de domínio
* Posso ou não receber o Repository relativo as depências do toModel(isso é um saco e gera distração na classe)
* Chamo o save

## Preciso gerar um JSON em função de um objeto carregado

* Retornar dados pedidos através de algum endpoint

* Crio uma classe que representa aquele conjunto de dados. Geralmente meto um sufixo DTO e pronto mesmo
* Recebo no construtor do DTO o objeto de domínio que contém as informações que eu preciso
* Invoco os métodos de acesso e populo os atributos do DTO
* De vez em quando precisa usar formatadores de data, dinheiro etc
* Muitas vezes preciso gerar coleções daquele DTO em função da coleção do objeto de domínio. Lá vou eu fazer 
  ```colecao.stream().map(...)```
  
# Esse é um template que acho eficiente, mas fiquei cansado de repetir

Depois de fazer isso muitas vezes, eu comecei a me achar meio que um robô. Eu conseguia programar esses endpoints
pensando na morte da bezerra. Conversei até com Mauricio Aniche para pensarmos numa possibilidade de criar uma 
linguagem de programação para desenvolvimento web. Digo, nessa linguagem teria a palavra reservada ```Controller```,
```Form``` e várias facilidades para o nosso dia a dia. 

Como não tenho competência, nesse momento, para criar uma linguagem, resolvi criar uma mini lib com o objetivo de facilitar
algumas dessas tarefas. 

# Alguns exemplos de uso

## Geração de jsons

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

## Salvando um objeto em função de um form que segue meu template de desenvolvimento

```
public class FormFlowTest {

	@Test
	public void shouldSaveFormSync() {
		TeamFormTest form = new TeamFormTest();
		form.setName("test");
		formFlow.save(form).andThen(team -> {
			//faça o que precisar
		});
	}
	
	@Test
	public void shouldSaveFormAsync() {
		TeamFormTest form = new TeamFormTest();
		form.setName("test");
		formFlow.save(form).andThenAsync(team -> {
			//faça o que precisar
		});
	}	
	
}

```

##Encontre mais exemplos no código de teste

Você pode encontrar mais exemplos no código de teste. Eu também tentei deixar javadoc em todos lugares, tudo com o objetivo
de facilitar o uso da lib. Veremos :). 

Caso tenha gostado e queira colaborar, fala comigo no <a href="https://twitter.com/alberto_souza">twitter</a>.

##Ainda falta deployar em algum repo maven

Foi mal, preciso fazer isso e não gosto hehe.  
  

  

