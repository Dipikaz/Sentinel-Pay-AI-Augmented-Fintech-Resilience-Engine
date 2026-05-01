package com.sentinel.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // 👈 This automatically creates getTransactionId(), getStatus(), etc.
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskResponse {
	
	@JsonProperty("transactionId")
    private String transactionId;
    private String status;
    private int riskScore;
    private String reason;
}