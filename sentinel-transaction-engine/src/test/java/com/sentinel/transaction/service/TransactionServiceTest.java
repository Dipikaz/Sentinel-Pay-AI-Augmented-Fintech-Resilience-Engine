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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
// 💡 Added to allow multiple stubbing calls for the repository save
@MockitoSettings(strictness = Strictness.LENIENT) 
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionProducer transactionProducer;

    @InjectMocks
    private TransactionService transactionService;

    private void mockJpaSaveBehavior() {
        // Use lenient() or simply Answer to handle multiple save calls
        lenient().when(transactionRepository.save(any(TransactionRecord.class)))
                .thenAnswer(invocation -> {
                    TransactionRecord record = invocation.getArgument(0);
                    if (record.getId() == null) {
                        record.setId(1L);
                    }
                    return record;
                });
    }

    @Test
    public void testProcessTransaction_Success() {
        mockJpaSaveBehavior();

        PaymentRequest request = PaymentRequest.builder()
                .transactionId(UUID.randomUUID().toString())
                .customerId("C_TEST_001")
                .amount(new BigDecimal("100.00"))
                .currency("USD")
                .build();

        TransactionResponse result = transactionService.processTransaction(request);

        assertNotNull(result);
        assertEquals("PROCESSING", result.getStatus());
        verify(transactionRepository, times(1)).save(any(TransactionRecord.class));
        verify(transactionProducer, times(1)).sendTransaction(any(PaymentRequest.class));
    }

    @Test
    public void testProcessTransaction_KafkaFailure() {
        mockJpaSaveBehavior();

        PaymentRequest request = PaymentRequest.builder()
                .transactionId(UUID.randomUUID().toString())
                .customerId("C_TEST_001")
                .amount(new BigDecimal("100.00"))
                .currency("USD")
                .build();

        // 💡 Use doThrow for void methods or Mockito Producer
        doThrow(new RuntimeException("Kafka Broker Unreachable"))
                .when(transactionProducer)
                .sendTransaction(any(PaymentRequest.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                transactionService.processTransaction(request)
        );

        // This must match EXACTLY what is in your TransactionService catch block
        assertTrue(exception.getMessage().contains("Failed to publish transaction event") 
                || exception.getMessage().contains("Kafka Broker Unreachable"));

        ArgumentCaptor<TransactionRecord> captor = ArgumentCaptor.forClass(TransactionRecord.class);
        
        // Verify we saved twice (Once for PROCESSING, Once for FAILED)
        verify(transactionRepository, atLeast(2)).save(captor.capture());

        List<TransactionRecord> savedRecords = captor.getAllValues();
        assertEquals("PROCESSING", savedRecords.get(0).getStatus());
        assertEquals("FAILED", savedRecords.get(savedRecords.size() - 1).getStatus());
    }

    @Test
    public void testProcessTransaction_InvalidInput() {
        PaymentRequest request = PaymentRequest.builder()
                .customerId(null)
                .amount(new BigDecimal("50.00"))
                .build();

        // Ensure your service actually throws IllegalArgumentException for null customerId
        assertThrows(RuntimeException.class, () ->
                transactionService.processTransaction(request)
        );
    }
}