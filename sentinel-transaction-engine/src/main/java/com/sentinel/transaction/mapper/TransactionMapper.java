package com.sentinel.transaction.mapper;

import org.springframework.stereotype.Component;
import com.sentinel.transaction.dto.PaymentRequest;
import com.sentinel.transaction.dto.PaymentResponse; // MUST BE THIS ONE
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

    // THE FIX: Ensure this returns PaymentResponse, not a String or Builder
    public PaymentResponse toPaymentResponse(RiskResponse riskRes) {
        return PaymentResponse.builder()
                .status(riskRes.getStatus())
                .message(riskRes.getReason())
                .transactionId(UUID.randomUUID().toString())
                .build();
    }
}