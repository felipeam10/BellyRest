package br.com.felipe.rest.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import br.com.felipe.rest.core.BaseTest;
import br.com.felipe.rest.utils.DateUtils;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BellyTest extends BaseTest{
	
	private static String CONTA_NAME = "Conta " + System.nanoTime();
	private static Integer CONTA_ID; 
	private static Integer MOV_ID;
	
	private Movimentacao getMovimentacaoValida() {
		Movimentacao mov = new Movimentacao();
		mov.setConta_id(CONTA_ID);
//		mov.setUsuario_id(usuario_id);
		mov.setDescricao("Pagamento de agua");
		mov.setEnvolvido("Felipe");
		mov.setTipo("REC");
		mov.setData_transacao(DateUtils.getDataDiferencaDias(-1));
		mov.setData_pagamento(DateUtils.getDataDiferencaDias(5));
		mov.setValor(100f);
		mov.setStatus(true);
		return mov;
	}

	@BeforeClass // é executado apenas uma vez para a classe inteira. Se fosse soh o @before executaria um token para cada teste
	public static void login() { // no before class o metodo precisa ser static
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
	}

	@Test
	public void t02_deveIncluirContaComSucesso() {
		// o login eh feito pela classe @Before public void login()
		CONTA_ID = given()
			.body("{\"nome\": \""+CONTA_NAME+"\"}")
		.when()
			.post("/contas")
		.then()
			.statusCode(201)
			.extract().path("id")
		;
	}
	
	@Test
	public void t03_deveAlterarContaComSucesso() {
		// o login eh feito pela classe @Before public void login()
		given()
			.body("{\"nome\": \""+CONTA_NAME+" alterada\"}")
			.pathParam("id", CONTA_ID)
		.when()
			.put("/contas/{id}")
		.then()
			.statusCode(200)
			.body("nome", is(CONTA_NAME + " alterada"))
		;
	}
	
	@Test
	public void t04_naoDeveContaComMesmoNome() {
		given()
			.body("{\"nome\": \""+CONTA_NAME+" alterada\"}")
		.when()
			.post("/contas")
		.then()
			.log().all()
			.statusCode(400)
			.body("error", is("Já existe uma conta com esse nome!"))
		;
	}

	@Test
	public void t05_deveInserirMovimentacaoComSucesso() {
		Movimentacao mov = getMovimentacaoValida();
		
		MOV_ID = given()
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(201)
			.extract().path("id")
		;
	}

	@Test
	public void t06_deveValidarCamposObrigatoriosMovimentacao() {
		given()
			.body("{}")
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("$", hasSize(8))
			.body("msg", hasItems(  
					"Data da Movimentação é obrigatório",
					"Data da Movimentação é obrigatório",
					"Data do pagamento é obrigatório",
					"Descrição é obrigatório",
					"Interessado é obrigatório",
					"Valor é obrigatório",
					"Valor deve ser um número",
					"Conta é obrigatório",
					"Situação é obrigatório"))
		;
	}
	
	@Test
	public void t07_naoDeveInserirMovimentacaoComDataFutura() {
		Movimentacao mov = getMovimentacaoValida();
		mov.setData_transacao(DateUtils.getDataDiferencaDias(5));
		
		given()
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("$", hasSize(1))
			.body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual"))
		;
	}
	
	@Test
	public void t08_naoDeveRemoverContaComMovimentacao() {
		given()
			.pathParam("id", CONTA_ID)
		.when()
			.delete("/contas/{id}")
		.then()
			.statusCode(500)
			.body("constraint", is("transacoes_conta_id_foreign"))
		;
	}
	
	@Test
	public void t09_deveCalcularSaldoContas() {
		given()
		.when()
			.get("/saldo")
		.then()
			.statusCode(200)
			.body("find{it.conta_id == "+CONTA_ID+"}.saldo", is("100.00"))
//			.body("conta_id", hasItems("423740")) nao funcionou
//			.body("conta", hasItem("Conta de Teste 2.1 alterada")) funcionou
//			.body("saldo", hasItem("400.00")) funcionou
		;
	}
	
	@Test
	public void t10_deveRemoverMovimentacao() {
		given()
			.pathParam("id", MOV_ID)
		.when()
			.delete("/transacoes/{id}")
		.then()
			.statusCode(204)
		;
	}
	
	@Test
	public void t11_naoDeveAcessarAPISemToken() {
		FilterableRequestSpecification req = (FilterableRequestSpecification) RestAssured.requestSpecification;
		req.removeHeader("Authorization");
		
		given()
		.when()
			.get("/contas")
		.then()
			.statusCode(401)
		;
	}
}


