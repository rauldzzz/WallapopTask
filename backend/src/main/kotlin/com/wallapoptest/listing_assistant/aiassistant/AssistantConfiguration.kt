package com.wallapoptest.listing_assistant.aiassistant

import com.google.genai.Client
import com.google.genai.types.HttpOptions
import com.google.genai.types.HttpRetryOptions
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.google.genai.GoogleGenAiChatModel
import org.springframework.ai.google.genai.GoogleGenAiChatOptions
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.retry.support.RetryTemplate

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "app.ai", name = ["mock-mode"], havingValue = "false")
class AssistantConfiguration {
    @Bean(destroyMethod = "close")
    fun geminiClient(properties: AssistantProperties): Client = Client.builder()
        .vertexAI(false)
        .apiKey(properties.apiKey)
        .httpOptions(HttpOptions.builder()
            .timeout(properties.timeoutSeconds * 1000)
            .retryOptions(HttpRetryOptions.builder().attempts(1).build())
            .build())
        .build()

    @Bean
    fun chatModel(client: Client, properties: AssistantProperties): ChatModel =
        GoogleGenAiChatModel.builder()
            .genAiClient(client)
            .defaultOptions(GoogleGenAiChatOptions.builder()
                .model(properties.model)
                .temperature(0.2)
                .maxOutputTokens(2048)
                .responseMimeType("application/json")
                .build())
            .retryTemplate(RetryTemplate.builder().maxAttempts(1).build())
            .build()

    @Bean
    fun chatClient(chatModel: ChatModel): ChatClient = ChatClient.create(chatModel)
}
