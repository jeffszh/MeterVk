package com.amware.meterkit

import com.alibaba.fastjson.JSON
import com.amware.meterkit.net.LoraPlatformClient

fun main() {
	val result = LoraPlatformClient.createDevice("amwares", "1111110000001234")
	println("-------------------------------")
	println(result)
	println(JSON.parseObject(result))
//	if (result != null) {
//		println(result.application_eui)
//		println(result.application_key)
//	}
	println("-------------------------------")
}
