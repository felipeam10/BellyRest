package br.com.felipe.rest.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import br.com.felipe.rest.core.BaseTest;

public class BellyTest extends BaseTest{
	
	private String token;

	@Before
	public void login() {
		Map<String, String> login = new HashMap<>();
		login.put("email", "felipeam10@hotmail.com");
		login.put("senha", "123456");
		
		token = given()
			.body(login)
		.when()
			.post("/signin")
		.then()
			.statusCode(200)
			.extract().path("token");
	}

	@Test
	public void naoDeveAcessarAPISemToken() {
		
		given()
		.when()
			.get("/contas")
		.then()
			.statusCode(401)
		;
	}
	
	@Test
	public void deveIncluirContaComSucesso() {
		// o login eh feito pela classe @Before public void login()
		given()
			.header("Authorization", "JWT " + token) // JWT é mais antigo, mais atual eh "bearer "
			.body("{\"nome\": \"Conta de Teste 3\"}")
		.when()
			.post("/contas")
		.then()
			.statusCode(201)
		;
		
	}
	
	@Test
	public void deveAlterarContaComSucesso() {
		// o login eh feito pela classe @Before public void login()
		given()
			.header("Authorization", "JWT " + token)
			.body("{\"nome\": \"Conta de Teste 2.1 alterada\"}")
		.when()
			.put("/contas/423740")
		.then()
			.log().all()
			.statusCode(200)
			.body("nome", is("Conta de Teste 2.1 alterada"))
		;
		
	}
}


