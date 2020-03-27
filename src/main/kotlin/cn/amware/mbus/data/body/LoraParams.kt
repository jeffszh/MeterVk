package cn.amware.mbus.data.body

import cn.amware.mbus.data.HexData16
import cn.amware.mbus.data.HexData8
import cn.amware.mbus.data.MeterDataBody
import cn.amware.mbus.data.rd
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

data class LoraParams(
		var devEui: HexData8 = HexData8(),
		var appEui: HexData8 = HexData8(),
		var appKey: HexData16 = HexData16()
) : MeterDataBody() {

	@Throws(IOException::class)
	override fun writeToBytesStream(outputStream: ByteArrayOutputStream) {
		outputStream.write(devEui.bytes.reversed().toByteArray())
		outputStream.write(appEui.bytes.reversed().toByteArray())
		outputStream.write(appKey.bytes.reversed().toByteArray())
	}

	override fun readFromBytesStream(inputStream: ByteArrayInputStream) {
		inputStream rd devEui rd appEui rd appKey
		devEui.bytes.reverse()
		appEui.bytes.reverse()
		appKey.bytes.reverse()
	}

}
