package com.intern.hub.library.common.autoconfig.exadvice;

import com.intern.hub.library.common.dto.FieldError;
import com.intern.hub.library.common.dto.ResponseApi;
import com.intern.hub.library.common.dto.ResponseMetadata;
import com.intern.hub.library.common.dto.ResponseStatus;
import com.intern.hub.library.common.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler that provides centralized exception handling for
 * REST controllers.
 * <p>
 * This advice catches various exception types and transforms them into
 * standardized
 * {@link ResponseApi} responses with appropriate HTTP status codes.
 * </p>
 *
 * <p>
 * <b>Handled Exceptions:</b>
 * </p>
 * <ul>
 * <li>{@link NotFoundException} - HTTP 404 Not Found</li>
 * <li>{@link BadRequestException} - HTTP 400 Bad Request</li>
 * <li>{@link ConflictDataException} - HTTP 409 Conflict</li>
 * <li>{@link ForbiddenException} - HTTP 403 Forbidden</li>
 * <li>{@link UnauthorizeException} - HTTP 401 Unauthorized</li>
 * <li>{@link TooManyRequestException} - HTTP 429 Too Many Requests</li>
 * <li>{@link InternalErrorException} - HTTP 500 Internal Server Error</li>
 * <li>{@link MethodArgumentTypeMismatchException} - HTTP 400 Bad Request</li>
 * <li>{@link MethodArgumentNotValidException} - HTTP 400 Bad Request</li>
 * <li>{@link Exception} - HTTP 500 Internal Server Error (catch-all)</li>
 * </ul>
 *
 * @see ResponseApi
 * @see ResponseStatus
 * @see ResponseMetadata
 */
@Slf4j
@ControllerAdvice
public class DefaultGlobalExceptionAdvice {

