package ru.netology.netologydiplomacloudservice.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import ru.netology.netologydiplomacloudservice.dto.ErrorDto;
import ru.netology.netologydiplomacloudservice.exception.IncorrectFileDataException;

@Slf4j
@ControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler({
        MaxUploadSizeExceededException.class,
        IncorrectFileDataException.class,
        MissingServletRequestParameterException.class})
    public ResponseEntity<ErrorDto> handleClientException(Exception ex) {
        log.error(ex.getMessage());
        return ResponseEntity.badRequest().body(new ErrorDto(ex.getMessage()));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDto> handleAccessDeniedException(Exception ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorDto(ex.getMessage()));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleFileProcessingException(Exception ex) {
        log.error(ex.getMessage());
        return ResponseEntity.internalServerError().body(new ErrorDto(ex.getMessage()));
    }
}
