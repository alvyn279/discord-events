package com.alvyn279.discord.exception;

/**
 * Exception thrown when a discord user tries to perform sensitive
 * operations on events that do not belong to them.
 */
public class AccessDeniedException extends Exception {
    public AccessDeniedException(String message) {
        super(message);
    }
}
