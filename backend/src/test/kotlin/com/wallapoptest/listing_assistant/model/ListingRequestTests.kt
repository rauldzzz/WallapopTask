package com.wallapoptest.listing_assistant.model

import jakarta.validation.Validation
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ListingRequestTests {
    @Test
    fun `accepts prices with up to two decimal places`() {
        Validation.buildDefaultValidatorFactory().use { factory ->
            listOf("25", "25.5", "25.50").forEach { price ->
                val request = ListingRequest("Bicicleta", listOf("urbana", "bicicleta"), PriceRange(BigDecimal(price), BigDecimal(price)))

                assertTrue(factory.validator.validate(request).isEmpty())
            }
        }
    }

    @Test
    fun `rejects prices with more than two decimal places`() {
        Validation.buildDefaultValidatorFactory().use { factory ->
            val request = ListingRequest("Bicicleta", listOf("urbana", "bicicleta"), PriceRange(BigDecimal("25"), BigDecimal("25.555")))

            val violations = factory.validator.validate(request)

            assertEquals(1, violations.size)
            assertEquals("priceRange.max", violations.single().propertyPath.toString())
        }
    }
}
