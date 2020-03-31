package com.amware.meterkit

import cn.amware.mbus.data.MeterDataType
import cn.amware.mbus.data.builder.MeterPacketBuilder
import com.amware.meterkit.mbus.SerialPortMan

fun main() {
	SerialPortMan.openSerialPort("COM6", 2400, 8, 1, 2)
	try {
		val address = "AA AA AA AA AA AA AA"
		val meterPacket = MeterPacketBuilder.buildReadNormalDataPacket(
				address, MeterDataType.FLOW_DATA)
		meterPacket.ctrlCode = 0x01

		val result = SerialPortMan.sendAndReceive(meterPacket)
		println("result count = ${result.size}")
		result.forEach {
			println(it)
		}
	} finally {
		println("正在关闭串口")
		SerialPortMan.closeSerialPort()
		println("串口已关闭")
	}
}
