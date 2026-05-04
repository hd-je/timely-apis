package io.github.timely.timelyapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TimelyApiApplication

fun main(args: Array<String>) {
    runApplication<TimelyApiApplication>(*args)
}
