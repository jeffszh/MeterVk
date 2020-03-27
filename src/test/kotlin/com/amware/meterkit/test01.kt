package com.amware.meterkit

import cn.amware.utils.DataUtils
import com.amware.meterkit.mbus.SerialPortMan

fun main() {
	val data = DataUtils.hexStrToBytes("68 10 AA AA AA AA AA AA AA 01 03 1F 90 01 D2 16")
	SerialPortMan.openSerialPort("COM6", 2400, 8, 1, 2)
	try {
		val recData = SerialPortMan.sendAndReceive(data)
		println(DataUtils.bytesToHexStr(*recData))
	} finally {
		println("正在关闭串口")
		SerialPortMan.closeSerialPort()
		println("串口已关闭")
	}
}
