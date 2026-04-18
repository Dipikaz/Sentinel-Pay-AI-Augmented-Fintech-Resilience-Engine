package com.sentinel.transaction.service;

import com.sentinel.common.dto.PaymentRequest;
import com.sentinel.common.dto.TransactionResponse;
import com.sentinel.transaction.entity.TransactionRecord;
import com.sentinel.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionProducer transactionProducer;

    @Transactional
    public TransactionResponse processTransaction(PaymentRequest request) {
        log.info("Processing transaction for customer: {}", request.getCustomerId());

        // 1. Validation (Fixes the '400 vs 200' Test Failure)
        if (request.getCustomerId() == null || request.getAmount() == null) {
            log.error("Validation failed: Missing CustomerId or Amount");
            throw new IllegalArgumentException("CustomerId and Amount are mandatory fields");
        }

        // 2. Ensure we have a Transaction ID
        if (request.getTransactionId() == null) {
            request.setTransactionId(UUID.randomUUID().toString());
        }

        // 3. Persist Initial Record (Status: PROCESSING)
        TransactionRecord record = new TransactionRecord();
        record.setTransactionId(request.getTransactionId());
        record.setCustomerId(request.getCustomerId());
        record.setAmount(request.getAmount());
        record.setCurrency(request.getCurrency());
        record.setStatus("PROCESSING");
        record.setCreatedAt(LocalDateTime.now());

        TransactionRecord savedRecord = transactionRepository.save(record);

        // 4. Publish to Kafka
        try {
            transactionProducer.sendTransaction(request);
            log.info("Transaction event published to Kafka: {}", request.getTransactionId());
        } catch (Exception e) {
            log.error("Failed to publish to Kafka. Updating status to FAILED for ID: {}", request.getTransactionId());
            
            // Resilience: Update DB to FAILED if Kafka is down
            savedRecord.setStatus("FAILED");
            transactionRepository.save(savedRecord);
            
            throw new RuntimeException("Failed to publish transaction event: " + e.getMessage());
        }

        // 5. Build and Return Response
        return TransactionResponse.builder()
                .id(savedRecord.getId())
                .transactionId(savedRecord.getTransactionId())
                .customerId(savedRecord.getCustomerId())
                .amount(savedRecord.getAmount())
                .status(savedRecord.getStatus())
                .createdAt(savedRecord.getCreatedAt())
                .message("Transaction processing started. Risk evaluation is in progress.")
                .build();
    }
}