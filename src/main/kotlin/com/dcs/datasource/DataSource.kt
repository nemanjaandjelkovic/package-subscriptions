package com.dcs.datasource

import com.digitalchargingsolutions.middleware.oiapiclient.model.response.Service
import org.springframework.stereotype.Component

interface DataSource {
    fun getBookedSubscription(): Result<List<Service>>

    fun getExpiredSubscription(): Result<List<Service>>

    fun getUpdatedSubscription(timeLastJobActive: Long): Result<List<Service>>
}