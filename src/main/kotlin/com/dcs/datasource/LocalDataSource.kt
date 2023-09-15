package com.dcs.datasource

import com.dcs.oiremote.FilterType
import com.digitalchargingsolutions.middleware.oiapiclient.model.response.Service
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.time.OffsetDateTime
import java.time.ZoneOffset

class LocalDataSource : DataSource {

    companion object {
        private const val FILE_NAME = "data.json"
    }

    override fun getBookedSubscription(): Result<List<Service>> {
        return Result.success(getSubscriptionFromFile().filter { it.assetState == FilterType.ASSET_STATE_FOR_BOOKED_SERVICE.value && it.code == FilterType.CODE.value })
    }

    override fun getExpiredSubscription(): Result<List<Service>> {
        val currentTimeInEpoch = OffsetDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli()
        val data = getSubscriptionFromFile()
        return Result.success(data.filter { (it.validTo ?: currentTimeInEpoch.plus(1)) < currentTimeInEpoch })
    }

    override fun getUpdatedSubscription(timeLastJobActive: Long): Result<List<Service>> {
        return Result.success(getSubscriptionFromFile().filter { it.updated > timeLastJobActive && it.created < timeLastJobActive })
    }

    private fun getSubscriptionFromFile(): MutableList<Service> {
        val objectMapper = jacksonObjectMapper()
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        val inputStream = this::class.java.classLoader.getResourceAsStream(FILE_NAME)
        return objectMapper.readValue<MutableList<Service>>(inputStream!!)
    }
}