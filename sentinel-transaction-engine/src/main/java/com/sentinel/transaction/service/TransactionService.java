package com.sentinel.transaction.service;

import com.sentinel.common.dto.RiskRequest;
import com.sentinel.common.dto.RiskResponse;
import com.sentinel.transaction.client.RiskClient;
import com.sentinel.transaction.dto.PaymentRequest;
import com.sentinel.transaction.dto.PaymentResponse;
import com.sentinel.transaction.entity.TransactionRecord;
import com.sentinel.transaction.mapper.TransactionMapper;
import com.sentinel.transaction.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j // Provides a 'log' variable for senior-level debugging
@Service
public class TransactionService {

    @Autowired
    private RiskClient riskClient;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionMapper mapper;

    /**
     * The main orchestrator for processing payments.
     * @Transactional ensures that if the DB save fails, the logic is consistent.
     */
    @Transactional
    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        log.info("Starting payment processing for customer: {}", paymentRequest.getCustomerId());

        // 1. Transform: External DTO -> Internal Common DTO
        RiskRequest riskRequest = mapper.toRiskRequest(paymentRequest);

        // 2. Communicate: Call Risk Engine via OpenFeign
        // This is a synchronous blocking call
        log.debug("Calling Risk Engine for evaluation...");
        RiskResponse riskResponse = riskClient.checkRisk(riskRequest);
        log.info("Risk Engine returned status: {}", riskResponse.getStatus());

        // 3. Persist: Map all data into an Entity and save to H2 Database
        TransactionRecord record = new TransactionRecord();
        record.setCustomerId(paymentRequest.getCustomerId());
        record.setAmount(paymentRequest.getAmount());
        record.setStatus(riskResponse.getStatus());
        record.setRiskScore(riskResponse.getRiskScore());
        record.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(record);
        log.debug("Transaction saved to database with ID: {}", record.getId());

        // 4. Transform & Return: Internal Response -> External API Response
        return mapper.toPaymentResponse(riskResponse);
    }
}