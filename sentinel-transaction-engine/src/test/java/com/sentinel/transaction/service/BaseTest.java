package com.sentinel.transaction.service;


import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public class BaseTest {
	
	
	protected static ExtentReports extent;
    protected static ExtentTest test;

    @BeforeAll
    public static void setupReport() {
        // This creates an HTML file in your target folder
        ExtentSparkReporter spark = new ExtentSparkReporter("target/ExtentReport.html");
        extent = new ExtentReports();
        extent.attachReporter(spark);
    }

    @AfterAll
    public static void tearDownReport() {
        extent.flush(); // This actually writes the report to the file
    }

}
