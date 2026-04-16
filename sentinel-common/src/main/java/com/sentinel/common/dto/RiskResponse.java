package com.sentinel.common.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data

public class RiskResponse implements Serializable {
	
	
	private static final long serialVersionUID = 1L;

    private String status;      // e.g., "APPROVED", "REJECTED", "REVIEW"
    private Integer riskScore;  // 0 to 100
    private String reason;

}
