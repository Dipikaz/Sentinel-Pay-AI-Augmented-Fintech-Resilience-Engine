package com.sentinel.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class TransactionResponse {
	
	private Long id;
    private String customerId;
    private BigDecimal amount;
    private String status;
    private Integer riskScore;
    private String reason;
    private LocalDateTime createdAt;

}
