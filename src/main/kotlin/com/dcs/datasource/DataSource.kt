package com.dcs.datasource

import com.digitalchargingsolutions.middleware.oiapiclient.model.response.Service

interface DataSource {
    fun getBookedSubscription(): Result<List<Service>>

    fun getExpiredSubscription(): Result<List<Service>>

    fun getUpdatedSubscription(timeLastJobActive: Long): Result<List<Service>>
}