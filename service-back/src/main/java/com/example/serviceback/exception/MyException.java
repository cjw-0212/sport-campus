package com.example.serviceback.exception;

/**
 * @author CJW
 * @since 2024/3/25
 */
public class MyException extends RuntimeException {

    public MyException(MyErrorEnum myErrorEnum) {
        super(myErrorEnum.getMessage());
    }
}
