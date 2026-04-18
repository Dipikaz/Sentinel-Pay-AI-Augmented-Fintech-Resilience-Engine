package com.sentinel.transaction.mapper;

import org.springframework.stereotype.Component;

import com.sentinel.common.dto.TransactionResponse; // Import the shared DTO
import com.sentinel.common.dto.PaymentRequest;
import com.sentinel.common.dto.RiskRequest;
import com.sentinel.common.dto.RiskResponse;
import java.util.UUID;

@Component
public class TransactionMapper {

    public RiskRequest toRiskRequest(PaymentRequest request) {
        return RiskRequest.builder()
                .customerId(request.getCustomerId())
                .amount(request.getAmount())
                .build();
    }

    // UPDATED: Now returns the shared TransactionResponse DTO
    public TransactionResponse toTransactionResponse(RiskResponse riskRes) {
        return TransactionResponse.builder()
                .status(riskRes.getStatus())
                // Ensure these field names match your TransactionResponse class
                .riskScore(riskRes.getRiskScore()) 
                .reason(riskRes.getReason())
                .build();
    }
}