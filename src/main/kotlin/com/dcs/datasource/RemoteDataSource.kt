package com.dcs.datasource

import com.dcs.datasource.exception.DataSourceException
import com.dcs.oiremote.FilterType
import com.dcs.oiremote.OIClientService
import com.digitalchargingsolutions.middleware.oiapiclient.datasource.service.OIServiceRemoteDataSource
import com.digitalchargingsolutions.middleware.oiapiclient.model.filter.ServiceFilter
import com.digitalchargingsolutions.middleware.oiapiclient.model.response.Service
import java.time.OffsetDateTime
import java.time.ZoneOffset

class RemoteDataSource(private val client: OIClientService) : DataSource {
    companion object{
        private const val ASSET_DETAILS = "assetDetails"
        private const val LT = "lt"
        private const val GT = "gt"
    }
    override fun getBookedSubscription(): Result<List<Service>> {
        val filter = ServiceFilter.builder()
            .code(FilterType.CODE.value)
            .assetState(FilterType.ASSET_STATE_FOR_BOOKED_SERVICE.value)
            .expand(ASSET_DETAILS)
            .build()
        runCatching {
            return Result.success(client.getServices(filter))
        }.onFailure {
            return Result.failure(DataSourceException(it.message))
        }
        return Result.failure(DataSourceException())
    }

    override fun getExpiredSubscription(): Result<List<Service>> {
        val currentTimeInEpoch = OffsetDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli()
        val filter = ServiceFilter.builder()
            .code(FilterType.CODE.value)
            .validTo("$LT(${currentTimeInEpoch})")
            .build()
        runCatching {
            return Result.success(client.getServices(filter))
        }.onFailure {
            return Result.failure(DataSourceException(it.message))
        }
        return Result.failure(DataSourceException())
    }

    override fun getUpdatedSubscription(timeLastJobActive: Long): Result<List<Service>> {
        val filter = ServiceFilter.builder()
            .code(FilterType.CODE.value)
            .updated("$GT(${timeLastJobActive})")
            .created("$LT(${timeLastJobActive})")
            .build()
        runCatching {
            return Result.success(client.getServices(filter))
        }.onFailure {
            return Result.failure(DataSourceException(it.message))
        }
        return Result.failure(DataSourceException())
    }
}