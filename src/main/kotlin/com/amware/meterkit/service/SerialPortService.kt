package com.amware.meterkit.service

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/serial-port")
class SerialPortService {

	class SerialPortStatus(
			var name: String = "",
			var value: Boolean = false
	)

	@RequestMapping("", method = [RequestMethod.GET], produces = ["application/json"])
	fun enumSerialPorts(): List<SerialPortStatus> {
		return listOf(
				SerialPortStatus("COM1"),
				SerialPortStatus("COM2", true),
				SerialPortStatus("COM5"),
				SerialPortStatus("COM6")
		)
	}

}
