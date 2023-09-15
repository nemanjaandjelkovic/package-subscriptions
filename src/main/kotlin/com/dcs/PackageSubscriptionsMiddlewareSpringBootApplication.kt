package com.dcs

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class PackageSubscriptionsMiddlewareSpringBootApplication

fun main(args: Array<String>) {
    runApplication<PackageSubscriptionsMiddlewareSpringBootApplication>(*args)
}
