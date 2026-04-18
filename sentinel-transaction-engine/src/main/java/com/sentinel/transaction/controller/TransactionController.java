package com.sentinel.transaction.controller;

import com.sentinel.common.dto.PaymentRequest;
import com.sentinel.common.dto.TransactionResponse;
import com.sentinel.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor 
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/process")
    public ResponseEntity<TransactionResponse> process(@RequestBody PaymentRequest request) {
        try {
            // If service logic passes, return 200 OK
            TransactionResponse response = transactionService.processTransaction(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
         
            return ResponseEntity.badRequest().build(); 
        }
    }
}