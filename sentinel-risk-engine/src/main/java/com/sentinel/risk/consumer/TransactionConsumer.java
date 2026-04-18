package com.sentinel.risk.consumer;

import com.sentinel.common.dto.PaymentRequest;
import com.sentinel.common.dto.RiskResultMessage;
import com.sentinel.risk.producer.RiskResultProducer;
import com.sentinel.risk.service.RiskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionConsumer {

    private final RiskService riskService;
    private final RiskResultProducer riskResultProducer; 

    @KafkaListener(topics = "transaction-events", groupId = "risk-engine-group")
    public void consume(PaymentRequest request) {
        log.info("📥 Received Transaction for evaluation: {}", request.getTransactionId());
       
        // 1. Evaluate the risk using your service
        var evaluation = riskService.evaluateRisk(request);
        
        log.info("✅ Risk Evaluation Complete for ID {}: Status is {}", 
                 request.getTransactionId(), evaluation.getStatus());
        
        // 2. Map the evaluation results to RiskResultMessage
        RiskResultMessage result = RiskResultMessage.builder()
                .transactionId(request.getTransactionId())
                .status(evaluation.getStatus()) 
                .riskScore(evaluation.getRiskScore())
                .reason(evaluation.getReason())
                .build();

        // 3. 🚀 Fire the message back via the INJECTED instance (lower-case 'r')
        riskResultProducer.sendResult(result); 
        
        log.info("📤 Sent Risk Result back to Kafka for ID: {}", request.getTransactionId());
    }
}