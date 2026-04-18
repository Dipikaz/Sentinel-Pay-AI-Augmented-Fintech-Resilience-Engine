package com.sentinel.common.dto;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor



public class RiskResultMessage {
	private String transactionId;
    private String status; 
    private Integer riskScore;
    private String reason;
	

}
