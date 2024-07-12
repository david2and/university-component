package com.javeriana.component.errors;

public class WrongFormatFieldException extends RuntimeException {

    public WrongFormatFieldException(String message) {
            super(message);
        }
}
