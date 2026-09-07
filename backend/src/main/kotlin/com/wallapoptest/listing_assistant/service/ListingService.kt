package com.wallapoptest.listing_assistant.service

import com.fasterxml.jackson.core.StreamReadFeature
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.cfg.CoercionAction
import com.fasterxml.jackson.databind.cfg.CoercionInputShape
import com.fasterxml.jackson.databind.type.LogicalType
import com.wallapoptest.listing_assistant.aiassistant.ListingAssistant
import com.wallapoptest.listing_assistant.error.InvalidSuggestionException
import com.wallapoptest.listing_assistant.model.ListingRequest
import jakarta.validation.Validator
import org.springframework.ai.converter.BeanOutputConverter
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class ListingService(
    private val assistant: ListingAssistant,
    objectMapper: ObjectMapper,
    private val validator: Validator,
) {
    private val converter = BeanOutputConverter(ListingRequest::class.java, objectMapper.copy().apply {
        enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
        enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION.mappedFeature())
        listOf(CoercionInputShape.Integer, CoercionInputShape.Float, CoercionInputShape.Boolean)
            .forEach { coercionConfigFor(LogicalType.Textual).setCoercion(it, CoercionAction.Fail) }
        coercionConfigFor(LogicalType.Float).setCoercion(CoercionInputShape.String, CoercionAction.Fail)
    })

    fun suggest(description: String): ListingRequest {
        val raw = assistant.suggest(description.trim())
        if (raw.isBlank() || raw.length > 8192) throw InvalidSuggestionException()
        val listing = try {
            converter.convert(raw)
        } catch (exception: RuntimeException) {
            throw InvalidSuggestionException()
        } ?: throw InvalidSuggestionException()
        if (validator.validate(listing).isNotEmpty() ||
            listing.title.trim().length !in 3..120 || listing.title.none { it.isLetterOrDigit() } ||
            listing.tags.size !in 3..5 ||
            listing.tags.any { it.trim().length !in 1..30 || it.none { char -> char.isLetterOrDigit() } } ||
            listing.tags.map { it.trim().lowercase(Locale.ROOT) }.distinct().size != listing.tags.size ||
            listing.priceRange.min > listing.priceRange.max) {
            throw InvalidSuggestionException()
        }
        return listing.copy(title = listing.title.trim(), tags = listing.tags.map { it.trim() })
    }
}
