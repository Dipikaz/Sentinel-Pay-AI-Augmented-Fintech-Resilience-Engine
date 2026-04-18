package com.sentinel.transaction.consumer;
import com.sentinel.common.dto.RiskResultMessage;
import com.sentinel.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor

public class RiskResultConsumer {
	
	private final TransactionRepository transactionRepository;
	@KafkaListener(topics = "risk-results", groupId = "transaction-engine-group")
    public void consumeResult(RiskResultMessage result) {
        log.info("🔄 Feedback Received for ID: {} | Status: {}", 
                 result.getTransactionId(), result.getStatus());
        
        transactionRepository.findByTransactionId(result.getTransactionId())
        .ifPresentOrElse(record -> {
            record.setStatus(result.getStatus());
            record.setRiskScore(result.getRiskScore());
            record.setReason(result.getReason());
            
            transactionRepository.save(record);
            log.info("✅ Database updated for ID: {}. New Status: {}", 
                     result.getTransactionId(), record.getStatus());
        }, () -> {
            log.warn("⚠️ Received result for unknown Transaction ID: {}", 
                     result.getTransactionId());
        });
}

}
