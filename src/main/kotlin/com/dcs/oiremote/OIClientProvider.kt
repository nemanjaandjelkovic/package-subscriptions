package com.dcs.oiremote

import com.digitalchargingsolutions.middleware.oiapiclient.datasource.service.OIServiceRemoteDataSource
import com.digitalchargingsolutions.middleware.oiapiclient.datasource.service.OIServiceRestRemoteDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OIClientProvider {
    @Bean
    fun remoteDataSource(): OIServiceRemoteDataSource {
        return OIServiceRestRemoteDataSource()
    }
}