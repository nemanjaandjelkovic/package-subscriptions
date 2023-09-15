package com.dcs.jobs.api

import com.dcs.enum.ServiceDataEnum.*
import com.dcs.`interface`.GetSubscription
import com.dcs.oi.OIClientProvider
import com.digitalchargingsolutions.middleware.oiapiclient.datasource.service.OIServiceRestRemoteDataSource
import com.digitalchargingsolutions.middleware.oiapiclient.model.filter.ServiceFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.*
import java.util.*

@Service
@Profile("API")
class SubscriptionApi : GetSubscription {

    @Autowired
    lateinit var client: OIClientProvider

    @Value("\${job.time}")
    private var jobTime: Int = 900000

    @Scheduled(fixedRateString = "\${job.time}")
    override fun getBookedSubscription() {
        val filter = ServiceFilter.builder()
            .code(CODE.value)
              .assetState(ASSET_STATE_FOR_BOOKED_SERVICE.value)
            .expand("assetDetails")
            .build()
        val totalPagesReponse = client.remoteDataSource().getServices(filter)
//        val data = getDataFromResponse(filter, totalPagesReponse)
//        println(data.size)
        val properties = linkedMapOf(Pair("status", "NEW"))
        println(client.remoteDataSource().getServices(filter))
        sendToServiceBus(client.remoteDataSource().getServices(filter) ,properties)
    }

    @Scheduled(fixedRateString = "\${job.time}")
    override fun getExpiredSubscription() {
        val currentTimeInEpoch = OffsetDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli()
        val filter = ServiceFilter.builder()
            .code(CODE.value)
            .validTo("lt(${currentTimeInEpoch})")
            .build()
//        val totalPagesReponse = client.getServices(filter).pagination.totalPages
//        val data = getDataFromResponse(filter, totalPagesReponse)
//        println(data.size)
        val properties = linkedMapOf(Pair("status", "EXPIRED"))
           sendToServiceBus(client.remoteDataSource().getServices(filter) ,properties)
    }

    @Scheduled(fixedRateString = "\${job.time}")
    override fun getUpdatedSubscription() {
        val currentTimeInEpoch = OffsetDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli()
        val timeLastJobActive = currentTimeInEpoch - jobTime
        val filter = ServiceFilter.builder()
            .code(CODE.value)
            .updated("gt(${timeLastJobActive})")
            .created("lt(${timeLastJobActive})")
            .build()
//        val data = getDataFromResponse(filter, client.getServices(filter).pagination.totalPages)
        val properties = linkedMapOf(Pair("status", "UPDATED"))
         sendToServiceBus(client.remoteDataSource().getServices(filter) ,properties)
    }


    fun getDataFromResponse(filter: ServiceFilter, totalPages: Long): MutableList<com.digitalchargingsolutions.middleware.oiapiclient.model.response.Service> {

        val numberOfThreads = 10
        val pagesPerThread = totalPages / numberOfThreads
        val remainingPages = totalPages % numberOfThreads

        val data = Collections.synchronizedList(mutableListOf<com.digitalchargingsolutions.middleware.oiapiclient.model.response.Service>())

        val threads = mutableListOf<Thread>()

        for (threadNumber in 1..numberOfThreads) {
            val startPage = (threadNumber - 1) * pagesPerThread + 1
            val endPage = threadNumber * pagesPerThread + if (threadNumber == numberOfThreads) remainingPages else 0

            val thread = Thread {
                for (i in startPage..endPage) {
                    filter.page = i.toInt()
//                    data.addAll(client.getServices(filter).data)
                }
            }
            threads.add(thread)
            thread.start()
        }

        threads.forEach { it.join() }

        return data
    }

}