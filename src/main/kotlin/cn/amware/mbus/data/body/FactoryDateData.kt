package cn.amware.mbus.data.body

import cn.amware.mbus.data.Bcd40
import cn.amware.mbus.data.MeterDataBody
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * # “出厂日期”数据
 */
data class FactoryDateData(
		var factoryDate: Bcd40 = Bcd40()
) : MeterDataBody() {

	@Throws(IOException::class)
	override fun readFromBytesStream(inputStream: ByteArrayInputStream) {
		factoryDate.readFromBytesStream(inputStream)
	}

	@Throws(IOException::class)
	override fun writeToBytesStream(outputStream: ByteArrayOutputStream) {
		factoryDate.writeToBytesStream(outputStream)
	}

}
