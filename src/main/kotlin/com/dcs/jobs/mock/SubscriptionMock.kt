package com.dcs.jobs.mock

import com.dcs.enum.ServiceDataEnum.*
import com.dcs.`interface`.GetSubscription
import com.digitalchargingsolutions.middleware.oiapiclient.model.response.Service
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import java.time.OffsetDateTime
import java.time.ZoneOffset

@org.springframework.stereotype.Service
@Profile("MOCK")
class SubscriptionMock : GetSubscription {

    @Value("\${job.time}")
    private var jobTime: Int = 900000

    @Scheduled(fixedRateString = "\${job.time}")
    override fun getBookedSubscription() {
        val data =
            getSubscriptionFromFile().filter { it.assetState == ASSET_STATE_FOR_BOOKED_SERVICE.value && it.code == CODE.value }.distinct().toMutableList()
        val properties = linkedMapOf(Pair("status","NEW"))
        sendToServiceBus(data, properties)
    }

    @Scheduled(fixedRateString = "\${job.time}")
    override fun getExpiredSubscription() {
        val currentTimeInEpoch = OffsetDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli()
        val data = getSubscriptionFromFile()
        val expiredSubscriptions = data.filter { (it.validTo ?: currentTimeInEpoch.plus(1)) < currentTimeInEpoch }.distinct().toMutableList()
        val properties = linkedMapOf(Pair("status","EXPIRED"))
        sendToServiceBus(expiredSubscriptions, properties)
    }

    @Scheduled(fixedRateString = "\${job.time}")
    override fun getUpdatedSubscription() {
        val currentTimeInEpoch = OffsetDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli()
        val timeLastJobActive = currentTimeInEpoch - jobTime
        val data = getSubscriptionFromFile()
        val updatedSubscriptions = data.filter { it.updated > timeLastJobActive && it.created < timeLastJobActive }.distinct().toMutableList()
        val properties = linkedMapOf(Pair("status","UPDATED"))
        sendToServiceBus(updatedSubscriptions, properties)
    }

    fun getSubscriptionFromFile(): MutableList<Service> {
        val objectMapper = jacksonObjectMapper()
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        val inputStream = this::class.java.classLoader.getResourceAsStream("data.json")
        return objectMapper.readValue<MutableList<Service>>(inputStream!!)
    }
}