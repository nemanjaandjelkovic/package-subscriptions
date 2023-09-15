package com.dcs.`interface`

import com.digitalchargingsolutions.middleware.oiapiclient.model.filter.ServiceFilter
import com.digitalchargingsolutions.middleware.oiapiclient.model.response.Service
import com.digitalchargingsolutions.middleware.servicebus.client.sender.SenderClient
import com.digitalchargingsolutions.middleware.servicebus.client.sender.SenderProvider
import org.springframework.stereotype.Component

@Component
interface GetSubscription {

    fun getBookedSubscription()

    fun sendToServiceBus(data : MutableList<Service>, properties: LinkedHashMap<String, String> = linkedMapOf()){
        val sender: SenderProvider<Service> = SenderClient<Service>(
            configurationName = "internal",
            fileName = "azure.yaml",
        )
        data.forEach {
            sender.sendMessage(it, applicationProperties = properties)
        }
    }

    fun getExpiredSubscription()

    fun getUpdatedSubscription()

}