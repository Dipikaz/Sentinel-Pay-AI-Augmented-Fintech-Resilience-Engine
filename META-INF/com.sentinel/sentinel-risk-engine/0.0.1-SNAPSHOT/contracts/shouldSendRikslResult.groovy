import org.springframework.cloud.contract.spec.Contract

Contract.make {
    label 'risk_result_approved' // This is the trigger name
    input {
        triggeredBy('sendApprovedResult()') // The method in our test that fires the event
    }
    outputMessage {
        sentTo 'risk-results'
        body([
            transactionId: $(anyUuid()),
            status: "APPROVED",
            riskScore: 10,
            reason: $(anyAlphaNumeric())
        ])
        headers {
            header('contentType', 'application/json')
        }
    }
}