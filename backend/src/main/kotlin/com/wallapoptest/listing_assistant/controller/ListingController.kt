package com.wallapoptest.listing_assistant.controller

import com.wallapoptest.listing_assistant.model.ListingRequest
import com.wallapoptest.listing_assistant.model.SuggestionRequest
import com.wallapoptest.listing_assistant.service.ListingService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/listings")
class ListingController(private val service: ListingService) {
    @PostMapping("/suggestions")
    fun suggest(@Valid @RequestBody request: SuggestionRequest): ListingRequest =
        service.suggest(request.description)
}
