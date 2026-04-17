package com.sentinel.transaction.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentinel.common.dto.TransactionResponse;
import com.sentinel.transaction.dto.PaymentRequest;
import com.sentinel.transaction.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {
	
	@Autowired
    private MockMvc mockMvc;
	@MockBean
    private TransactionService transactionService;
	
	@Autowired
    private ObjectMapper objectMapper;
	
	@Test
    public void testProcessPayment_EndpointSuccess() throws Exception {
   
        PaymentRequest request = new PaymentRequest("C_001", new BigDecimal("100.00"), "USD");
        TransactionResponse mockResponse = TransactionResponse.builder()
                .status("APPROVED")
                .riskScore(5)
                .build();
        when(transactionService.processPayment(any(PaymentRequest.class))).thenReturn(mockResponse);
        mockMvc.perform(post("/api/v1/payments/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))) 
                .andExpect(status().isOk()) 
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.riskScore").value(5));
    }
	
	
	@Test
	public void testProcessPayment_BadRequest_MissingCustomerId() throws Exception {
	    PaymentRequest invalidRequest = new PaymentRequest(null, new BigDecimal("100.00"), "USD");


	    mockMvc.perform(post("/api/v1/payments/process")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(invalidRequest)))
	            .andExpect(status().isBadRequest()); // Expect HTTP 400
	            
	    // Verification: The service should NEVER be called if validation fails
	    verifyNoInteractions(transactionService);
	}

}
