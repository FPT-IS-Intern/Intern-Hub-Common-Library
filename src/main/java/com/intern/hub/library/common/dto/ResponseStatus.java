package com.intern.hub.library.common.dto;

import java.util.List;

/**
 * Represents the status information of an API response.
 * <p>
 * This record contains a status code, an optional message describing the result,
 * and an optional list of field-level validation errors.
 * </p>
 *
 * <p><b>Common Status Codes:</b></p>
 * <ul>
 *   <li>{@code success} - Operation completed successfully</li>
 *   <li>{@code bad.request} - Invalid request parameters</li>
 *   <li>{@code resource.not.found} - Requested resource not found</li>
 *   <li>{@code forbidden} - Access denied</li>
 *   <li>{@code unauthorized} - Authentication required</li>
 *   <li>{@code internal.server.error} - Server-side error occurred</li>
 * </ul>
 *
 * @param code    the status code identifying the result type
 * @param message a human-readable message describing the result
 * @param errors  a list of field-level validation errors (null if no validation errors)
 * @see ResponseApi
 * @see FieldError
 * @see com.intern.hub.library.common.exception.ExceptionConstant
 */
public record ResponseStatus(String code, String message, List<FieldError> errors) {

    /**
     * Creates a ResponseStatus with a code and message, without field errors.
     *
     * @param code    the status code
     * @param message the status message
     */
    public ResponseStatus(String code, String message) {
        this(code, message, null);
    }

    /**
     * Creates a ResponseStatus with only a code, without message or field errors.
     *
     * @param code the status code
     */
    public ResponseStatus(String code) {
        this(code, null, null);
    }
}
