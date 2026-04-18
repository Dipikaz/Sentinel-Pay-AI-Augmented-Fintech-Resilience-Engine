package com.sentinel.transaction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentinel.common.dto.PaymentRequest;
import com.sentinel.common.dto.TransactionResponse;
import com.sentinel.transaction.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

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
        // ✅ Use Builder to avoid constructor order issues
        PaymentRequest request = PaymentRequest.builder()
                .transactionId(UUID.randomUUID().toString())
                .customerId("C_001")
                .amount(new BigDecimal("100.00"))
                .currency("USD")
                .build();

        TransactionResponse mockResponse = TransactionResponse.builder()
                .transactionId(request.getTransactionId())
                .status("PROCESSING") // Kafka flow returns PROCESSING now
                .message("Received")
                .build();

        // ✅ Match the method name 'processTransaction'
        when(transactionService.processTransaction(any(PaymentRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/payments/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))) 
                .andExpect(status().isOk()) 
                .andExpect(jsonPath("$.status").value("PROCESSING"));
    }
	
    @Test
    public void testProcessPayment_BadRequest_MissingCustomerId() throws Exception {
        // ✅ Use Builder and leave customerId null for validation test
        PaymentRequest invalidRequest = PaymentRequest.builder()
                .amount(new BigDecimal("100.00"))
                .currency("USD")
                .build();

        mockMvc.perform(post("/api/v1/payments/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest()); 
	            
        verifyNoInteractions(transactionService);
    }
}