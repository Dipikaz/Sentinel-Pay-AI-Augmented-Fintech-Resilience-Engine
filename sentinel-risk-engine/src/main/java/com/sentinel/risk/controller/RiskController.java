package com.sentinel.risk.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.sentinel.risk.service.RiskService;
import com.sentinel.common.dto.PaymentRequest;
import com.sentinel.common.dto.RiskRequest;
import com.sentinel.common.dto.RiskResponse;



@RestController
@RequestMapping("/api/v1/risk")
public class RiskController {
	
	@Autowired
    private RiskService riskService;

    @PostMapping("/evaluate")
    public RiskResponse evaluate(@RequestBody PaymentRequest request) {
        return riskService.evaluateRisk(request);
    	
    }

}
