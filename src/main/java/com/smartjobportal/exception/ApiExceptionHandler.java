package com.smartjobportal.exception;

import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(NoSuchElementException.class)
    ResponseEntity<Map<String,Object>> notFound(Exception ex,HttpServletRequest req) { return body(HttpStatus.NOT_FOUND,ex.getMessage(),req); }
    @ExceptionHandler({IllegalArgumentException.class})
    ResponseEntity<Map<String,Object>> badRequest(Exception ex,HttpServletRequest req) { return body(HttpStatus.BAD_REQUEST,ex.getMessage(),req); }
    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<Map<String,Object>> forbidden(Exception ex,HttpServletRequest req) { return body(HttpStatus.FORBIDDEN,ex.getMessage(),req); }
    @ExceptionHandler(BadCredentialsException.class)
    ResponseEntity<Map<String,Object>> unauthorized(Exception ex,HttpServletRequest req) { return body(HttpStatus.UNAUTHORIZED,"Invalid email or password",req); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String,Object>> validation(MethodArgumentNotValidException ex,HttpServletRequest req) {
        String msg=ex.getBindingResult().getFieldErrors().stream().map(e->e.getField()+": "+e.getDefaultMessage()).findFirst().orElse("Validation failed");
        return body(HttpStatus.BAD_REQUEST,msg,req);
    }
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    ResponseEntity<Map<String,Object>> conflict(Exception ex,HttpServletRequest req) { return body(HttpStatus.CONFLICT,"Database constraint conflict",req); }
    @ExceptionHandler(Exception.class)
    ResponseEntity<Map<String,Object>> internal(Exception ex,HttpServletRequest req) { return body(HttpStatus.INTERNAL_SERVER_ERROR,"Unexpected server error",req); }
    private ResponseEntity<Map<String,Object>> body(HttpStatus status,String message,HttpServletRequest req) {
        return ResponseEntity.status(status).body(Map.of("timestamp",java.time.Instant.now().toString(),"status",status.value(),
          "error",status.getReasonPhrase(),"message",Objects.requireNonNullElse(message,"Request failed"),"path",req.getRequestURI()));
    }
}
