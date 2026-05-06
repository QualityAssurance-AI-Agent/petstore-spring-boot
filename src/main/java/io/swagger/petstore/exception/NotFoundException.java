package io.swagger.petstore.exception;

/** Thrown when a referenced entity does not exist. Handled as HTTP 404. */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
