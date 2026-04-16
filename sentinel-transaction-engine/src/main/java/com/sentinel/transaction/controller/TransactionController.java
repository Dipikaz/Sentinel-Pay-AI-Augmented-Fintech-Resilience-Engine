

package com.sentinel.transaction.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sentinel.transaction.dto.PaymentRequest;
import com.sentinel.transaction.dto.PaymentResponse;
import com.sentinel.transaction.service.TransactionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/payments")
public class TransactionController {
    
    @Autowired
private TransactionService transactionService;
    
    @PostMapping("/process")
    // Return the actual object, not the builder tool
    public PaymentResponse process(@Valid @RequestBody PaymentRequest request) { 
        return transactionService.processPayment(request); 
    }
}