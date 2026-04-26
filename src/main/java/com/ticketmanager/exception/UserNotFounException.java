package com.ticketmanager.exception;

public class UserNotFounException extends RuntimeException {
    public UserNotFounException(String message) {
        super(message);
    }
}
