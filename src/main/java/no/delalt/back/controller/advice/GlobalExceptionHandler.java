package no.delalt.back.controller.advice;

import no.delalt.back.response.ErrorResponse;
import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(
    GlobalExceptionHandler.class
  );

  protected ResponseEntity<?> createErrorResponse(
    HttpStatus status,
    String message
  ) {
    return ResponseEntity
      .status(status)
      .body(new ErrorResponse(status.value(), message));
  }

  @ExceptionHandler(ResponseStatusException.class)
  protected ResponseEntity<?> handleResponseStatusException(
    ResponseStatusException e
  ) {
    return createErrorResponse(
      HttpStatus.valueOf(e.getStatusCode().value()),
      e.getReason()
    );
  }

  @ExceptionHandler(ClientAbortException.class)
  protected void handleClientAbortException(ClientAbortException e) {
    LOGGER.warn("Client closed the connection prematurely: " + e.getMessage());
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  protected ResponseEntity<?> handleDataIntegrityViolationException(
    DataIntegrityViolationException e
  ) {
    return createErrorResponse(
      HttpStatus.BAD_REQUEST,
      "Wrong information given."
    );
  }

  /**

  @ExceptionHandler(ConstraintViolationException.class)
  protected ResponseEntity<?> handleConstraintViolationException(
    ConstraintViolationException e
  ) {
    return createErrorResponse(
      HttpStatus.BAD_REQUEST,
      "Wrong information given."
    );
  }

  @ExceptionHandler(MismatchedInputException.class)
  public ResponseEntity<?> handleMismatchedInputException(
          MismatchedInputException e) {

    return createErrorResponse(
            HttpStatus.BAD_REQUEST,
            "Wrong information given."
    );
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<?> handleHttpMessageNotReadableException(
          HttpMessageNotReadableException e) {

    return createErrorResponse(
            HttpStatus.BAD_REQUEST,
            "Wrong information given."
    );
  }

  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<?> handleHttpMediaTypeNotSupportedException(
          HttpMediaTypeNotSupportedException e) {

    return createErrorResponse(
            HttpStatus.BAD_REQUEST,
            "Wrong information given."
    );
  }
   */

  @ExceptionHandler(MethodArgumentNotValidException.class)
  protected ResponseEntity<?> handleMethodArgumentNotValidException(
    MethodArgumentNotValidException e
  ) {
    return createErrorResponse(
      HttpStatus.BAD_REQUEST,
      "Wrong information given."
    );
  }

  @ExceptionHandler(Exception.class)
  protected ResponseEntity<?> handleGeneralException(Exception e) {
    LOGGER.error("An unexpected exception occurred", e);
    return createErrorResponse(
      HttpStatus.INTERNAL_SERVER_ERROR,
      "An error occurred while processing the request"
    );
  }
}
