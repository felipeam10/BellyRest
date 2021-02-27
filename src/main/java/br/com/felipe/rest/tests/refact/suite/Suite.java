package br.com.felipe.rest.tests.refact.suite;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

import br.com.felipe.rest.core.BaseTest;
import br.com.felipe.rest.tests.refact.AutenticacaoTest;
import br.com.felipe.rest.tests.refact.ContasTest;
import br.com.felipe.rest.tests.refact.MovimentacaoTest;
import br.com.felipe.rest.tests.refact.SaldoTest;
import io.restassured.RestAssured;

@RunWith(org.junit.runners.Suite.class)
@SuiteClasses({
	ContasTest.class,
	MovimentacaoTest.class,
	SaldoTest.class,
	AutenticacaoTest.class
})
public class Suite extends BaseTest {
	
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
}
