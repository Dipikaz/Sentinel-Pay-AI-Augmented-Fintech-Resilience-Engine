import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should send an APPROVED risk result message to Kafka"
    
    // This is the label your Java test calls
    label 'risk_result_approved'

    input {
        // This triggers the method in your ContractBaseClass
        triggeredBy('sendApprovedResult()') 
    }

    outputMessage {
        sentTo 'risk-results'
        body([
            // MUST match the fields in your RiskResponse DTO
            transactionId: "test-id-123", 
            status: "APPROVED",
            riskScore: 10,
            reason: "Low risk transaction"
        ])
        headers {
            header('contentType', 'application/json')
        }
    }
}