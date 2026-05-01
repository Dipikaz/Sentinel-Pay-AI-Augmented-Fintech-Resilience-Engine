import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should send an APPROVED risk result message to the risk-results topic"
    
    // This is the name your Java test uses to fire the message
    label 'risk_result_approved'

    input {
        // This triggers the message in your Base Class
        triggeredBy('sendApprovedResult()')
    }

    outputMessage {
        // This MUST match the topic name in your listener
        sentTo 'risk-results'
        body([
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