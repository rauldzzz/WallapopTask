package com.wallapoptest.listing_assistant.aiassistant

import com.google.genai.Client
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.context.annotation.Configuration

class AssistantConfigurationTests {
    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(AssistantProperties::class)
    class PropertiesConfiguration

    private val runner = ApplicationContextRunner()
        .withUserConfiguration(PropertiesConfiguration::class.java, AssistantConfiguration::class.java)

    @ParameterizedTest
    @ValueSource(strings = ["", "   "])
    fun `real mode rejects missing or blank API key`(key: String) {
        runner.withPropertyValues("app.ai.mock-mode=false", "app.ai.api-key=$key").run {
            assertThat(it).hasFailed()
            assertThat(it.startupFailure).hasStackTraceContaining("GEMINI_API_KEY")
        }
    }

    @Test
    fun `mock creates no Google client`() {
        runner.withPropertyValues("app.ai.mock-mode=true").run {
            assertThat(it).hasNotFailed().doesNotHaveBean(Client::class.java)
        }
    }

    @Test
    fun `real mode builds with API key alone without making a request`() {
        runner.withPropertyValues("app.ai.mock-mode=false", "app.ai.api-key=test-only").run {
            assertThat(it).hasNotFailed().hasSingleBean(Client::class.java)
        }
    }
}
