package com.sentinel.transaction.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sentinel.transaction.entity.*;


@Repository
public interface TransactionRepository extends JpaRepository<TransactionRecord, Long> {
    // This MUST exist for the listener to find the record by "test-id-123"
    Optional<TransactionRecord> findByTransactionId(String transactionId);
}


