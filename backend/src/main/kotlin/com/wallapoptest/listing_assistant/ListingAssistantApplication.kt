package com.wallapoptest.listing_assistant

import com.wallapoptest.listing_assistant.aiassistant.AssistantProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(AssistantProperties::class)
class ListingAssistantApplication

fun main(args: Array<String>) {
	runApplication<ListingAssistantApplication>(*args)
}
