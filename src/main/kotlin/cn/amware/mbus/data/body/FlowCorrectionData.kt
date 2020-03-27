package cn.amware.mbus.data.body

import cn.amware.mbus.data.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import kotlin.reflect.KMutableProperty1

/**
 * # 流量修正数据
 */
data class FlowCorrectionData(
		var d0: Byte = 0,      // 总是0
		// 以下是从大到小的4个流量修正点，带符号特殊BCD码，单位：%
		var correctionPoint1: Bcd21 = Bcd21(),
		var correctionPoint2: Bcd21 = Bcd21(),
		var correctionPoint3: Bcd21 = Bcd21(),
		var correctionPoint4: Bcd21 = Bcd21()
) : MeterDataBody() {

	private val streamProperties = arrayOf(
			FlowCorrectionData::d0,
			FlowCorrectionData::correctionPoint1,
			FlowCorrectionData::correctionPoint2,
			FlowCorrectionData::correctionPoint3,
			FlowCorrectionData::correctionPoint4
	)

	@Throws(IOException::class)
	override fun readFromBytesStream(inputStream: ByteArrayInputStream) {
		streamProperties.forEach { prop ->
			prop.get(this).also { field ->
				when (field) {
					is BytesStreamable -> field.readFromBytesStream(inputStream)
					is Byte -> {
						inputStream.read().also {
							if (it < 0) throw IOException("读数据出错！")
						}
						@Suppress("UNCHECKED_CAST")
						prop as KMutableProperty1<FlowCorrectionData, Byte>
						prop.set(this, 0)
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
					is Byte -> outputStream.write(0)
				}
			}
		}
	}

}
