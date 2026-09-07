package com.wallapoptest.listing_assistant.controller

import com.google.genai.Client
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.ai.chat.model.ChatModel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import kotlin.test.assertTrue

@SpringBootTest(properties = ["app.ai.mock-mode=true", "app.ai.api-key=", "app.ai.mock-scenario=VALID"])
@AutoConfigureMockMvc
class ListingControllerTests {
    @Autowired lateinit var mvc: MockMvc
    @Autowired lateinit var context: ApplicationContext

    @Test
    fun `mock works without Google client or credential`() {
        assertTrue(context.getBeansOfType(Client::class.java).isEmpty())
        assertTrue(context.getBeansOfType(ChatModel::class.java).isEmpty())
        mvc.post("/api/listings/suggestions") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"description":"Chaqueta vintage talla M"}"""
        }.andExpect {
            status { isOk() }
            jsonPath("$.title") { isString() }
            jsonPath("$.tags.length()") { value(4) }
            jsonPath("$.priceRange.min") { value(40) }
            jsonPath("$.priceRange.max") { value(50) }
            jsonPath("$.price") { doesNotExist() }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["{}", "{", """{"description":42}""", """{"description":null}""",
        """{"description":""}""", """{"description":"  "}""", """{"description":"a"}"""])
    fun `bad requests return safe errors`(body: String) {
        mvc.post("/api/listings/suggestions") {
            contentType = MediaType.APPLICATION_JSON
            content = body
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.code") { isString() }
            jsonPath("$.message") { isString() }
        }
    }
}
