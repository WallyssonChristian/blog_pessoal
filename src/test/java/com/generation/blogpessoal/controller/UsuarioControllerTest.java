package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;


/* @SpringBootTest indica que a Classe UsuarioControllerTest é uma Classe Spring Boot Testing.
 * A Opção environment indica que caso a porta principal (8080 para uso local) esteja ocupada, 
 * o Spring irá atribuir uma outra porta automaticamente. 
 * */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
	// @TestInstance indica que o Ciclo de vida da Classe de Teste será por Classe.
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {

	// Objeto para enviar as requisições para a nossa aplicação.
	@Autowired
	private TestRestTemplate testRestTemplate;

	// Objeto para persistir os objetos no Banco de dados de testes com a senha criptografada.
	@Autowired
	private UsuarioService usuarioService;

	// Objeto para limpar o Banco de dados de testes.
	@Autowired
	private UsuarioRepository usuarioRepository;

	/* @BeforeAll é usada para marcar um método em uma classe de teste JUnit 5 
	 * que deve ser executado antes de todos os métodos de teste naquela classe. 
	 * Este método é executado apenas uma vez, antes da execução de qualquer método de teste na classe.
	 * */
	@BeforeAll
	void start(){

		// Apaga todos os dados da tabela
		usuarioRepository.deleteAll();

		// Cria o usuário root@root.com para testar os Métodos protegidos por autenticação.
		usuarioService.cadastrarUsuario(new Usuario(0L, 
			"Root", "root@root.com", "rootroot", "-"));

	}

	/* @Test é usada para marcar um método como um método de teste. 
	 * Quando você executa testes em JUnit, o framework procura por métodos 
	 * anotados com @Test e os executa como parte do processo de teste.
	 * */
	@Test
		// @DisplayName configura uma mensagem que será exibida ao invés do nome do Método.
	@DisplayName("Cadastrar Um Usuário")
	public void deveCriarUmUsuario() {

		// Cria objeto de teste
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L, 
			"Paulo Antunes", "paulo_antunes@email.com.br", "13465278", "-"));

		/* O Método exchange executa uma requisição de qualquer Método HTTP e retorna uma instância da Classe ResponseEntity.
		 * A URI: Endereço do endpoint (/usuarios/cadastrar);
		 * O Método HTTP: Neste exemplo o Método POST;
		 * O Objeto HttpEntity: Neste exemplo o objeto requisicao, que contém o objeto da Classe Usuario;
		 * O conteúdo esperado no Corpo da Resposta (Response Body): Neste exemplo será do tipo Usuario (Usuario.class).
		 */
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
			.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);

		/* Através do Método AssertEquals(), checaremos se a resposta da requisição (Response), 
		 * é a resposta esperada (CREATED 🡪 201).
		 * Para obter o status da resposta vamos utilizar o Método getStatusCode() 
		 * */
		assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());
	
	}

	@Test
	@DisplayName("Não deve permitir duplicação do Usuário")
	public void naoDeveDuplicarUsuario() {

		// Persistindo objeto no BdD
		usuarioService.cadastrarUsuario(new Usuario(0L, 
			"Maria da Silva", "maria_silva@email.com.br", "13465278", "-"));

		// Criando objeto duplicado
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L, 
			"Maria da Silva", "maria_silva@email.com.br", "13465278", "-"));

		// Enviando requisição de cadastrar usuario para testar persistencia de dados duplicados
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
			.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);

		// Resposta esperada para a requisição
		assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());
	}

	@Test
	@DisplayName("Atualizar um Usuário")
	public void deveAtualizarUmUsuario() {

		// Guardando resultado da persistencia de dados no BdD, e salvando dados do usuario
		Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(new Usuario(0L, 
			"Juliana Andrews", "juliana_andrews@email.com.br", "juliana123", "-"));

		// Criando objeto atualizado utilizando os dados criados anteriormente
		Usuario usuarioUpdate = new Usuario(usuarioCadastrado.get().getId(), 
			"Juliana Andrews Ramos", "juliana_ramos@email.com.br", "juliana123" , "-");
		
		// Encapsula o objeto atualizado para ser utilizado em uma requisição HTTP
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuarioUpdate);

		// Enviando requisição com autenticação
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
			.withBasicAuth("root@root.com", "rootroot")
			.exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, Usuario.class);

		// Verifica se a resposta
		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
		
	}

	@Test
	@DisplayName("Listar todos os Usuários")
	public void deveMostrarTodosUsuarios() {

		// Persistindo Objeto 1
		usuarioService.cadastrarUsuario(new Usuario(0L, 
			"Sabrina Sanches", "sabrina_sanches@email.com.br", "sabrina123", "-"));
		
		// Persistindo Objeto 2
		usuarioService.cadastrarUsuario(new Usuario(0L, 
			"Ricardo Marques", "ricardo_marques@email.com.br", "ricardo123", "-"));

		// Enviando requisição
		ResponseEntity<String> resposta = testRestTemplate
		.withBasicAuth("root@root.com", "rootroot")
			.exchange("/usuarios/all", HttpMethod.GET, null, String.class);

		// Analisando resposta da requisição
		assertEquals(HttpStatus.OK, resposta.getStatusCode());

	}

}