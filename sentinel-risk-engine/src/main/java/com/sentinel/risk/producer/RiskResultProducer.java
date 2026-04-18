package com.sentinel.risk.producer;


import com.sentinel.common.dto.RiskResultMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j

public class RiskResultProducer {
	
	
	private final KafkaTemplate<String, Object> kafkaTemplate;
	
	public void sendResult(RiskResultMessage result) {
        log.info("📤 Publishing Risk Result for Transaction ID: {}", result.getTransactionId());
        
        try {
            // Topic name: risk-results
            // Key: transactionId (ensures order)
            // Value: the result DTO
            kafkaTemplate.send("risk-results", result.getTransactionId(), result);
        } catch (Exception e) {
            log.error("❌ Failed to send risk result to Kafka: {}", e.getMessage());
        }
	}
}