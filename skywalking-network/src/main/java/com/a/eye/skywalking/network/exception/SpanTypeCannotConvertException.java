package com.a.eye.skywalking.network.exception;

public class SpanTypeCannotConvertException extends RuntimeException {
    public SpanTypeCannotConvertException(String spanTypeValue) {
        super("Can not convert SpanTypeValue[" + spanTypeValue + "]");
    }
}
