package br.com.felipe.rest.tests.refact;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import br.com.felipe.rest.core.BaseTest;
import io.restassured.RestAssured;

public class ContasTest extends BaseTest {

	public Integer getIdContaPeloNome(String nome) {
		return RestAssured.get("/contas?nome=" + nome).then().extract().path("id[0]");
	}
	
	@BeforeClass 
	public static void login() {
		Map<String, String> login = new HashMap<>();
		login.put("email", "felipeam10@hotmail.com");
		login.put("senha", "123456");
		
		String token = given()
			.body(login)
		.when()
			.post("/signin")
		.then()
			.statusCode(200)
			.extract().path("token");
		
		RestAssured.requestSpecification.header("Authorization", "JWT " + token); // JWT é mais antigo, mais atual eh "bearer "
		
		RestAssured.get("/reset").then().statusCode(200);
	}
	
	@Test
	public void deveIncluirContaComSucesso() {
		given()
			.body("{\"nome\": \"Conta inserida\"}")
		.when()
			.post("/contas")
		.then()
			.statusCode(201)
			.extract().path("id")
		;
	}
	
	@Test
	public void deveAlterarContaComSucesso() {
		Integer CONTA_ID = getIdContaPeloNome("Conta para alterar");
		
		given()
			.body("{\"nome\": \"Conta alterada\"}")
			.pathParam("id", CONTA_ID)
		.when()
			.put("/contas/{id}")
		.then()
			.statusCode(200)
			.body("nome", is("Conta alterada"))
		;
	}
	
	@Test
	public void naoDeveContaComMesmoNome() {
		given()
			.body("{\"nome\": \"Conta mesmo nome\"}")
		.when()
			.post("/contas")
		.then()
			.log().all()
			.statusCode(400)
			.body("error", is("Já existe uma conta com esse nome!"))
		;
	}
}
