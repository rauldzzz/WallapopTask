package com.wallapoptest.listing_assistant.aiassistant

import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Pattern
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@Validated
@ConfigurationProperties("app.ai")
class AssistantProperties(
    val mockMode: Boolean = true,
    val mockScenario: MockScenario = MockScenario.VALID,
    val apiKey: String = "",
    @field:Pattern(regexp = "[a-zA-Z0-9._-]+")
    val model: String = "gemini-3.5-flash-lite",
    @field:Min(1) @field:Max(60) val timeoutSeconds: Int = 20,
) {
    @AssertTrue(message = "GEMINI_API_KEY is required when MOCK_MODE=false")
    fun isApiKeyConfigured(): Boolean = mockMode || apiKey.isNotBlank()
}

enum class MockScenario { VALID, MALFORMED, NONSENSICAL }
