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
	
	private Movimentacao getMovimentacaoValida() {
		Movimentacao mov = new Movimentacao();
		mov.setConta_id(423740);
//		mov.setUsuario_id(usuario_id);
		mov.setDescricao("Pagamento de agua");
		mov.setEnvolvido("Felipe");
		mov.setTipo("REC");
		mov.setData_transacao("01/01/2020");
		mov.setData_pagamento("01/01/2021");
		mov.setValor(100f);
		mov.setStatus(true);
		return mov;
	}

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
			.statusCode(200)
			.body("nome", is("Conta de Teste 2.1 alterada"))
		;
	}
	
	@Test
	public void naoDeveContaComMesmoNome() {
		given()
			.header("Authorization", "JWT " + token)
			.body("{\"nome\": \"Conta de Teste 2.1 alterada\"}")
		.when()
			.post("/contas")
		.then()
			.log().all()
			.statusCode(400)
			.body("error", is("Já existe uma conta com esse nome!"))
		;
	}

	@Test
	public void deveInserirMovimentacaoComSucesso() {
		Movimentacao mov = getMovimentacaoValida();
		
		given()
			.header("Authorization", "JWT " + token)
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(201)
		;
	}

	@Test
	public void deveValidarCamposObrigatoriosMovimentacao() {
		given()
			.header("Authorization", "JWT " + token)
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
	public void naoDeveInserirMovimentacaoComDataFutura() {
		Movimentacao mov = getMovimentacaoValida();
		mov.setData_transacao("28/02/2021");
		
		given()
			.header("Authorization", "JWT " + token)
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
	public void naoDeveRemoverContaComMovimentacao() {
		given()
			.header("Authorization", "JWT " + token)
		.when()
			.delete("/contas/427782")
		.then()
			.statusCode(500)
			.body("constraint", is("transacoes_conta_id_foreign"))
		;
	}
	
	@Test
	public void deveCalcularSaldoContas() {
		given()
			.header("Authorization", "JWT " + token)
		.when()
			.get("/saldo")
		.then()
			.statusCode(200)
			.body("find{it.conta_id == 423740}.saldo", is("400.00"))
//			.body("conta_id", hasItems("423740")) nao funcionou
//			.body("conta", hasItem("Conta de Teste 2.1 alterada")) funcionou
//			.body("saldo", hasItem("400.00")) funcionou
		;
	}
	
	@Test
	public void deveRemoverMovimentacao() {
		given()
			.header("Authorization", "JWT " + token)
		.when()
			.delete("/transacoes/391832")
		.then()
			.statusCode(204)
		;
	}
}


