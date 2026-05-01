package com.sentinel.risk;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;

import com.sentinel.risk.controller.RiskController;
import com.sentinel.risk.service.RiskService;


@SpringBootTest
@AutoConfigureMessageVerifier

public  abstract class BaseContractTest {
	
	@Autowired
    private RiskService riskService;
	
	
	
	@BeforeEach
	public void setup() {
		
		
	}
	
	public void sendApprovedResult() {
		riskService.evaluateAndSendResult("test-id-123", "C_001", new java.math.BigDecimal("100.00"));
		
		
	}
}


	

