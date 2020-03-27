package cn.amware.mbus.data.body

import cn.amware.mbus.data.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import kotlin.reflect.KMutableProperty1

data class PreciseFlowData(
		var sumOfCooling: Bcd54 = Bcd54(),    // 累计冷量
		var unit1: Units = Units.Unknown,
		var sumOfHeat: Bcd54 = Bcd54(),       // 累计热量
		var unit2: Units = Units.Unknown,
		var instantPower: Bcd42 = Bcd42(),    // 瞬时功率
		var unit3: Units = Units.Unknown,
		var instantFlow: Bcd42 = Bcd42(),     // 瞬时流量
		var unit4: Units = Units.Unknown,
		var sumOfFlow: Bcd63 = Bcd63(),       // 累计流量
		var unit5: Units = Units.Unknown
) : MeterDataBody() {

	private val streamProperties = arrayOf(
			PreciseFlowData::sumOfCooling,
			PreciseFlowData::unit1,
			PreciseFlowData::sumOfHeat,
			PreciseFlowData::unit2,
			PreciseFlowData::instantPower,
			PreciseFlowData::unit3,
			PreciseFlowData::instantFlow,
			PreciseFlowData::unit4,
			PreciseFlowData::sumOfFlow,
			PreciseFlowData::unit5
	)

	@Throws(IOException::class)
	override fun readFromBytesStream(inputStream: ByteArrayInputStream) {
		streamProperties.forEach { prop ->
			prop.get(this).also { field ->
				when (field) {
					is BytesStreamable -> field.readFromBytesStream(inputStream)
					is Units -> {
						@Suppress("UNCHECKED_CAST")
						prop as KMutableProperty1<PreciseFlowData, Units>
						prop.set(this, Units.fromNumber(inputStream.read().also {
							if (it < 0) throw IOException("读数据出错！")
						}))
					}
				}
			}
		}
	}

	@Throws(IOException::class)
	override fun writeToBytesStream(outputStream: ByteArrayOutputStream) {
		streamProperties.forEach { kProp ->
			kProp.get(this).also { fieldValue ->
				when (fieldValue) {
					is BytesStreamable -> fieldValue.writeToBytesStream(outputStream)
					is Units -> outputStream.write(fieldValue.code.toInt())
				}
			}
		}
	}

}
