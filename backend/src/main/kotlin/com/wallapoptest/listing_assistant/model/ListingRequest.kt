package com.wallapoptest.listing_assistant.model

import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import jakarta.validation.Valid

data class ListingRequest(
    val title: String,
    @param:JsonSetter(contentNulls = Nulls.FAIL)
    val tags: List<String>,
    @field:Valid
    val priceRange: PriceRange,
)
