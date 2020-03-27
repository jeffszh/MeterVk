package cn.amware.mbus.data.body

import cn.amware.mbus.data.MeterDataBody
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * # 运行模式
 */
data class WorkModeData(
		var workMode: WorkMode = WorkMode.INVALID
) : MeterDataBody() {

	@Throws(IOException::class)
	override fun readFromBytesStream(inputStream: ByteArrayInputStream) {
		val bytes = ByteArray(2)
		if (inputStream.read(bytes) != bytes.size) {
			throw IOException("读数据出错！")
		}
		workMode = WorkMode.values().firstOrNull {
			it.d0 == bytes[0] && it.d1 == bytes[1]
		} ?: WorkMode.INVALID
	}

	@Throws(IOException::class)
	override fun writeToBytesStream(outputStream: ByteArrayOutputStream) {
		with(workMode) {
			outputStream.write(byteArrayOf(d0, d1))
		}
	}

	@Suppress("unused")
	enum class WorkMode(val d0: Byte, val d1: Byte, val text: String) {
		INVALID(0, 0, "非法值"),
		OUT_FACTORY_MODE(0x33, 0x77, "退出工厂模式"),
		IN_FACTORY_MODE(0x22, 0x88.toByte(), "进入工厂模式");

		override fun toString() = text
	}

}
