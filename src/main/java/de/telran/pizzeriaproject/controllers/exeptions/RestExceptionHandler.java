package de.telran.pizzeriaproject.controllers.exeptions;

import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@RestControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(Exception.class)
    ResponseEntity<ProblemDetail> handleException(Exception e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        problemDetail.setTitle("Ошибка на сервере");
        problemDetail.setType(URI.create("https://api.documents.com/errors/internal_server_error"));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("traceId", MDC.get("traceId"));
        return new ResponseEntity<>(problemDetail, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Обработка иключений при валидации (not null, not empty, size)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ProblemDetail> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult().getFieldErrors()
                .stream().map(FieldError::getDefaultMessage).toList();

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        problemDetail.setTitle(e.getMessage());
        problemDetail.setDetail(Arrays.toString(errors.toArray()));
        problemDetail.setType(URI.create("https://api.documents.com/errors/unprocessable_entity"));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("traceId", MDC.get("traceId"));
        return new ResponseEntity<>(problemDetail, HttpStatus.UNPROCESSABLE_ENTITY);
    }

//    //Обработка иключений при валидации (unique)
//    @ExceptionHandler(DuplicateEntryException.class)
//    protected ResponseEntity<ProblemDetail> handleUniqueNotValid(DuplicateEntryException e) {
//        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
//        problemDetail.setTitle("Duplicate entry");
//        problemDetail.setDetail(e.getMessage());
//        problemDetail.setType(URI.create("https://api.documents.com/errors/unprocessable_entity"));
//        problemDetail.setProperty("timestamp", Instant.now());
//        problemDetail.setProperty("traceId", MDC.get("traceId"));
//        return new ResponseEntity<>(problemDetail, HttpStatus.UNPROCESSABLE_ENTITY);
//    }
}
