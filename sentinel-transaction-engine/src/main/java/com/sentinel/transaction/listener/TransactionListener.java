package com.sentinel.transaction.listener;

import com.sentinel.common.dto.RiskResponse;
import com.sentinel.transaction.entity.TransactionRecord;
import com.sentinel.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionListener {

    private final TransactionRepository repository;

    // ✅ FIXED: Using RiskResponse to match what the Producer/Test is sending
    @Transactional
    @KafkaListener(topics = "risk-results", groupId = "transaction-engine-group")
    public void consumeResult(RiskResponse response) {
        log.info("🚀 KAFKA RECEIVED - ID: {} Status: {}", response.getTransactionId(), response.getStatus());
        
        repository.findByTransactionId(response.getTransactionId())
            .ifPresentOrElse(record -> {
                record.setStatus(response.getStatus());
                repository.save(record);
                log.info("✅ DB UPDATED for Transaction ID: {}", response.getTransactionId());
            }, () -> {
                log.error("❌ DB UPDATE FAILED: Transaction ID {} not found", response.getTransactionId());
            });
    }
}