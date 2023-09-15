package com.dcs.datasource

import com.dcs.oiremote.OIClientService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DataSourceConfiguration {

    @Bean
    @ConditionalOnProperty(name = ["package-events.datasource-type"], havingValue = "local")
    fun createLocalDataSource(): DataSource {
        return LocalDataSource()
    }

    @Bean
    @ConditionalOnProperty(name = ["package-events.datasource-type"], havingValue = "remote")
    fun createRemoteDataSource(client: OIClientService): DataSource {
        return RemoteDataSource(client)
    }
}