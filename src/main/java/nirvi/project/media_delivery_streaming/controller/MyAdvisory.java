package nirvi.project.media_delivery_streaming.controller;

import nirvi.project.media_delivery_streaming.dto.ErrorInfo;
import nirvi.project.media_delivery_streaming.exception.FileNotFoundException;
import nirvi.project.media_delivery_streaming.exception.FileStorageException;
import nirvi.project.media_delivery_streaming.exception.InvalidFileFormatException;
import nirvi.project.media_delivery_streaming.exception.S3exception; // Corrected import

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;

@RestControllerAdvice
public class MyAdvisory {

    // Handle File Storage Exception
    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ErrorInfo> handleFileStorageException(FileStorageException ex) {
        ErrorInfo errorInfo = ErrorInfo.builder()
                .message(ex.getMessage())
                .errorCode(500)
                .timeStamp(new Date())
                .build();
        return new ResponseEntity<>(errorInfo, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Handle File Not Found Exception
    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ErrorInfo> handleFileNotFoundException(FileNotFoundException ex) {
        ErrorInfo errorInfo = ErrorInfo.builder()
                .message(ex.getMessage())
                .errorCode(404)
                .timeStamp(new Date())
                .build();
        return new ResponseEntity<>(errorInfo, HttpStatus.NOT_FOUND);
    }

    // Handle Invalid File Format Exception
    @ExceptionHandler(InvalidFileFormatException.class)
    public ResponseEntity<ErrorInfo> handleInvalidFileFormatException(InvalidFileFormatException ex) {
        ErrorInfo errorInfo = ErrorInfo.builder()
                .message(ex.getMessage())
                .errorCode(400)
                .timeStamp(new Date())
                .build();
        return new ResponseEntity<>(errorInfo, HttpStatus.BAD_REQUEST);
    }

    // Handle S3 Exception
    @ExceptionHandler(S3exception.class)
    public ResponseEntity<ErrorInfo> handleS3Exception(S3exception ex) {
        ErrorInfo errorInfo = ErrorInfo.builder()
                .message(ex.getMessage())
                .errorCode(503)  // S3 Service Unavailable
                .timeStamp(new Date())
                .build();
        return new ResponseEntity<>(errorInfo, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
