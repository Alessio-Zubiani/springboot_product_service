package com.example.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.example.product.dto.ProductResponse;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.MySQLContainer;

import io.restassured.RestAssured;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductServiceApplicationTest {
	
	@ServiceConnection
	static MySQLContainer mySQLContainer = (MySQLContainer) new MySQLContainer("mysql:latest")
			.withDatabaseName("product_service")
			.withReuse(true);
	
	@LocalServerPort
	private Integer port;
	
	
	@BeforeEach
	void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = this.port;
	}
	
	static {
		mySQLContainer.start();
	}

	@Test
	@Sql(scripts = "classpath:sql/delete_product.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	void createProductTest() {
		String productRequest = """
			{
			    "name": "iPhone 15",
				"description": "iPhone 15 is a smartphone from Apple",
				"price": 1000
			}
			""";
		
		RestAssured.given()
			.contentType("application/json")
			.body(productRequest)
			.when()
			.post("/api/products")
			.then()
			.log().all()
			.statusCode(201)
			.body("id", Matchers.notNullValue())
			.body("name", Matchers.equalTo("iPhone 15"))
			.body("description", Matchers.equalTo("iPhone 15 is a smartphone from Apple"))
			.body("price", Matchers.equalTo(1000));
	}
	
	@Test
	@Sql(scripts = "classpath:sql/insert_product.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(scripts = "classpath:sql/delete_product.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	void getAllProductsTest() {
		List<ProductResponse> response = RestAssured.given()
			.when()
			.get("/api/products")
			.then()
			.log().all()
			.statusCode(200)
			.extract()
			.body().as(List.class);
		
		assertThat(response).hasSize(2);
	}

}
