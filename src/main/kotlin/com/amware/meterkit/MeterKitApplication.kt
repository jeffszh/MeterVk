package com.amware.meterkit

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MeterKitApplication

fun main(args: Array<String>) {
	runApplication<MeterKitApplication>(*args)
}
