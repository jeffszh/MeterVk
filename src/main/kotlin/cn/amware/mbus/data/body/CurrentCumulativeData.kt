package cn.amware.mbus.data.body

import cn.amware.mbus.data.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import kotlin.reflect.KMutableProperty1

/**
 * # 当前累积数据
 */
data class CurrentCumulativeData(
		var sumOfCooling: Bcd50 = Bcd50(),    // 累计冷量
		var unit1: Units = Units.Unknown,
		var sumOfHeat: Bcd50 = Bcd50(),       // 累计热量
		var unit2: Units = Units.Unknown,
		var sumOfFlow: Bcd42 = Bcd42(),       // 累积流量
		var unit3: Units = Units.Unknown
) : MeterDataBody() {

	private val streamProperties = arrayOf(
			CurrentCumulativeData::sumOfCooling,
			CurrentCumulativeData::unit1,
			CurrentCumulativeData::sumOfHeat,
			CurrentCumulativeData::unit2,
			CurrentCumulativeData::sumOfFlow,
			CurrentCumulativeData::unit3
	)

	@Throws(IOException::class)
	override fun readFromBytesStream(inputStream: ByteArrayInputStream) {
		streamProperties.forEach { prop ->
			prop.get(this).also { field ->
				when (field) {
					is BytesStreamable -> field.readFromBytesStream(inputStream)
					is Units -> {
						@Suppress("UNCHECKED_CAST")
						prop as KMutableProperty1<CurrentCumulativeData, Units>
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
