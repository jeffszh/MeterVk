package cn.amware.mbus.data

import cn.amware.mbus.data.body.*
import cn.amware.utils.DataUtils
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.annotation.JSONField
import com.alibaba.fastjson.serializer.SerializerFeature
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import kotlin.math.min
import kotlin.math.roundToLong

interface BytesStreamable {

	@Throws(IOException::class)
	fun writeToBytesStream(outputStream: ByteArrayOutputStream)

	@Throws(IOException::class)
	fun readFromBytesStream(inputStream: ByteArrayInputStream)

}

interface HasBytes : BytesStreamable {
	val bytes: ByteArray

	@Throws(IOException::class)
	override fun writeToBytesStream(outputStream: ByteArrayOutputStream) {
		outputStream.write(bytes)
	}

	@Throws(IOException::class)
	override fun readFromBytesStream(inputStream: ByteArrayInputStream) {
		if (inputStream.read(bytes) != bytes.size) {
			throw IOException("读数据出错！")
		}
	}

}

@Throws(IOException::class)
infix fun ByteArrayOutputStream.wr(item: BytesStreamable): ByteArrayOutputStream {
	item.writeToBytesStream(this)
	return this
}

@Throws(IOException::class)
infix fun ByteArrayInputStream.rd(item: BytesStreamable): ByteArrayInputStream {
	item.readFromBytesStream(this)
	return this
}

/**
 * 十六进制数据
 * 用于存贮16进制形式的数据，可与16进制字符串双向转换。
 * @param size 数据长度，字节数
 */
open class HexData(size: Int) : HasBytes {

	@JSONField(serialize = false)
	override val bytes = ByteArray(size)

	var asHex: String
		get() = DataUtils.bytesToHexStr(*bytes)
		set(asHex) {
			val bytes = DataUtils.hexStrToBytes(asHex)
			System.arraycopy(bytes, 0, this.bytes, 0, min(this.bytes.size, bytes.size))
		}

	override fun toString(): String = "HexData($asHex)"

}

class HexData1 : HexData(1)
class HexData2 : HexData(2)
class HexData4 : HexData(4)
class HexData7 : HexData(7)

class HexData8 : HexData(8)
class HexData16 : HexData(16)

/**
 * BCD数据
 * @param size 数据长度，字节数
 * @param fraction 小数位数，只用于显示
 */
open class BcdData(size: Int, @JSONField(serialize = false) val fraction: Int) : HexData(size) {

	var value: Long
		get() = DataUtils.bcdToLong(*bytes)
		set(value) {
			val buffer = DataUtils.longToBcd(value, bytes.size)
			System.arraycopy(buffer, 0, bytes, 0, bytes.size)
		}

	var asString: String
		get() = toString()
		set(string) {
			value = run {
				var doubleValue = string.toDouble()
				repeat(fraction) {
					doubleValue *= 10
				}
				doubleValue.roundToLong()
			}
		}

	override fun toString(): String {
		// 先检查是否合法的BCD码
		if (bytes.filterIndexed { i, b ->
					!b.isBcd(i == bytes.size - 1)
				}.any()) {
			return "非法BCD数值：${super.toString()}"
		}

		if (fraction <= 0) {
			return "$value"
		}
		var factor = 1
		repeat(fraction) {
			factor *= 10
		}
		return "${value.toDouble() / factor}"
	}
}

fun Byte.isBcd(allowNegative: Boolean = false): Boolean = this.toInt().let {
	(it and 0x0F in 0..9) and when (it and 0xF0) {
		in 0..0x90 -> true
		0xF0 -> allowNegative
		else -> false
	}
}

class Bcd11 : BcdData(1, 1)
class Bcd20 : BcdData(2, 0)
class Bcd21 : BcdData(2, 1)
class Bcd22 : BcdData(2, 2)
class Bcd23 : BcdData(2, 3)
class Bcd30 : BcdData(3, 0)
class Bcd40 : BcdData(4, 0)
class Bcd42 : BcdData(4, 2)
class Bcd50 : BcdData(5, 0)
class Bcd54 : BcdData(5, 4)
class Bcd63 : BcdData(6, 3)
class Bcd70 : BcdData(7, 0)

fun Any.toJson(): String = JSON.toJSONString(this, SerializerFeature.PrettyFormat)
fun Any.toJsonInOneLine(): String = JSON.toJSONString(this)
fun <T> Class<T>.fromJson(jsonStr: String): T = JSON.parseObject(jsonStr, this)

enum class MeterDataType(val tag: String, val clazz: Class<out MeterDataBody>) {

	FLOW_DATA("1F90", FlowData::class.java),
	PRECISE_FLOW_DATA("2F90", PreciseFlowData::class.java),
	DEBUGGING_UI_DATA("3F90", DebuggingUiData::class.java),
	CURRENT_CUMULATIVE_DATA("1ED1", CurrentCumulativeData::class.java),
	STANDARD_TIME_DATA("15A0", StandardTimeData::class.java),
	METER_NUM_ADDRESS_DATA("18A0", MeterNumAddressData::class.java),
	FACTORY_DATE_DATA("19A0", FactoryDateData::class.java),
	WORK_MODE_DATA("21A0", WorkModeData::class.java),
	FLOW_CORRECTION_DATA("22A0", FlowCorrectionData::class.java),
	USM_TEST_RESULT("7D45", UsmTestResult::class.java),
	VALVE_CTRL("1C46", ValveCtrlData::class.java),
	METER_PARAM("D357", MeterParams::class.java),
	LORA_PARAM("1EC7", LoraParams::class.java);

	companion object {
		fun getByTag(tag: String): MeterDataType? = values().find { it.tag == tag }
		fun getByBodyClass(clazz: Class<out MeterDataBody>): MeterDataType? = values().find { it.clazz == clazz }
	}

}

private var packetSeq = 0
fun generateNextSeq() = (++packetSeq).toByte()
