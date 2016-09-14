package com.a.eye.skywalking.network.exception;

public class ConvertFailedException extends Exception {


    public ConvertFailedException(String message, Exception e) {
        super(message, e);
    }
}
