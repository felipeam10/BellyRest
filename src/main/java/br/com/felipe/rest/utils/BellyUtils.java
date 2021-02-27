package br.com.felipe.rest.utils;

import io.restassured.RestAssured;

public class BellyUtils {

	public static Integer getIdContaPeloNome(String nome) {
		return RestAssured.get("/contas?nome=" + nome).then().extract().path("id[0]");
	}
	
	public static Integer getIdMovPelaDescricao(String desc) {
		return RestAssured.get("/transacoes?descricao=" + desc).then().extract().path("id[0]");
	}
}
