package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.io.IOException;
import java.sql.SQLException;

@EnableWebMvc
@ControllerAdvice(annotations = RestController.class)
@Slf4j
public class ExceptionControllerAdvice {

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler({IOException.class, SQLException.class})
    @ResponseBody
    public Exception ioAndDBExceptionHandler(Exception exception) {
        log.error(exception.getMessage(), exception);
        return exception;
    }

    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Exception commonExceptionHandler(Exception exception) {
        log.error(exception.getMessage(), exception);
        return exception;
    }
}
