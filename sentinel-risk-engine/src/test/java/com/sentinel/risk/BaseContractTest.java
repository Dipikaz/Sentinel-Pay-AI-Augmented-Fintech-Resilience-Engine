package com.sentinel.risk;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.sentinel.risk.controller.RiskController;


@SpringBootTest
public  abstract class BaseContractTest {
	
	@Autowired
    private RiskController riskController;
	
	@BeforeEach
    public void setup() {
		RestAssuredMockMvc.standaloneSetup(riskController);
	}

}
