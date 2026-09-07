package com.wallapoptest.listing_assistant.error

class InvalidSuggestionException : RuntimeException("Invalid model response.")
class ProviderUnavailableException : RuntimeException("Suggestion provider unavailable.")
class ProviderTimeoutException : RuntimeException("Suggestion provider timed out.")
