package com.amware.meterkit.service

import gnu.io.CommPortIdentifier
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/serial-port")
class SerialPortService {

	private val commPortIdentifierMap = sortedMapOf<String, CommPortIdentifier>()
	private val commPortStatusMap = sortedMapOf<String, Boolean>()

	@RequestMapping("", method = [RequestMethod.GET], produces = ["application/json"])
	fun enumSerialPorts(): Map<String, Boolean> {
		commPortIdentifierMap.clear()
		@Suppress("UNCHECKED_CAST")
		val portIdentifiers: Enumeration<CommPortIdentifier> =
				CommPortIdentifier.getPortIdentifiers() as Enumeration<CommPortIdentifier>
		while (portIdentifiers.hasMoreElements()) {
			val id = portIdentifiers.nextElement()
			if (id.portType == CommPortIdentifier.PORT_SERIAL) {
				commPortIdentifierMap[id.name] = id
			}
		}

		commPortStatusMap.clear()
		commPortIdentifierMap.forEach { (portName, _) ->
			commPortStatusMap[portName] = false
		}

		// 保證沒有重複的active
		val firstActiveKey = commPortStatusMap.asIterable().find {
			it.value
		}?.key
		firstActiveKey?.also { activeKey ->
			commPortStatusMap.keys.forEach { key ->
				if (key != activeKey) {
					commPortStatusMap[key] = false
				}
			}
		}

		return commPortStatusMap
	}

}
