package cn.amware.mbus.data.body

//import cn.amware.app.BitwiseAgent
import cn.amware.mbus.data.MeterDataBody
import cn.amware.mbus.data.MeterDataHead
import cn.amware.mbus.data.MeterPacket
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * # 阀控命令 / 阀控状态
 * 这里比较特殊，为方便处理，将阀控命令和阀控状态用同一个类来表示。
 * 因为原先做好的机制是根据[MeterDataHead.dataTag]来决定[MeterDataBody]的具体类型，所以很难将其分开。
 * 此外，目前的机制，数据包各部分的读写是嵌套关系并互相独立（尽量减少耦合），
 * 因此在读入[MeterDataBody]的过程中，已无法获知前面读取的[MeterPacket.ctrlCode]的值，
 * 故此权宜用数据长度作为判断，好在目前并无例外情况。
 */
data class ValveCtrlData(
		var command0Status1: Boolean = false,
		var off0On1: Byte = 0,
		var valveStatus: ValveStatus = ValveStatus.Unknown,
		var errorCode: Byte = 0
) : MeterDataBody() {

//	var errOpenValveTimeout by BitwiseAgent(ValveCtrlData::errorCode, 0x01)
//	var errCloseValveTimeout by BitwiseAgent(ValveCtrlData::errorCode, 0x02)

	@Throws(IOException::class)
	private fun readOneByteFromStream(inputStream: ByteArrayInputStream): Byte =
			inputStream.read().also {
				if (it < 0) {
					throw IOException("读数据出错！")
				}
			}.toByte()

	@Throws(IOException::class)
	override fun readFromBytesStream(inputStream: ByteArrayInputStream) {
		when (inputStream.available()) {
			1 -> {
				command0Status1 = false
				off0On1 = readOneByteFromStream(inputStream)
			}
			2 -> {
				command0Status1 = true
				valveStatus = ValveStatus.findByCode(readOneByteFromStream(inputStream))
				errorCode = readOneByteFromStream(inputStream)
			}
			else -> throw IOException("阀控数据包长度错误！")
		}
	}

	@Throws(IOException::class)
	override fun writeToBytesStream(outputStream: ByteArrayOutputStream) {
		if (command0Status1) {
			outputStream.write(valveStatus.code.toInt())
			outputStream.write(errorCode.toInt())
		} else {
			outputStream.write(off0On1.toInt())
		}
	}

	@Suppress("unused")
	enum class ValveStatus(val code: Byte, val text: String) {
		Unknown(-1, "未知"),
		NotSupport(0x00, "水表不支持阀控"),
		Opened(0x01, "已开"),
		Opening(0x02, "正在开"),
		Closing(0x03, "正在关"),
		Closed(0x04, "已关"),
		Fault(0x05, "故障");

		override fun toString() = text

		companion object {
			fun findByCode(code: Byte): ValveStatus = values().find {
				it.code == code
			} ?: Unknown
		}
	}

}
