package com.dcs.messaging

import com.digitalchargingsolutions.middleware.oiapiclient.model.response.Service
import com.digitalchargingsolutions.middleware.servicebus.client.sender.SenderClient
import com.digitalchargingsolutions.middleware.servicebus.client.sender.SenderProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SenderConfiguration {
    @Value("\${package-events.service-bus-clients.sender}")
    private lateinit var configurationName: String

    @Bean(destroyMethod = "close")
    fun initSenderClient(): SenderProvider<Service> {
        return SenderClient(configurationName = configurationName)
    }
}