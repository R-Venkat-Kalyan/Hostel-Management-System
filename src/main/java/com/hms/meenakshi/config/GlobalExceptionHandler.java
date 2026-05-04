package com.hms.meenakshi.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;

import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleAllExceptions(Exception ex, Model model, HttpServletRequest request) {
        // Log the error for your own debugging
        System.err.println("Exception occurred: " + ex.getMessage());

        model.addAttribute("status", "500");
        model.addAttribute("message", "We encountered an internal error. Please try again later.");

        return "error";
    }


//    @ExceptionHandler({AsyncRequestNotUsableException.class, IOException.class})
//    public void handleAsyncAbort() {
//        // Just catch it. This happens when the browser tab is closed or refreshed.
//        // No need to print a 200-line stack trace.
//    }
}
