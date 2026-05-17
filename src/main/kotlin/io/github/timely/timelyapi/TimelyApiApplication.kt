package io.github.timely.timelyapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class TimelyApiApplication

fun main(args: Array<String>) {
    runApplication<TimelyApiApplication>(*args)
}
