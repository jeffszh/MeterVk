package cn.amware.mbus.data.body

import cn.amware.mbus.data.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

data class FlowData(
		var sumOfFlow: Bcd42 = Bcd42(),        // 累计流量
		var unit1: Units.Streamable = Units.Streamable(),
		var negSumOfFlow: Bcd42 = Bcd42(),
		var unit2: Units.Streamable = Units.Streamable(),
		var realTimeClock: Bcd70 = Bcd70(),
		var bitField: HexData1 = HexData1(),
		var reserved: HexData1 = HexData1()
) : MeterDataBody() {

	var unit1Value: Units
		get() = unit1.value
		set(v) {
			unit1.value = v
		}
	var unit2Value: Units
		get() = unit2.value
		set(v) {
			unit2.value = v
		}

	private val streamProperties = arrayOf(
			FlowData::sumOfFlow,
			FlowData::unit1,
			FlowData::negSumOfFlow,
			FlowData::unit2,
			FlowData::realTimeClock,
			FlowData::bitField,
			FlowData::reserved
	)

	@Throws(IOException::class)
	override fun readFromBytesStream(inputStream: ByteArrayInputStream) {
//		inputStream rd sumOfFlow rd unit1 rd negSumOfFlow rd unit2 rd
//				realTimeClock rd bitField rd reserved

		// 这是Kotlin很高级的特性！
		streamProperties.forEach {
			// 因为有it.set，甚至还有办法给枚举赋值，不需要专门弄一个Units.Streamable
			it.get(this).readFromBytesStream(inputStream)
		}
	}

	@Throws(IOException::class)
	override fun writeToBytesStream(outputStream: ByteArrayOutputStream) {
		streamProperties.forEach {
			it.get(this).writeToBytesStream(outputStream)
		}
//		outputStream wr sumOfFlow wr unit1 wr negSumOfFlow wr unit2 wr
//				realTimeClock wr bitField wr reserved
	}

}
