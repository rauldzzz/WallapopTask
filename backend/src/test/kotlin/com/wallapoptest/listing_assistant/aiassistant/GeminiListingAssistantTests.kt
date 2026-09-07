package com.wallapoptest.listing_assistant.aiassistant

import com.wallapoptest.listing_assistant.error.InvalidSuggestionException
import com.wallapoptest.listing_assistant.error.ProviderTimeoutException
import com.wallapoptest.listing_assistant.error.ProviderUnavailableException
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.metadata.ChatGenerationMetadata
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.model.Generation
import org.springframework.ai.chat.prompt.Prompt
import java.net.SocketTimeoutException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class GeminiListingAssistantTests {
    @Test
    fun `Spring AI sends user data separately and returns the answer`() {
        val model = mock(ChatModel::class.java)
        val metadata = ChatGenerationMetadata.builder().finishReason("STOP").build()
        `when`(model.call(any(Prompt::class.java))).thenReturn(
            ChatResponse(listOf(Generation(AssistantMessage("answer"), metadata)))
        )
        assertEquals("answer", GeminiListingAssistant(ChatClient.create(model)).suggest("Chaqueta"))
        val prompt = ArgumentCaptor.forClass(Prompt::class.java)
        verify(model).call(prompt.capture())
        assertIs<SystemMessage>(prompt.value.instructions[0])
        assertIs<UserMessage>(prompt.value.instructions[1])
        assertEquals("Chaqueta", prompt.value.instructions[1].text)
    }

    @Test
    fun `rejects missing candidates`() {
        val model = mock(ChatModel::class.java)
        `when`(model.call(any(Prompt::class.java))).thenReturn(ChatResponse(emptyList()))
        assertFailsWith<InvalidSuggestionException> {
            GeminiListingAssistant(ChatClient.create(model)).suggest("Chaqueta")
        }
    }

    @Test
    fun `translates failures without leaking provider details`() {
        val model = mock(ChatModel::class.java)
        `when`(model.call(any(Prompt::class.java))).thenThrow(IllegalStateException("private details"))
        val error = assertFailsWith<ProviderUnavailableException> {
            GeminiListingAssistant(ChatClient.create(model)).suggest("Chaqueta")
        }
        assertEquals(null, error.cause)
    }

    @Test
    fun `translates SDK timeout`() {
        val model = mock(ChatModel::class.java)
        `when`(model.call(any(Prompt::class.java))).thenThrow(RuntimeException(SocketTimeoutException()))
        assertFailsWith<ProviderTimeoutException> {
            GeminiListingAssistant(ChatClient.create(model)).suggest("Chaqueta")
        }
    }
}
