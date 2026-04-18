package com.sentinel.transaction.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.sentinel.common.dto.PaymentRequest;
@Service               
@Slf4j                 
@RequiredArgsConstructor
public class TransactionProducer {
	private final KafkaTemplate<String, PaymentRequest> kafkaTemplate;
    private static final String TOPIC = "transaction-events";
    
    public void sendTransaction(PaymentRequest request) {
        log.info("🚀 Sending transaction event to Kafka for Customer: {}", request.getCustomerId());
        
        kafkaTemplate.send("transaction-events", request.getTransactionId(), request);

}
}