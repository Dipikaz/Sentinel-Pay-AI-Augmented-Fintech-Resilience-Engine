package com.sentinel.transaction.integration;

import com.sentinel.common.dto.PaymentRequest;
import com.sentinel.common.dto.RiskResultMessage;
import com.sentinel.transaction.repository.TransactionRepository;

import lombok.extern.slf4j.Slf4j;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(
        partitions = 1,
        topics = {"transaction-events", "risk-results"},
        bootstrapServersProperty = "spring.kafka.bootstrap-servers"
)
@DirtiesContext
@Slf4j
class EndToEndTransactionTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 🧪 MOCK RISK ENGINE
     */
    @KafkaListener(topics = "transaction-events", groupId = "test-group")
    public void mockRiskEngine(PaymentRequest request) {
        log.info("🧪 Mock Risk Engine: received {}", request.getTransactionId());

        RiskResultMessage result = RiskResultMessage.builder()
                .transactionId(request.getTransactionId())
                .status("APPROVED")
                .riskScore(5)
                .reason("Test Approved")
                .build();

        kafkaTemplate.send("risk-results", request.getTransactionId(), result);
    }

    @Test
    void testFullTransactionCircle() {

        // ✅ Generate deterministic ID
        String transactionId = UUID.randomUUID().toString();
        String testCustId = "C_AUTO_TEST_" + System.currentTimeMillis();

        PaymentRequest request = PaymentRequest.builder()
                .transactionId(transactionId)
                .customerId(testCustId)
                .amount(new BigDecimal("150.00"))
                .currency("USD")
                .build();

        log.info("🚀 Starting test for TxnId: {}", transactionId);

        // ✅ Call API
        restTemplate.postForEntity(
                "/api/v1/payments/process",
                request,
                String.class
        );

        // ✅ Await async processing
        Awaitility.await()
                .atMost(20, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {

                    var record = repository.findByTransactionId(transactionId);

                    assertTrue(record.isPresent(), "Record should exist in DB");

                    String status = record.get().getStatus();

                    log.info("🔎 Current Status: {}", status);

                    // ✅ FINAL ASSERTION (missing earlier)
                    assertEquals("APPROVED", status);
                });

        log.info("✅ End-to-End test passed for {}", transactionId);
    }
}