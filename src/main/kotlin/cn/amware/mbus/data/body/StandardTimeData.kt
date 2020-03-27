package cn.amware.mbus.data.body

import cn.amware.mbus.data.Bcd70
import cn.amware.mbus.data.MeterDataBody
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

data class StandardTimeData(
		var standardTime: Bcd70 = Bcd70()
) : MeterDataBody() {

	@Throws(IOException::class)
	override fun readFromBytesStream(inputStream: ByteArrayInputStream) {
		standardTime.readFromBytesStream(inputStream)
	}

	@Throws(IOException::class)
	override fun writeToBytesStream(outputStream: ByteArrayOutputStream) {
		standardTime.writeToBytesStream(outputStream)
	}

}
