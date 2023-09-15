package com.dcs.oiremote

import com.digitalchargingsolutions.middleware.oiapiclient.datasource.service.OIServiceRemoteDataSource
import com.digitalchargingsolutions.middleware.oiapiclient.model.filter.ServiceFilter
import com.digitalchargingsolutions.middleware.oiapiclient.model.response.Service
import kotlinx.coroutines.*
import org.springframework.stereotype.Component

@Component
class OIClientService(private var client: OIServiceRemoteDataSource) {

    fun getServices(filter: ServiceFilter): List<Service> {
        val services = mutableListOf<Service>()
        runBlocking {
            //TODO  HVATA IZUZETAK AKO JE STRANICA PRAZNA
            val firstPage = client.getServices(filter)
            val totalPages = firstPage.pagination.totalPages
            services.addAll(firstPage.data.toMutableList())
            val jobs = (2..totalPages).map {
                CoroutineScope(Dispatchers.IO).launch {
                    //TODO SINHRONIZUJE
                    filter.page = it.toInt()
                    //TODO HVATA IZUZETAK AKO JE STRANICA PRAZNA
                    val data = client.getServices(filter)
                    synchronized(services) {
                        services.addAll(data.data)
                    }
                }
            }
            jobs.joinAll()
        }
        return services
    }
}