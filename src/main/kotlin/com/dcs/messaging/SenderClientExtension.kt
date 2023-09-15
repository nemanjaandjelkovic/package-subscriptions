package com.dcs.messaging

import com.digitalchargingsolutions.middleware.oiapiclient.model.response.Service
import com.digitalchargingsolutions.middleware.servicebus.client.sender.SenderProvider
import com.digitalchargingsolutions.middleware.servicebus.client.sender.message.SenderServiceBusMessage

fun SenderProvider<Service>.sendBulk(services: List<Service>, property: String) {
    val messages = services.map {
        SenderServiceBusMessage(it, applicationProperties = linkedMapOf(STATUS to property))
    }
    sendBulkMessages(messages)
}