package io.swagger.petstore.exception;

/** Thrown when the request is syntactically valid but semantically invalid. Handled as HTTP 400. */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
