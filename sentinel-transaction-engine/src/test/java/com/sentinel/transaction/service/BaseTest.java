package com.sentinel.transaction.service;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.junit.jupiter.api.*;

public class BaseTest {
    
    protected static ExtentReports extent;
    protected ExtentTest test; 

    @BeforeAll
    public static void setupReport() {
        if (extent == null) {
            // SparkReporter is the standard for Extent 5.x
            ExtentSparkReporter spark = new ExtentSparkReporter("target/ExtentReport.html");
            spark.config().setReportName("Sentinel Transaction Engine Unit Tests");
            spark.config().setDocumentTitle("Test Results");
            
            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Environment", "QA/Local");
            extent.setSystemInfo("User", "Dipika");
        }
    }
    
    @BeforeEach
    public void initTest(TestInfo testInfo) {
        // Create the test entry using the actual Method Name
        String testName = testInfo.getTestMethod().isPresent() 
                          ? testInfo.getTestMethod().get().getName() 
                          : testInfo.getDisplayName();
        
        test = extent.createTest(testName);
        test.info("Starting test execution: " + testName);
    }

    @AfterEach
    public void logTestResult(TestInfo testInfo) {
        // Optional: You could add logic here to check if the test failed 
        // and log it to ExtentReports automatically.
    }

    @AfterAll
    public static void tearDownReport() {
        if (extent != null) {
            System.out.println("DEBUG: Flushing Extent Reports...");
            extent.flush();
        }
    }
}