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
        "La descripción debe contener entre 3 y 2000 caracteres y no estar vacía.")

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun invalidRequest() = error(HttpStatus.BAD_REQUEST, "INVALID_REQUEST",
        "Envía un objeto JSON con description de tipo texto.")

    @ExceptionHandler(InvalidSuggestionException::class)
    fun invalidResponse() = error(HttpStatus.BAD_GATEWAY, "INVALID_MODEL_RESPONSE",
        "No se ha podido obtener una sugerencia válida.")

    @ExceptionHandler(ProviderTimeoutException::class)
    fun timeout() = error(HttpStatus.GATEWAY_TIMEOUT, "PROVIDER_TIMEOUT",
        "El servicio de sugerencias ha tardado demasiado.")

    @ExceptionHandler(ProviderUnavailableException::class)
    fun unavailable() = error(HttpStatus.SERVICE_UNAVAILABLE, "PROVIDER_UNAVAILABLE",
        "El servicio de sugerencias no está disponible.")

    private fun error(status: HttpStatus, code: String, message: String) =
        ResponseEntity.status(status).body(ApiError(code, message))
}
