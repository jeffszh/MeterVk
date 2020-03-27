package cn.amware.mbus.data.body

import cn.amware.mbus.data.HexData7
import cn.amware.mbus.data.MeterDataBody
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * # “表号/地址”数据
 */
data class MeterNumAddressData(
		var address: HexData7 = HexData7()
) : MeterDataBody() {

	@Throws(IOException::class)
	override fun readFromBytesStream(inputStream: ByteArrayInputStream) {
		address.readFromBytesStream(inputStream)
	}

	@Throws(IOException::class)
	override fun writeToBytesStream(outputStream: ByteArrayOutputStream) {
		address.writeToBytesStream(outputStream)
	}

}

// 原先的地址是： 00 01 00 00 00 11 11
