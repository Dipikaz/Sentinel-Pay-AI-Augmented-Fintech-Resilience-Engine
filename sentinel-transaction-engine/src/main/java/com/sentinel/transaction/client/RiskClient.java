package com.sentinel.transaction.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.sentinel.common.dto.RiskRequest;
import com.sentinel.common.dto.RiskResponse;
import com.sentinel.transaction.config.FeignConfig;

@FeignClient(name = "risk-engine", url = "${risk-service.url}", fallback = RiskClientFallback.class)
public interface RiskClient {
    @PostMapping("/api/v1/risk/check")
    RiskResponse checkRisk(@RequestBody RiskRequest request);
}

