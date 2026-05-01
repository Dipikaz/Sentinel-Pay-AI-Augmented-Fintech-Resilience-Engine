import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Verify a standard risk check request"
    request {
        method 'POST'
        url '/api/v1/risk/check'
        body([
            customerId: "C_TEST_001",
            amount: 100.00
        ])
        headers {
            contentType(applicationJson())
        }
    }
    response {
        status OK()
        body([
            status: "APPROVED",
            riskScore: 10,
            reason: "Low risk transaction"
        ])
        headers {
            contentType(applicationJson())
        }
    }
}