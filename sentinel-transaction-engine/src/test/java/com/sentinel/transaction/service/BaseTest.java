package com.sentinel.transaction.service;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

public class BaseTest {
    
    // The engine (Reports) should be static so it survives across all test classes
    protected static ExtentReports extent;
    
    // The specific test logger should be instance-based (remove static)
    protected ExtentTest test; 

    @BeforeAll
    public static void setupReport() {
        if (extent == null) {
            ExtentSparkReporter spark = new ExtentSparkReporter("target/ExtentReport.html");
            extent = new ExtentReports();
            extent.attachReporter(spark);
        }
    }
    
    @BeforeEach
    public void initTest(TestInfo testInfo) {
        // This automatically creates a test entry in the report using the method name
        test = extent.createTest(testInfo.getDisplayName());
    }

    @AfterAll
    public static void tearDownReport() {
        if (extent != null) {
            extent.flush();
        }
    }
}