    /**
     * Handles generic exceptions not caught by other handlers.
     *
     * @param exception the exception that was thrown
     * @return a ResponseEntity containing the error response with HTTP 500 status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseApi<?>> handleGenericException(Exception exception) {
        log.error("Unhandled exception occurred", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ResponseApi<>(
                        new ResponseStatus(ExceptionConstant.INTERNAL_SERVER_ERROR_DEFAULT_CODE,
                                exception.getMessage() != null ? exception.getMessage()
                                        : ExceptionConstant.UNKNOWN_ERROR),
                        null,
                        ResponseMetadata.fromRequestId()));
    }

    /**
     * Handles NotFoundException and returns HTTP 404 Not Found.
     *
     * @param exception the NotFoundException that was thrown
     * @return a ResponseEntity containing the error response with HTTP 404 status
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ResponseApi<?>> handleNotFoundException(NotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseApi<>(
                        new ResponseStatus(
                                exception.getCode() != null ? exception.getCode()
                                        : ExceptionConstant.NOT_FOUND_DEFAULT_CODE,
                                exception.getMessage() != null ? exception.getMessage()
                                        : ExceptionConstant.RESOURCE_NOT_FOUND),
                        null,
                        ResponseMetadata.fromRequestId()));
    }

    /**
     * Handles BadRequestException and returns HTTP 400 Bad Request.
     *
     * @param exception the BadRequestException that was thrown
     * @return a ResponseEntity containing the error response with HTTP 400 status
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ResponseApi<?>> handleBadRequestException(BadRequestException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ResponseApi<>(
                        new ResponseStatus(
                                exception.getCode() != null ? exception.getCode()
                                        : ExceptionConstant.BAD_REQUEST_DEFAULT_CODE,
                                exception.getMessage() != null ? exception.getMessage()
                                        : ExceptionConstant.BAD_REQUEST),
                        null,
                        ResponseMetadata.fromRequestId()));
    }

    /**
     * Handles ConflictDataException and returns HTTP 409 Conflict.
     *
     * @param exception the ConflictDataException that was thrown
     * @return a ResponseEntity containing the error response with HTTP 409 status
     */
    @ExceptionHandler(ConflictDataException.class)
    public ResponseEntity<ResponseApi<?>> handleConflictDataException(ConflictDataException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ResponseApi<>(
                        new ResponseStatus(
                                exception.getCode() != null ? exception.getCode()
                                        : ExceptionConstant.CONFLICT_DATA_DEFAULT_CODE,
                                exception.getMessage() != null ? exception.getMessage()
                                        : ExceptionConstant.CONFLICT_DATA),
                        null,
                        ResponseMetadata.fromRequestId()));
    }

    /**
     * Handles ForbiddenException and returns HTTP 403 Forbidden.
     *
     * @param exception the ForbiddenException that was thrown
     * @return a ResponseEntity containing the error response with HTTP 403 status
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ResponseApi<?>> handleForbiddenException(ForbiddenException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                new ResponseApi<>(
                        new ResponseStatus(
                                exception.getCode() != null ? exception.getCode()
                                        : ExceptionConstant.FORBIDDEN_DEFAULT_CODE,
                                exception.getMessage() != null ? exception.getMessage() : ExceptionConstant.FORBIDDEN),
                        null,
                        ResponseMetadata.fromRequestId()));
    }

    /**
     * Handles UnauthorizeException and returns HTTP 401 Unauthorized.
     *
     * @param exception the UnauthorizeException that was thrown
     * @return a ResponseEntity containing the error response with HTTP 401 status
     */
    @ExceptionHandler(UnauthorizeException.class)
    public ResponseEntity<ResponseApi<?>> handleUnauthorizeException(UnauthorizeException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ResponseApi<>(
                        new ResponseStatus(
                                exception.getCode() != null ? exception.getCode()
                                        : ExceptionConstant.UNAUTHORIZED_DEFAULT_CODE,
                                exception.getMessage() != null ? exception.getMessage()
                                        : ExceptionConstant.UNAUTHORIZED),
                        null,
                        ResponseMetadata.fromRequestId()));
    }

    /**
     * Handles TooManyRequestException and returns HTTP 429 Too Many Requests.
     *
     * @param exception the TooManyRequestException that was thrown
     * @return a ResponseEntity containing the error response with HTTP 429 status
     */
    @ExceptionHandler(TooManyRequestException.class)
    public ResponseEntity<ResponseApi<?>> handleTooManyRequestException(TooManyRequestException exception) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(
                new ResponseApi<>(
                        new ResponseStatus(
                                exception.getCode() != null ? exception.getCode()
                                        : ExceptionConstant.TOO_MANY_REQUESTS_DEFAULT_CODE,
                                exception.getMessage() != null ? exception.getMessage()
                                        : ExceptionConstant.TOO_MANY_REQUESTS),
                        null,
                        ResponseMetadata.fromRequestId()));
    }

    /**
     * Handles InternalErrorException and returns HTTP 500 Internal Server Error.
     *
     * @param exception the InternalErrorException that was thrown
     * @return a ResponseEntity containing the error response with HTTP 500 status
     */
    @ExceptionHandler(InternalErrorException.class)
    public ResponseEntity<ResponseApi<?>> handleInternalErrorException(InternalErrorException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ResponseApi<>(
                        new ResponseStatus(
                                exception.getCode() != null ? exception.getCode()
                                        : ExceptionConstant.INTERNAL_SERVER_ERROR_DEFAULT_CODE,
                                exception.getMessage() != null ? exception.getMessage()
                                        : ExceptionConstant.UNKNOWN_ERROR),
                        null,
                        ResponseMetadata.fromRequestId()));
    }

    /**
     * Handles NoHandlerFoundException when no handler is found for a request.
     *
     * @return a ResponseEntity containing the error response with HTTP 404 status
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ResponseApi<?>> handleNoHandlerFoundException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseApi<>(
                        new ResponseStatus(
                                ExceptionConstant.HANDLER_NOT_FOUND_DEFAULT_CODE,
                                ExceptionConstant.HANDLER_NOT_FOUND),
                        null,
                        ResponseMetadata.fromRequestId()));
    }

    private static final String INVALID_PARAMETER_MESSAGE_TEMPLATE = "Invalid value '%s' for parameter '%s'. Expected type: %s";

    /**
     * Handles MethodArgumentTypeMismatchException when a request parameter cannot
     * be converted to the expected type.
     *
     * @param exception the MethodArgumentTypeMismatchException that was thrown
     * @return a ResponseEntity containing the error response with HTTP 400 status
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResponseApi<?>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException exception) {
        String requiredType = exception.getRequiredType() != null ? exception.getRequiredType().getSimpleName()
                : "unknown type";
        String errorMessage = String.format(INVALID_PARAMETER_MESSAGE_TEMPLATE, exception.getValue(),
                exception.getName(), requiredType);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ResponseApi<>(
                        new ResponseStatus(
                                ExceptionConstant.INVALID_PARAMETER_DEFAULT_CODE,
                                errorMessage),
                        null,
                        ResponseMetadata.fromRequestId()));
    }

    /**
     * Handles MethodArgumentNotValidException for validation errors on request body
     * fields.
     * <p>
     * Returns a list of {@link FieldError} objects containing details about each
     * invalid field.
     * </p>
     *
     * @param exception the MethodArgumentNotValidException that was thrown
     * @return a ResponseEntity containing the error response with field errors and
     *         HTTP 400 status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseApi<List<FieldError>>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception) {
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldError(error.getField(),
                        error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value"))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ResponseApi<>(
                        new ResponseStatus(
                                ExceptionConstant.INVALID_PARAMETER_DEFAULT_CODE,
                                "Invalid request parameters"),
                        fieldErrors,
                        ResponseMetadata.fromRequestId()));
    }

}
