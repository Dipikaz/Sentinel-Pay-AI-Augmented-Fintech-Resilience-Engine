package com.sentinel.transaction.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.*;
import lombok.Data;


@Data

public class PaymentRequest {
	
	@NotBlank(message = "Customer ID is required")
    private String customerId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    private String currency; // Transaction specific

}
