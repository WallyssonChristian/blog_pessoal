package com.generation.blogpessoal.configuration;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

// @Configuration define a Classe como fonte de definições de Beans.
@Configuration
public class SwaggerConfig {

	/* @Bean indica ao Spring que ele deve invocar o Método e gerenciar 
	 * o objeto retornado por ele, ou seja, agora este objeto pode ser 
	 * injetado em qualquer ponto da sua aplicação.
	 */ 
	@Bean
	// Cria um Objeto que gera a documentação no Swagger utilizando a especificação OpenAPI.
    OpenAPI springBlogPessoalOpenAPI() {
        return new OpenAPI()
        		// Informações sobre a API
            .info(new Info()
                .title("Projeto Blog Pessoal")
                .description("Projeto Blog Pessoal - Generation Brasil")
                .version("v0.0.1")
                // Informações de Licença
                .license(new License()
                    .name("Generation Brasil")
                    .url("https://brazil.generation.org/"))
                // Informações de Contato
                .contact(new Contact()
                    .name("Wallysson Araujo")
                    .url("https://www.linkedin.com/in/wallysson-araujo/")
                    .email("wallysson.christian@outlook.com")))
            // Informações referentes a documentações Externas
            .externalDocs(new ExternalDocumentation()
                .description("Github")
                .url("https://github.com/WallyssonChristian"));
    }


	@Bean
	// Permite personalizar o Swagger
	OpenApiCustomizer customerGlobalHeaderOpenApiCustomiser() {

		// Cria um Objeto que gera a documentação no Swagger utilizando a especificação OpenAPI.
		return openApi -> {
			openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations().forEach(operation -> {

				ApiResponses apiResponses = operation.getResponses();

				apiResponses.addApiResponse("200", createApiResponse("Sucesso!"));
				apiResponses.addApiResponse("201", createApiResponse("Objeto Persistido!"));
				apiResponses.addApiResponse("204", createApiResponse("Objeto Excluído!"));
				apiResponses.addApiResponse("400", createApiResponse("Erro na Requisição!"));
				apiResponses.addApiResponse("401", createApiResponse("Acesso Não Autorizado!"));
				apiResponses.addApiResponse("403", createApiResponse("Acesso Proibido!"));
				apiResponses.addApiResponse("404", createApiResponse("Objeto Não Encontrado!"));
				apiResponses.addApiResponse("500", createApiResponse("Erro na Aplicação!"));

			}));
		};
	}

	// Adiciona uma mensagem na resposta da API
	private ApiResponse createApiResponse(String message) {

		return new ApiResponse().description(message);

	}
}