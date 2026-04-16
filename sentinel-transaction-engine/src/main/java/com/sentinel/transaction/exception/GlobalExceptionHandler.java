package com.sentinel.transaction.exception;



import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;


public class GlobalExceptionHandler {
	
	@ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Map<String, String> handleGeneralError(Exception e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Sentinel System Error");
        error.put("message", e.getMessage());
        return error;
        
        
    
    }
	
	public Map<String, Object> handleAllErrors(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", ex.getMessage());
        return response;
    }

}
