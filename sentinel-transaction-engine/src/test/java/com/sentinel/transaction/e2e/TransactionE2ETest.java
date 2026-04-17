package com.sentinel.transaction.e2e;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import org.springframework.boot.test.context.SpringBootTest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@SpringBootTest

public class TransactionE2ETest {
	
	
	@BeforeAll
    public static void setup() {
        // Point RestAssured to your running Transaction Engine
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }
	
	@Test
    public void testCompletePaymentLifecycle() {
        String requestBody = """
            {
                "customerId": "C_E2E_001",
                "amount": 500.00,
                "currency": "USD"
            }
            """;
        given()
        .contentType(ContentType.JSON)
        .body(requestBody)
    .when()
        .post("/api/v1/payments/process")
    .then()
        .statusCode(200)
        .body("status", equalTo("APPROVED"))
        .body("customerId", equalTo("C_E2E_001"))
        .body("id", notNullValue())
        .log().all();

}
}
