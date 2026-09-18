package ru.prohor.universe.scarif.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import ru.prohor.universe.jocasta.core.collections.common.Opt;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
// TODO move to jocasta spring web
// TODO log instead of returning description
// TODO убрать дубликат GlobalExceptionController в Yahtzee
public class GlobalExceptionController {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Validation failed");
        error.put(
                "details",
                e.getBindingResult().getFieldErrors().stream()
                        .map(fieldError -> Map.of(
                                "field", fieldError.getField(),
                                "message", Opt.ofNullable(fieldError.getDefaultMessage()).orElse("")
                        ))
                        .toList()
        );
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonParseException(HttpMessageNotReadableException e) {
        // TODO log
        System.err.println("Invalid JSON");
        e.printStackTrace();

        return ResponseEntity.badRequest().body(Map.of(
                "error", "Invalid JSON",
                "details", e.getMostSpecificCause().getMessage()
        ));
    }

    // HttpMessageNotWritableException


    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException e) {
        // TODO log
        System.err.println("ResponseStatusException: " + e.getStatusCode());
        e.printStackTrace();
        return ResponseEntity.status(e.getStatusCode()).body(Map.of(
                "error", Opt.ofNullable(e.getReason()).orElse(e.getStatusCode().toString())
        ));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<Map<String, Object>> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        // TODO log
        System.err.println("Method Not Allowed");
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(Map.of(
                "error", "Method Not Allowed",
                "details", e.getMessage()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleOtherExceptions(Exception e) {
        // TODO log
        System.err.println("Internal Server Error");
        e.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Internal Server Error",
                "details", e.getMessage()
        ));
    }
}
