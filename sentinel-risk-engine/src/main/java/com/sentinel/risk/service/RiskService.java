package com.sentinel.risk.service;

import com.sentinel.common.dto.PaymentRequest;
import com.sentinel.common.dto.RiskResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor // Automatically injects KafkaTemplate
public class RiskService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final List<String> blacklistedCustomers = List.of("C_FRAUD_99", "C_SCAM_01");

    /**
     * ✅ NEW METHOD: This is what the Contract Base Class calls.
     * It bridges the internal risk logic with the Kafka output.
     */
    public void evaluateAndSendResult(String transactionId, String customerId, BigDecimal amount) {
        log.info("Contract Trigger: Evaluating risk for transaction {}", transactionId);

        // 1. Reuse your existing logic
        PaymentRequest dummyRequest = new PaymentRequest();
        dummyRequest.setCustomerId(customerId);
        dummyRequest.setAmount(amount);

        RiskResponse response = evaluateRisk(dummyRequest);

        // 2. Prepare the payload for Kafka (Matching your Groovy Contract body)
        // You can use a Map or a specific DTO here.
        java.util.Map<String, Object> message = new java.util.HashMap<>();
        message.put("transactionId", transactionId);
        message.put("status", response.getStatus());
        message.put("riskScore", response.getRiskScore());
        message.put("reason", response.getReason());

        // 3. Send to Kafka (Topic name MUST match Groovy's sentTo)
        log.info("Sending Risk Result to Kafka: {}", message);
        kafkaTemplate.send("risk-results", message);
    }

    public RiskResponse evaluateRisk(PaymentRequest request) {
        // --- Your existing logic ---
        if (blacklistedCustomers.contains(request.getCustomerId())) {
            return RiskResponse.builder()
                    .riskScore(100)
                    .status("REJECTED")
                    .reason("Customer is on the global blacklist")
                    .build();
        }

        int score = calculateScore(request.getAmount());
        String status = (score >= 70) ? "REJECTED" : "APPROVED";

        return RiskResponse.builder()
                .riskScore(score)
                .status(status)
                .reason(status.equals("REJECTED") ? "High amount" : "Low risk transaction")
                .build();
    }

    private int calculateScore(BigDecimal amount) {
        if (amount == null) return 0;
        if (amount.compareTo(new BigDecimal("10000")) > 0) return 90;
        if (amount.compareTo(new BigDecimal("5000")) > 0) return 50;
        return 10;
    }
}