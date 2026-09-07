package com.wallapoptest.listing_assistant.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.wallapoptest.listing_assistant.aiassistant.AssistantProperties
import com.wallapoptest.listing_assistant.aiassistant.ListingAssistant
import com.wallapoptest.listing_assistant.aiassistant.MockListingAssistant
import com.wallapoptest.listing_assistant.aiassistant.MockScenario
import com.wallapoptest.listing_assistant.error.InvalidSuggestionException
import jakarta.validation.Validation
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ListingServiceTests {
    @Test
    fun `returns saved listing through the same converter and validation`() {
        Validation.buildDefaultValidatorFactory().use { factory ->
            val assistant = ListingAssistant { description ->
                assertEquals("Chaqueta", description)
                MockListingAssistant(AssistantProperties()).suggest(description)
            }
            val result = ListingService(assistant, jacksonObjectMapper(), factory.validator).suggest(" Chaqueta ")
            assertEquals(4, result.tags.size)
            assertEquals(0, result.priceRange.min.compareTo(BigDecimal("40")))
        }
    }

    @Test
    fun `broken mocks fail validation`() {
        Validation.buildDefaultValidatorFactory().use { factory ->
            listOf(MockScenario.MALFORMED, MockScenario.NONSENSICAL).forEach { scenario ->
                val service = ListingService(
                    MockListingAssistant(AssistantProperties(mockScenario = scenario)),
                    jacksonObjectMapper(), factory.validator,
                )
                assertFailsWith<InvalidSuggestionException> { service.suggest("Chaqueta") }
            }
        }
    }

    @ParameterizedTest
    @MethodSource("invalidResponses")
    fun `rejects untrusted output`(raw: String) {
        Validation.buildDefaultValidatorFactory().use { factory ->
            val service = ListingService(ListingAssistant { raw }, jacksonObjectMapper(), factory.validator)
            assertFailsWith<InvalidSuggestionException> { service.suggest("Chaqueta") }
        }
    }

    companion object {
        @JvmStatic
        fun invalidResponses(): List<String> {
            val valid = """{"title":"Chaqueta","tags":["ropa","cuero","vintage"],"priceRange":{"min":40,"max":45}}"""
            return listOf(
                "", "null", "{}", "[]", "not json", "x".repeat(8193), valid + "{}",
                valid.replace("40", "40.555"), valid.replace("45", "45.555"),
                valid.replace("40", "46"), valid.replace("40", "null"),
                valid.replace("45", "null"), valid.replace("40", "0"), valid.replace("45", "-1"),
                valid.replace("45", "100000000"), valid.replace("45", "\"45\""),
                valid.replace("\"vintage\"", "null"), valid.replace("vintage", "CUERO"),
                valid.replace(",\"vintage\"", ""), valid.replace("Chaqueta", "???"),
                valid.replace("\"Chaqueta\"", "42"),
                valid.replace("\"min\":", "\"extra\":1,\"min\":"),
                valid.replace("\"min\":", "\"min\":2,\"min\":"),
            )
        }
    }
}
