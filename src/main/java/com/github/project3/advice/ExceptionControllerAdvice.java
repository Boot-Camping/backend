package com.github.project3.advice;


import com.github.project3.service.exceptions.InvalidValueException;
import com.github.project3.service.exceptions.NotAcceptException;
import com.github.project3.service.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionControllerAdvice {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException nfe) {
        log.error("Client 요청 이후 DB 검색 중 에러로 다음처럼 줄력합니다. " + nfe.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(nfe.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(NotAcceptException.class)
    public ResponseEntity<String> handleNotAcceptException(NotAcceptException nae) {
        log.error("Client 요청이 거부됩니다." + nae.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(nae.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidValueException.class)
    public ResponseEntity<String> handleInvalidValueException(InvalidValueException ive) {
        log.error("Client 요청에 문제가 있어 다음처럼 출력합니다. " + ive.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ive.getMessage());
    }
}
