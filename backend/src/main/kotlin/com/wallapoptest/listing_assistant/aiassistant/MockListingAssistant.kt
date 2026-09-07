package com.wallapoptest.listing_assistant.aiassistant

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import java.util.Locale

@Component
@ConditionalOnProperty(prefix = "app.ai", name = ["mock-mode"], havingValue = "true", matchIfMissing = true)
class MockListingAssistant(properties: AssistantProperties) : ListingAssistant {
    private val response = ClassPathResource(
        "mock/${properties.mockScenario.name.lowercase(Locale.ROOT)}.json"
    ).inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }

    override fun suggest(description: String): String = response
}
