package com.sentinel.transaction.contract;

import com.sentinel.common.dto.RiskResponse;
import com.sentinel.transaction.entity.TransactionRecord;
import com.sentinel.transaction.repository.TransactionRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Direct Integration Test using Native KafkaTemplate.
 * This bypasses the complexity of Stub Runner bindings and tests the 
 * Consumer logic directly against the Embedded Kafka broker.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@EmbeddedKafka(
    partitions = 1, 
    topics = {"risk-results"},
    bootstrapServersProperty = "spring.kafka.bootstrap-servers"
)
public class ContractIntegrationTest {

    @Autowired
    private TransactionRepository repository;

    // Use RiskResponse explicitly to match the Listener's corrected argument
    @Autowired
    private KafkaTemplate<String, RiskResponse> kafkaTemplate;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    public void verifyRiskResultContract() {
        // 1. The Transaction ID used in your Groovy Contract
        String testTxId = "test-id-123";
        
        // 2. Prepare DB state: Set record to PENDING
        TransactionRecord record = new TransactionRecord();
        record.setTransactionId(testTxId);
        record.setStatus("PENDING");
        record.setAmount(new BigDecimal("100.00"));
        repository.saveAndFlush(record);

        // 3. Prepare the Payload using the synchronized RiskResponse DTO
        RiskResponse payload = RiskResponse.builder()
                .transactionId(testTxId)
                .status("APPROVED")
                .riskScore(10)
                .reason("Contract verification successful")
                .build();

        // 4. Give the Kafka consumer group a few seconds to join and rebalance
        // This is a common requirement on local machines (MacBook Air)
        try { 
            System.out.println("⏳ Waiting for Kafka Consumer to stabilize...");
            Thread.sleep(5000); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 5. Fire the message directly to the topic
        System.out.println("🚀 SENDING synchronized RiskResponse to Kafka topic: risk-results");
        kafkaTemplate.send("risk-results", payload);

        // 6. Verification loop using Awaitility
        Awaitility.await()
            .atMost(15, TimeUnit.SECONDS)
            .pollInterval(1, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                TransactionRecord updatedRecord = repository.findByTransactionId(testTxId)
                    .orElseThrow(() -> new AssertionError("Record not found for ID: " + testTxId));
                
                System.out.println("🔍 CHECKING DB - ID: " + testTxId + " | Status: " + updatedRecord.getStatus());
                assertEquals("APPROVED", updatedRecord.getStatus(), "The database record was not updated to APPROVED by the listener.");
            });
            
        System.out.println("✅ Test Passed: Status updated successfully!");
    }
}