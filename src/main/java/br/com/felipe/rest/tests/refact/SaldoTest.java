package br.com.felipe.rest.tests.refact;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import br.com.felipe.rest.core.BaseTest;
import br.com.felipe.rest.utils.BellyUtils;

public class SaldoTest extends BaseTest {

	@Test
	public void deveCalcularSaldoContas() {
		Integer CONTA_ID = BellyUtils.getIdContaPeloNome("Conta para saldo");
		
		given()
		.when()
			.get("/saldo")
		.then()
			.statusCode(200)
			.body("find{it.conta_id == "+CONTA_ID+"}.saldo", is("534.00"))
		;
	}
}
