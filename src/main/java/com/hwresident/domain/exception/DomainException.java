package com.hwresident.domain.exception;

/** Error de regla de negocio. */
public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}
