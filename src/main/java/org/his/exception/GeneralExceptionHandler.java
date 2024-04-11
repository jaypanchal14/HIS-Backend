package org.his.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class GeneralExceptionHandler {

    @ExceptionHandler(value = {AuthenticationException.class})
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException authException){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).contentType(MediaType.APPLICATION_JSON).body("{\"error\": \""+authException.getMessage()+"\"}");
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<?> generalException(IllegalArgumentException exception){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body("{\"error\": \""+exception.getMessage()+"\"}");
    }

    @ExceptionHandler(value = {NoSuchAccountException.class, UsernameNotFoundException.class})
    public ResponseEntity<?> accountException(Exception exception){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"error\": \""+exception.getMessage()+"\"}");
    }

    @ExceptionHandler(value = {IOException.class})
    public ResponseEntity<?> servletException(IOException exception){
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).contentType(MediaType.APPLICATION_JSON).body("{\"error\": \""+exception.getMessage()+"\"}");
    }


}
