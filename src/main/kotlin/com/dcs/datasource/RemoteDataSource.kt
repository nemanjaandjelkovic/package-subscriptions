package com.dcs.datasource

import com.dcs.datasource.exception.DataSourceException
import com.dcs.oiremote.FilterType
import com.dcs.oiremote.OIClientService
import com.digitalchargingsolutions.middleware.oiapiclient.model.filter.ServiceFilter
import com.digitalchargingsolutions.middleware.oiapiclient.model.response.Service
import java.time.OffsetDateTime
import java.time.ZoneOffset

class RemoteDataSource(private val client: OIClientService) : DataSource {
    companion object {
        //TODO preimenovati u ono sto znaci
        private const val LE = "le"
        private const val GE = "ge"
    }

    // FILTER
    // Da bude vece ili jednako od pocetka dana ( 00:00 danas) do 23:59:59:9999
    override fun getBookedSubscriptions(): Result<List<Service>> {
        val filter = ServiceFilter.builder()
            //TODO Dodati valid from
            .code(FilterType.CODE.value)
            .assetState(FilterType.ASSET_STATE_FOR_BOOKED_SERVICE.value)
            .build()
        runCatching {
            return client.getServices(filter)
        }.onFailure {
            return Result.failure(DataSourceException(it.message))
        }
        return Result.failure(DataSourceException())
    }

    override fun getExpiredSubscription(): Result<List<Service>> {
        val currentTimeInEpoch = OffsetDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli()
        val filter = ServiceFilter.builder()
            //TODO FilterType prebaciti u kanstante unutar ove klase i izbrisati enum
            .code(FilterType.CODE.value)
            .validTo("$LE(${currentTimeInEpoch})")
            .build()
        runCatching {
            return client.getServices(filter)
        }.onFailure {
            return Result.failure(DataSourceException(it.message))
        }
        return Result.failure(DataSourceException())
    }

    override fun getUpdatedSubscription(timeLastJobActive: Long): Result<List<Service>> {
        val filter = ServiceFilter.builder()
            .code(FilterType.CODE.value)
            .updated("$GE(${timeLastJobActive})")
            .created("$LE(${timeLastJobActive})")
            .build()
        runCatching {
            return client.getServices(filter)
        }.onFailure {
            return Result.failure(DataSourceException(it.message))
        }
        return Result.failure(DataSourceException())
    }
}