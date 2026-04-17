package com.sentinel.transaction.client;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import com.sentinel.common.dto.RiskRequest;
import com.sentinel.common.dto.RiskResponse;

@Component


public class RiskClientFallback implements RiskClient {
	
@Override
    public RiskResponse checkRisk(RiskRequest request) {
        return RiskResponse.builder()
                .status("PENDING")
                .riskScore(0)
                .reason("Risk Engine unreachable for " + request.getCustomerId() + ". Queued for review.")
                .build();
    }

}
