package com.dhbw.backend.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 401 Unauthorized — falsche Credentials (Login, Passwort ändern)
    // Wirf im Service: throw new org.springframework.security.core.AuthenticationException("...") {}
    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuthenticationException(
            org.springframework.security.core.AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", ex.getMessage()));
    }

    // 403 Forbidden — eingeloggt, aber keine Berechtigung
    // Wirf im Service: throw new SecurityException("Nur der Host darf dieses Event löschen.")
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, String>> handleSecurityException(SecurityException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", ex.getMessage()));
    }

    // 404 Not Found — Ressource existiert nicht
    // Wirf im Service: throw new jakarta.persistence.EntityNotFoundException("Event nicht gefunden.")
    @ExceptionHandler(jakarta.persistence.EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFoundException(
            jakarta.persistence.EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    // 409 Conflict — Business-Logik-Konflikte (Duplikate, Kapazität voll, Selbst-Einladung)
    // Wirf im Service: throw new IllegalStateException("Gast ist bereits eingeladen.")
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalStateException(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
    }

    // 422 Unprocessable Entity — semantisch ungültige Inputs (ungültiger Status-Wert, Business-Regeln)
    // Wirf im Service: throw new IllegalArgumentException("Ungültiger Status: XYZ")
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of("error", ex.getMessage()));
    }

    // 422 Unprocessable Entity — @Valid Validierungsfehler (Pflichtfelder, Format, Länge)
    // Wird automatisch von Spring geworfen wenn @Valid fehlschlägt
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Map.of("error", "Validierungsfehler", "details", errors));
    }

    // 500 Internal Server Error — unerwartete Fehler (Fallback)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Ein interner Fehler ist aufgetreten."));
    }
}