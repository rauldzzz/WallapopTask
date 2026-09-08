package com.wallapoptest.listing_assistant.error

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

data class ApiError(val code: String, val message: String)

@RestControllerAdvice
class ApiExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun invalidDescription() = error(HttpStatus.BAD_REQUEST, "INVALID_DESCRIPTION",
        "The description should contain between 3 and 2000 characters and cannor be empty.")

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun invalidRequest() = error(HttpStatus.BAD_REQUEST, "INVALID_REQUEST",
        "Send a JSON object with type text description.")

    @ExceptionHandler(InvalidSuggestionException::class)
    fun invalidResponse() = error(HttpStatus.BAD_GATEWAY, "INVALID_MODEL_RESPONSE",
        "The suggest was not valid.")

    @ExceptionHandler(ProviderTimeoutException::class)
    fun timeout() = error(HttpStatus.GATEWAY_TIMEOUT, "PROVIDER_TIMEOUT",
        "The suggest service had a timeout.")

    @ExceptionHandler(ProviderUnavailableException::class)
    fun unavailable() = error(HttpStatus.SERVICE_UNAVAILABLE, "PROVIDER_UNAVAILABLE",
        "The suggest service is unavailable.")

    private fun error(status: HttpStatus, code: String, message: String) =
        ResponseEntity.status(status).body(ApiError(code, message))
}
