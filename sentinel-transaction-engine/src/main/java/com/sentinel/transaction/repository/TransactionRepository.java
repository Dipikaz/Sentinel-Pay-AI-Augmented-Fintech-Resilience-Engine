package com.sentinel.transaction.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sentinel.transaction.entity.*;


@Repository

public interface TransactionRepository extends JpaRepository<TransactionRecord, Long>{
	
	Optional<TransactionRecord> findByTransactionId(String transactionId);
	
}




