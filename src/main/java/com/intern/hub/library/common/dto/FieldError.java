package com.intern.hub.library.common.dto;

/**
 * Represents a validation error for a specific field in a request.
 * <p>
 * This record is used to provide detailed information about validation failures
 * when processing request payloads with field-level validation.
 * </p>
 *
 * @param field   the name of the field that failed validation
 * @param message the validation error message describing what went wrong
 * @see ResponseApi
 * @see ResponseStatus
 */
public record FieldError(String field, String message) {}
