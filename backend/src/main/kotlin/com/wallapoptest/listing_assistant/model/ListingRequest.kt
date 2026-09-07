package com.wallapoptest.listing_assistant.model

import jakarta.validation.constraints.Digits
import java.math.BigDecimal

data class ListingRequest(
    val title: String,
    val tags: List<String>,
    @field:Digits(integer = Int.MAX_VALUE, fraction = 2)
    val price: BigDecimal,
)
