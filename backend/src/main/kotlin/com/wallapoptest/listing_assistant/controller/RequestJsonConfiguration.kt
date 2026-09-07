package com.wallapoptest.listing_assistant.controller

import com.fasterxml.jackson.databind.cfg.CoercionAction
import com.fasterxml.jackson.databind.cfg.CoercionInputShape
import com.fasterxml.jackson.databind.type.LogicalType
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class RequestJsonConfiguration {
    @Bean
    fun strictTextFields() = Jackson2ObjectMapperBuilderCustomizer { builder ->
        builder.postConfigurer { mapper ->
            listOf(CoercionInputShape.Integer, CoercionInputShape.Float, CoercionInputShape.Boolean)
                .forEach {
                    mapper.coercionConfigFor(LogicalType.Textual).setCoercion(it, CoercionAction.Fail)
                }
        }
    }
}
