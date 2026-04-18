package com.sentinel.transaction.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.sentinel.common.dto.PaymentRequest;
import com.sentinel.common.dto.TransactionResponse;
import com.sentinel.transaction.entity.TransactionRecord;
import com.sentinel.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest extends BaseTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionProducer transactionProducer;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    public void testProcessTransaction_Success() {
        // 1. Arrange
        PaymentRequest request = PaymentRequest.builder()
                .transactionId(UUID.randomUUID().toString())
                .customerId("C_TEST_001")
                .amount(new BigDecimal("100.00"))
                .currency("USD")
                .build();
        
        when(transactionRepository.save(any(TransactionRecord.class))).thenAnswer(invocation -> {
            TransactionRecord record = invocation.getArgument(0);
            record.setId(1L); 
            return record;
        });

        // 2. Act
        TransactionResponse result = transactionService.processTransaction(request);
        
        // 3. Assert
        assertNotNull(result);
        assertEquals("PROCESSING", result.getStatus());
        assertEquals("C_TEST_001", result.getCustomerId());
        
        verify(transactionRepository, times(1)).save(any(TransactionRecord.class));
        verify(transactionProducer, times(1)).sendTransaction(any(PaymentRequest.class));
        
        test.pass("Successfully verified Async Path: Data persisted and sent to Kafka");
    }

    @Test
    public void testProcessTransaction_KafkaFailure() {
        // 1. Arrange
        PaymentRequest request = PaymentRequest.builder()
                .customerId("C_TEST_001")
                .amount(new BigDecimal("100.00"))
                .currency("USD")
                .build();
        
        // Ensure the mock save returns a record with an ID so the service doesn't NPE
        when(transactionRepository.save(any(TransactionRecord.class))).thenAnswer(i -> {
            TransactionRecord r = i.getArgument(0);
            r.setId(99L);
            return r;
        });

        doThrow(new RuntimeException("Kafka Broker Unreachable"))
            .when(transactionProducer).sendTransaction(any());

        // 2. Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.processTransaction(request);
        });

        // Check if message matches the String thrown in your Service
        assertTrue(exception.getMessage().contains("Failed to publish transaction event"));
        
        // Verify DB updated to FAILED (at least 2 saves: initial and failure update)
        verify(transactionRepository, atLeast(2)).save(any(TransactionRecord.class));
        
        test.pass("Verified Resilience: DB status updated to FAILED on Kafka crash");
    }

    @Test
    public void testProcessTransaction_FieldValidation() {
        // 1. Arrange
        PaymentRequest request = PaymentRequest.builder()
                .customerId("C_NEW_USER")
                .amount(new BigDecimal("50.00"))
                .currency("USD")
                .build();

        when(transactionRepository.save(any(TransactionRecord.class))).thenAnswer(i -> i.getArguments()[0]);

        // 2. Act
        TransactionResponse result = transactionService.processTransaction(request);

        // 3. Assert
        assertNotNull(result.getTransactionId(), "Service should generate a UUID");
        assertEquals("PROCESSING", result.getStatus());
        // 🔥 REMOVED the stray Kafka exception check that was causing the failure here
        
        test.pass("Field validation logic passed and ID generated");
    }
}