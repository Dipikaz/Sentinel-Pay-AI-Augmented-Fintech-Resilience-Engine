package com.sentinel.risk.service;

import org.springframework.stereotype.Service;
import com.sentinel.common.dto.RiskRequest;
import com.sentinel.common.dto.RiskResponse;
import java.math.BigDecimal;
import java.util.List;


@Service
public class RiskService {
	
	private final List<String> blacklistedCustomers = List.of("C_FRAUD_99", "C_SCAM_01");

    public RiskResponse evaluateRisk(RiskRequest request) {
        // 1. Blacklist Logic
        if (blacklistedCustomers.contains(request.getCustomerId())) {
            return RiskResponse.builder()
                    .riskScore(100)
                    .status("REJECTED")
                    .reason("Customer is on the global blacklist")
                    .build();
        }

        // 2. Threshold Logic
        int score = calculateScore(request.getAmount());
        
        // 3. Final Decision Logic
        String status = (score >= 70) ? "REJECTED" : "APPROVED";

        return RiskResponse.builder()
                .riskScore(score)
                .status(status)
                .reason(status.equals("REJECTED") ? "High amount" : "Safe transaction")
                .build();
    }

	private int calculateScore(BigDecimal amount) {
		if (amount == null) {
	        return 0;
	    }

	    // 2. Comparison Logic: 
	    // compareTo returns: 1 if greater, 0 if equal, -1 if less.
	    if (amount.compareTo(new BigDecimal("10000")) > 0) {
	        return 90; // High Risk
	    } else if (amount.compareTo(new BigDecimal("5000")) > 0) {
	        return 50; // Medium Risk
	    }
	    
	    return 10;
	}

}
