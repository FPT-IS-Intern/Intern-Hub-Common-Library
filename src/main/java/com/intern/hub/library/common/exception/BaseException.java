package com.intern.hub.library.common.exception;

import lombok.Getter;

/**
 * Abstract base class for all application-specific exceptions.
 * <p>
 * This class provides a consistent structure for exceptions throughout the
 * library,
 * including an error code and support for cause chaining. All HTTP-specific
 * exceptions
 * should extend this class.
 * </p>
 *
 * <p>
 * <b>Features:</b>
 * </p>
 * <ul>
 * <li>Error code for programmatic error identification</li>
 * <li>Optional error message for human-readable details</li>
 * <li>Cause chaining for preserving original exception stack traces</li>
 * </ul>
 *
 * @see BadRequestException
 * @see NotFoundException
 * @see ForbiddenException
 * @see UnauthorizeException
 * @see ConflictDataException
 * @see TooManyRequestException
 * @see InternalErrorException
 */
@Getter
public abstract class BaseException extends RuntimeException {

    /**
     * The error code identifying the specific type of error.
     * <p>
     * This code is used for programmatic error identification and can be used
     * by clients to display localized error messages.
     * </p>
     */
    private final String code;

    /**
     * Constructs a new exception with the specified error code.
     *
     * @param code the error code identifying the type of error
     */
    protected BaseException(String code) {
        super();
        this.code = code;
    }

    /**
     * Constructs a new exception with the specified error code and message.
     *
     * @param code    the error code identifying the type of error
     * @param message a detailed message describing the error
     */
    protected BaseException(String code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * Constructs a new exception with the specified error code, message, and cause.
     * <p>
     * This constructor is useful for wrapping lower-level exceptions while
     * preserving
     * the original stack trace for debugging purposes.
     * </p>
     *
     * @param code    the error code identifying the type of error
     * @param message a detailed message describing the error
     * @param cause   the cause of this exception (may be {@code null})
     */
    protected BaseException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

}
