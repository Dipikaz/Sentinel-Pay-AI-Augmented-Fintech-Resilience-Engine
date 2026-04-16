package com.sentinel.transaction.entity;


import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "transactions")
@Data

public class TransactionRecord {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerId;
    private BigDecimal amount;
    private String status;
    private Integer riskScore;
    private LocalDateTime createdAt;

}
