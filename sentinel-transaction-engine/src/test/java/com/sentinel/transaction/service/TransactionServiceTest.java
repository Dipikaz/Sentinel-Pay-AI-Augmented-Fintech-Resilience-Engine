package com.sentinel.transaction.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.sentinel.common.dto.RiskRequest;
import com.sentinel.common.dto.RiskResponse;
import com.sentinel.common.dto.TransactionResponse;
import com.sentinel.transaction.client.RiskClient;
import com.sentinel.transaction.dto.PaymentRequest;
import com.sentinel.transaction.entity.TransactionRecord;
import com.sentinel.transaction.mapper.TransactionMapper;
import com.sentinel.transaction.repository.TransactionRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest extends BaseTest {

    @Mock
    private RiskClient riskClient;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper mapper;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    public void testProcessPayment_Success() {
        // 1. Arrange
        test = extent.createTest("Transaction Success Path - Risk Approved");
        PaymentRequest request = new PaymentRequest("C_TEST_001", new BigDecimal("100.00"), "USD");
        
        RiskResponse mockRiskResponse = RiskResponse.builder()
                .status("APPROVED").riskScore(10).reason("Low Risk").build();

        TransactionResponse mockFinalResponse = TransactionResponse.builder()
                .status("APPROVED").riskScore(10).build();
        
        // Stubbing
        when(mapper.toRiskRequest(any())).thenReturn(new RiskRequest());
        when(riskClient.checkRisk(any())).thenReturn(mockRiskResponse);
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(mapper.toTransactionResponse(any())).thenReturn(mockFinalResponse);

        // 2. Act
        TransactionResponse result = transactionService.processPayment(request); 
        
        // 3. Assert & Verify
        assertNotNull(result);
        assertEquals("APPROVED", result.getStatus());
        verify(transactionRepository, times(1)).save(any());
        test.pass("Successfully verified Happy Path: APPROVED");
    }

    @Test
    public void testProcessPayment_RiskDenied() {
        // 1. Arrange
        test = extent.createTest("Transaction Negative Path - Risk Denied");
        PaymentRequest request = new PaymentRequest("C_FRAUD_99", new BigDecimal("9999.99"), "USD");
        
        RiskResponse mockRiskResponse = RiskResponse.builder()
                .status("DENIED").riskScore(95).reason("High Fraud Risk").build();

        TransactionResponse mockFinalResponse = TransactionResponse.builder()
                .status("DENIED").riskScore(95).build();
        
        // Stubbing
        when(mapper.toRiskRequest(any())).thenReturn(new RiskRequest());
        when(riskClient.checkRisk(any())).thenReturn(mockRiskResponse);
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(mapper.toTransactionResponse(any())).thenReturn(mockFinalResponse);

        // 2. Act
        TransactionResponse result = transactionService.processPayment(request);

        // 3. Assert & Verify
        assertNotNull(result);
        assertEquals("DENIED", result.getStatus());
        // CRITICAL: Verify that we still saved the record even though it failed
        verify(transactionRepository, times(1)).save(any());
        test.pass("Successfully verified Negative Path: DENIED status recorded correctly");
    }
    
    
    @Test
    public void testProcessPayment_RiskEngineDown() {
    	
    	
    	test = extent.createTest("Resilience Path - Risk Engine Connectivity Failure");
        PaymentRequest request = new PaymentRequest("C_TEST_001", new BigDecimal("100.00"), "USD");
    	
        when(mapper.toRiskRequest(any())).thenReturn(new RiskRequest());
        when(riskClient.checkRisk(any())).thenThrow(new RuntimeException("Risk Engine is Unreachable"));
        
        
  
        assertThrows(RuntimeException.class, () -> {
            transactionService.processPayment(request);
        }, "Expected processPayment to throw RuntimeException when Risk Engine is down");
        test.pass("Resilience Verified: System correctly identified downstream failure");
        verify(transactionRepository, times(0)).save(any());
        test.info("Verification: No data persisted during network failure, maintaining data integrity");
        
    }
    
    
    
    
    
    
    
    
    
    
}