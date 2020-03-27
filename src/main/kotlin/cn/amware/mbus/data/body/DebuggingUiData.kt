package cn.amware.mbus.data.body

import cn.amware.mbus.data.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

data class DebuggingUiData(
		var programVersion: Bcd20 = Bcd20(),
		var supplyReturnFlag: HexData1 = HexData1(),
		var instrumentFactor: Bcd23 = Bcd23(),
		var lowFlowExcision: Bcd22 = Bcd22(),
		var flowCompensation1: Bcd21 = Bcd21(),
		var flowCompensation2: Bcd21 = Bcd21(),
		var flowCompensation3: Bcd21 = Bcd21(),
		var flowCompensation4: Bcd21 = Bcd21(),
		var supplyingTemperatureCompensation: Bcd22 = Bcd22(),
		var returningTemperatureCompensation: Bcd22 = Bcd22(),
		var introductionDate: Bcd30 = Bcd30(),
		var caliber: Bcd20 = Bcd20()
) : MeterDataBody() {

	private val streamProperties = arrayOf(
			DebuggingUiData::programVersion,
			DebuggingUiData::supplyReturnFlag,
			DebuggingUiData::instrumentFactor,
			DebuggingUiData::lowFlowExcision,
			DebuggingUiData::flowCompensation1,
			DebuggingUiData::flowCompensation2,
			DebuggingUiData::flowCompensation3,
			DebuggingUiData::flowCompensation4,
			DebuggingUiData::supplyingTemperatureCompensation,
			DebuggingUiData::returningTemperatureCompensation,
			DebuggingUiData::introductionDate,
			DebuggingUiData::caliber
	)

	@Throws(IOException::class)
	override fun readFromBytesStream(inputStream: ByteArrayInputStream) {
		streamProperties.forEach {
			it.get(this).readFromBytesStream(inputStream)
		}
	}

	@Throws(IOException::class)
	override fun writeToBytesStream(outputStream: ByteArrayOutputStream) {
		streamProperties.forEach {
			it.get(this).writeToBytesStream(outputStream)
		}
	}

}
