package com.raulbsouza.wex.TransactionSolution.exception;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDetails genericException(Exception ex, WebRequest request) {
        logger.error(ex.getMessage());
        return new ErrorDetails(ex.getMessage(), request.getDescription(false));
    }
    @ExceptionHandler(value = {ResourceNotFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorDetails resourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        return new ErrorDetails(ex.getMessage(), request.getDescription(false));
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ValidationError constraintValidationError(MethodArgumentNotValidException exception) {
        ValidationError errors = new ValidationError();
        List<FieldError> violations = exception.getBindingResult().getFieldErrors();
        for (FieldError violation : violations) {
            errors.addViolations(new ErrorDetails(violation.getDefaultMessage(), violation.getField()));
        }
        return errors;
    }
}
