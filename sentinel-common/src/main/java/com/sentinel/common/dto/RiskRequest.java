package com.sentinel.common.dto;

import java.math.BigDecimal;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor

public class RiskRequest implements Serializable{
	
	private static final long serialVersionUID = 1L;

    private String customerId;
    private BigDecimal amount;
    private String transactionId;
    private String sourceIp;
}
	


