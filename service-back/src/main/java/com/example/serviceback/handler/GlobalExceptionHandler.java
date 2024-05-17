package com.example.serviceback.handler;

import com.example.serviceback.exception.MyException;
import com.example.serviceback.util.Result;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;

/**
 * 全局统一异常处理
 *
 * @author CJW
 * @since 2023/9/20
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MyException.class)
    public Result<Void> myExceptionHandler(RuntimeException e) {
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Result<String> constraintViolationExceptionHandler(ConstraintViolationException e){
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        String message = null;
        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
            message=constraintViolation.getMessage();
        }
        return Result.error(message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e){
        List<FieldError> fieldErrors = e.getFieldErrors();
        String message=null;
        for (FieldError fieldError : fieldErrors) {
            message=fieldError.getDefaultMessage();
        }
        return Result.error(message);
    }

    //@ExceptionHandler(Exception.class)
    public Result<Void> exceptionHandler(Exception e) {
        e.printStackTrace();
        return Result.error("系统未知异常");
    }
}
