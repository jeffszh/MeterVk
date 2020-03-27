package cn.amware.mbus.data

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * <h1>水表数据</h1>
 */
data class MeterData(
		var head: MeterDataHead = MeterDataHead(),
		var body: MeterDataBody? = null
) : BytesStreamable {

	@Throws(IOException::class)
	override fun writeToBytesStream(outputStream: ByteArrayOutputStream) {
		outputStream wr head
		body?.let { outputStream wr it }
	}

	@Throws(IOException::class)
	override fun readFromBytesStream(inputStream: ByteArrayInputStream) {
		inputStream rd head
		body = if (inputStream.available() > 0) {
			val dataType = MeterDataType.getByTag(head.dataTag)
			dataType?.run {
				try {
					clazz.newInstance().also {
						inputStream rd it
					}
				} catch (e: InstantiationException) {
					throw IOException(e)
				} catch (e: IllegalAccessException) {
					throw IOException(e)
				}
			}
		} else {
			null
		}
	}

//	override fun toString(): String {
//		return toJson()
//	}

}
