package com.wallapoptest.listing_assistant.aiassistant

fun interface ListingAssistant {
    fun suggest(description: String): String
}
