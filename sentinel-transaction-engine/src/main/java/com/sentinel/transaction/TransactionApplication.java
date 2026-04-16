package com.sentinel.transaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.sentinel")
@EnableFeignClients(basePackages = "com.sentinel.transaction.client")

public class TransactionApplication {
	public static void main(String[] args) {
        SpringApplication.run(TransactionApplication.class, args);
    }
	

}
