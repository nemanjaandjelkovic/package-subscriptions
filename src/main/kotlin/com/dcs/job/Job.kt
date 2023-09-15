package com.dcs.job

import com.dcs.datasource.DataSource
import com.dcs.messaging.STATUS_BOOKED
import com.dcs.messaging.STATUS_EXPIRED
import com.dcs.messaging.STATUS_UPDATED
import com.dcs.messaging.sendBulk
import com.digitalchargingsolutions.middleware.oiapiclient.model.response.Service
import com.digitalchargingsolutions.middleware.servicebus.client.sender.SenderProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Component
class Job {

    @Autowired
    private lateinit var dataSource: DataSource

    @Autowired
    private lateinit var sender: SenderProvider<Service>

    @Value("\${package-events.job.schedule-time}")
    private var jobTime: Int = 0

    @Scheduled(fixedRateString = "\${package-events.job.schedule-time}")
    fun getBookedSubscription() {
        val data = dataSource.getBookedSubscription()
        data.onSuccess {
            sender.sendBulk(it, STATUS_BOOKED)
        }.onFailure {
            println(it.message)
        }
    }

    @Scheduled(fixedRateString = "\${package-events.job.schedule-time}")
    fun getExpiredSubscription() {
        val data = dataSource.getExpiredSubscription()
        data.onSuccess {
            sender.sendBulk(it, STATUS_EXPIRED)
        }.onFailure {
            println(it.message)
        }
    }

    @Scheduled(fixedRateString = "\${package-events.job.schedule-time}")
    fun getUpdatedSubscription() {
        val data = dataSource.getUpdatedSubscription(getLastActiveJobTime())
        data.onSuccess {
            sender.sendBulk(it, STATUS_UPDATED)
        }.onFailure {
            println(it.message)
        }
    }

    fun getLastActiveJobTime(): Long {
        val currentTimeInEpoch = OffsetDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli()
        return currentTimeInEpoch - jobTime
    }
}