package com.wallapoptest.listing_assistant.model

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class SuggestionRequest(
    @field:NotBlank
    @field:Size(min = 3, max = 2000)
    val description: String,
)
