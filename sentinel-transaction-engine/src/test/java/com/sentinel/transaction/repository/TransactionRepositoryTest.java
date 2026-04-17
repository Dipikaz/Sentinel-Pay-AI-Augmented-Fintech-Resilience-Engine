package com.sentinel.transaction.repository;

import com.sentinel.transaction.entity.TransactionRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // Forces H2 use
public class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    public void testSaveAndFindTransaction() {
        // Validation: If this fails here, the repo is null
        assertNotNull(transactionRepository, "Repository should not be null");

        TransactionRecord record = new TransactionRecord();
        record.setCustomerId("C_PERSIST_001");
        record.setAmount(new BigDecimal("250.00"));
        record.setStatus("APPROVED");
        record.setCreatedAt(LocalDateTime.now());

        TransactionRecord savedRecord = transactionRepository.save(record);
        assertNotNull(savedRecord.getId());
    }
}