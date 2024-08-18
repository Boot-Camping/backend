package com.github.project3.advice;


import com.github.project3.service.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.lang.IllegalArgumentException;
import java.sql.SQLIntegrityConstraintViolationException;

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

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ive) {
        log.error("요청에 문제가 있어 다음처럼 출력합니다. " + ive.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ive.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<String> handleFileUploadException(FileUploadException fue) {
        log.error("파일 업로드 중 오류가 발생했습니다. " + fue.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(fue.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CategoryException.class)
    public ResponseEntity<String> handleCategoryException(CategoryException ce) {
        log.error("카테고리 처리 중 오류 발생: " + ce.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ce.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DescriptionException.class)
    public ResponseEntity<String> handleDescriptionException(DescriptionException de) {
        log.error("설명 처리 중 오류 발생: " + de.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(de.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ImageException.class)
    public ResponseEntity<String> handleImageException(ImageException ie) {
        log.error("이미지 처리 중 오류 발생: " + ie.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ie.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        // 루트 원인을 확인하여 맞춤 메시지 설정
        Throwable rootCause = NestedExceptionUtils.getRootCause(ex);
        String errorMessage = "데이터 무결성 제약 조건 위반으로 인해 작업을 수행할 수 없습니다.";

        if (rootCause != null && rootCause.getMessage() != null && rootCause.getMessage().contains("FOREIGN KEY")) {
            errorMessage = "해당 캠핑지는 예약되어있어 삭제할 수 없습니다.";
        }

        log.error("데이터 무결성 제약 조건 위반: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
    }

}
