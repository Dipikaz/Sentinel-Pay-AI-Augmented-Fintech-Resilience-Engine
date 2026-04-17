package com.sentinel.risk;

import com.sentinel.risk.BaseContractTest;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;
import io.restassured.response.ResponseOptions;

import static org.springframework.cloud.contract.verifier.assertion.SpringCloudContractAssertions.assertThat;
import static org.springframework.cloud.contract.verifier.util.ContractVerifierUtil.*;
import static com.toomuchcoding.jsonassert.JsonAssertion.assertThatJson;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.*;

@SuppressWarnings("rawtypes")
public class ContractVerifierTest extends BaseContractTest {

	@Test
	public void validate_checkRisk() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"customerId\":\"C_TEST_001\",\"amount\":100.00}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/api/v1/risk/check");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).matches("application/json.*");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['status']").isEqualTo("APPROVED");
			assertThatJson(parsedJson).field("['riskScore']").isEqualTo(10);
			assertThatJson(parsedJson).field("['reason']").isEqualTo("Low risk transaction");
	}

	@Test
	public void validate_shouldReturnApprovedForLowRisk() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"customerId\":\"C_TEST_001\",\"amount\":100.00}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/api/v1/risk/check");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).matches("application/json.*");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['status']").isEqualTo("APPROVED");
			assertThatJson(parsedJson).field("['riskScore']").isEqualTo(10);
			assertThatJson(parsedJson).field("['reason']").isEqualTo("Low risk transaction");
	}

}
