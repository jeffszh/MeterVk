package cn.amware.mbus.data

import cn.amware.utils.DataUtils

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

data class MeterDataHead(
		var dataId: HexData2 = HexData2(),
		var seq: Byte = 0
) : BytesStreamable {

	/**
	 * 根据[.dataId]生成数据标识。
	 * @return 数据标识
	 */
	val dataTag: String
		get() = DataUtils.noSpaceHexStr(dataId.asHex).toUpperCase()

	@Throws(IOException::class)
	override fun writeToBytesStream(outputStream: ByteArrayOutputStream) {
		outputStream.wr(dataId).write(seq.toInt())
	}

	@Throws(IOException::class)
	override fun readFromBytesStream(inputStream: ByteArrayInputStream) {
		if (inputStream.read(dataId.bytes) != 2) {
			throw IOException("数据不足！")
		}
		val seq = inputStream.read()
		if (seq < 0) {
			throw IOException("数据不足！")
		}
		this.seq = seq.toByte()
	}

}
