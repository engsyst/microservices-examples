package ua.nure.it.microservice.orderservice.controller;

import jakarta.persistence.EntityNotFoundException;
//import jakarta.ws.rs.ServiceUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the REST controllers.
 * This class uses @ControllerAdvice to handle exceptions across the whole application.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles EntityNotFoundException and returns a 404 Not Found status.
     * This is useful for cases where a requested resource (like a MenuItem or Restaurant)
     * does not exist in the database.
     *
     * @param ex The EntityNotFoundException instance.
     * @return A ResponseEntity with the exception message and HTTP status code.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Handles MethodArgumentNotValidException and returns a 400 Bad Request status.
     * This is typically thrown when a @Valid annotated DTO fails validation.
     * It returns a map of field errors for a more detailed client-side response.
     *
     * @param ex The MethodArgumentNotValidException instance.
     * @return A ResponseEntity containing a map of validation errors and a BAD_REQUEST status.
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles ServiceUnavailableException and returns a 503 Service Unavailable status.
     * This is typically thrown when remote call returns 503 status code.
     *
     * @param ex The ServiceUnavailable instance.
     * @return A ResponseEntity with the exception message and 503 HTTP status code.
     */
//    @ExceptionHandler(value = ServiceUnavailableException.class,
//            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ResponseEntity<String> handleServiceUnavailableException(ServiceUnavailableException ex) {
//        return new ResponseEntity<>(ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
//    }
}
