package com.amware.meterkit.service

import com.amware.meterkit.mbus.SerialPortMan
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.lang.Exception

/**
 * # 串口相關服務
 * 包括：獲取串口列表、打開串口和關閉串口。
 */
@RestController
@RequestMapping("/serial-port")
class SerialPortService {

	/**
	 * # 獲取串口列表
	 * 此操作也可用來試驗串口驅動是否已安裝好。
	 * @return 串口列表
	 */
	@GetMapping("/list")
	fun enumSerialPorts(): List<String> {
		try {
			return SerialPortMan.findCommPorts()
		} catch (e: Exception) {
			throw CustomException(e.message ?: e.toString())
		}
	}

}
