package com.wallapoptest.listing_assistant

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(properties = ["app.ai.mock-mode=true"])
class ListingAssistantApplicationTests {

	@Test
	fun contextLoads() {
	}

}
