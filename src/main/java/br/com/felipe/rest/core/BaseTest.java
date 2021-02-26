package br.com.felipe.rest.core;

import org.hamcrest.Matchers;
import org.junit.BeforeClass;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;

public class BaseTest implements Constantes {

	@BeforeClass
	public static void setup() {
		RestAssured.baseURI = APP_BASE_URL;
		RestAssured.port = APP_PORT;
		RestAssured.basePath = APP_BASE_PATH;
		
		RequestSpecBuilder recBuilder = new RequestSpecBuilder();
		recBuilder.setContentType(APP_CONTENT_TYPE);
		RestAssured.requestSpecification = recBuilder.build();
		
		ResponseSpecBuilder respBuilder = new ResponseSpecBuilder();
		respBuilder.expectResponseTime(Matchers.lessThan(MAX_TIMEOUT));
		RestAssured.responseSpecification = respBuilder.build();
		
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
	}
}
