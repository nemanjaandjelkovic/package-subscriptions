package com.dcs.oiremote

import com.dcs.datasource.exception.DataSourceException
import com.digitalchargingsolutions.middleware.oiapiclient.datasource.service.OIServiceRemoteDataSource
import com.digitalchargingsolutions.middleware.oiapiclient.model.filter.ServiceFilter
import com.digitalchargingsolutions.middleware.oiapiclient.model.response.Service
import kotlinx.coroutines.*
import org.springframework.stereotype.Component

@Component
class OIClientService(private var client: OIServiceRemoteDataSource) {

    //TODO Prebaciti u Remote Data Source i izbrisati ovu klasu
    fun getServices(filter: ServiceFilter): Result<List<Service>> {
        val services = mutableListOf<Service>()
        runCatching {
            runBlocking {
                val firstPage = client.getServices(filter)
                val totalPages = firstPage.pagination.totalPages
                services.addAll(firstPage.data.toMutableList())
                val jobs = (2..totalPages).map {
                    CoroutineScope(Dispatchers.IO).launch {
                        val filterWithNewPage = filter.toBuilder().page(it.toInt()).build()
                        val data = client.getServices(filterWithNewPage)
                        synchronized(services) {
                            services.addAll(data.data)
                        }
                    }
                }
                jobs.joinAll()
            }
            return Result.success(services)
        }.onFailure { return Result.failure(it) }
        return Result.failure(DataSourceException())
    }
}