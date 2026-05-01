package com.sentinel.risk;

import com.sentinel.risk.BaseContractTest;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import javax.inject.Inject;
import org.springframework.cloud.contract.verifier.messaging.internal.ContractVerifierObjectMapper;
import org.springframework.cloud.contract.verifier.messaging.internal.ContractVerifierMessage;
import org.springframework.cloud.contract.verifier.messaging.internal.ContractVerifierMessaging;

import static org.springframework.cloud.contract.verifier.assertion.SpringCloudContractAssertions.assertThat;
import static org.springframework.cloud.contract.verifier.util.ContractVerifierUtil.*;
import static com.toomuchcoding.jsonassert.JsonAssertion.assertThatJson;
import static org.springframework.cloud.contract.verifier.messaging.util.ContractVerifierMessagingUtil.headers;
import static org.springframework.cloud.contract.verifier.util.ContractVerifierUtil.fileToBytes;

@SuppressWarnings("rawtypes")
public class ContractVerifierTest extends BaseContractTest {
	@Inject ContractVerifierMessaging contractVerifierMessaging;
	@Inject ContractVerifierObjectMapper contractVerifierObjectMapper;

	@Test
	public void validate_shouldSendRikslResult() throws Exception {
		// when:
			sendApprovedResult();

		// then:
			ContractVerifierMessage response = contractVerifierMessaging.receive("risk-results",
					contract(this, "shouldSendRikslResult.yml"));
			assertThat(response).isNotNull();

		// and:
			assertThat(response.getHeader("contentType")).isNotNull();
			assertThat(response.getHeader("contentType").toString()).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(contractVerifierObjectMapper.writeValueAsString(response.getPayload()));
			assertThatJson(parsedJson).field("['transactionId']").isEqualTo("test-id-123");
			assertThatJson(parsedJson).field("['status']").isEqualTo("APPROVED");
			assertThatJson(parsedJson).field("['riskScore']").isEqualTo(10);
			assertThatJson(parsedJson).field("['reason']").isEqualTo("Low risk transaction");
	}

}
