package com.wallapoptest.listing_assistant.model

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Digits
import java.math.BigDecimal

data class PriceRange(
    @field:DecimalMin("0.01")
    @field:Digits(integer = 8, fraction = 2)
    val min: BigDecimal,
    @field:DecimalMin("0.01")
    @field:Digits(integer = 8, fraction = 2)
    val max: BigDecimal,
)
