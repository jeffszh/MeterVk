package cn.amware.mbus.data.body

import cn.amware.mbus.data.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

data class MeterParams(
		var sampleTime: Bcd30 = Bcd30(),
		var zeroDrift: Bcd42 = Bcd42(),
		var unit1: Units.Streamable = Units.Streamable(),
		var pw1stThreshold: Bcd11 = Bcd11(),
		var tofUpperBound: Bcd42 = Bcd42(),
		var tofLowerBound: Bcd42 = Bcd42(),
		var tofMax: Bcd42 = Bcd42(),
		var initialFlow: Bcd42 = Bcd42(),
		var unit2: Units.Streamable = Units.Streamable(),
		var pipeHorizontalLength: Bcd42 = Bcd42(),
		var pipeVerticalLength: Bcd42 = Bcd42(),
		var pipeRadius: Bcd42 = Bcd42()
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

	@Throws(IOException::class)
	override fun readFromBytesStream(inputStream: ByteArrayInputStream) {
		inputStream rd sampleTime rd zeroDrift rd unit1 rd pw1stThreshold rd
				tofUpperBound rd tofLowerBound rd tofMax rd initialFlow rd unit2 rd
				pipeHorizontalLength rd pipeVerticalLength rd pipeRadius
	}

	@Throws(IOException::class)
	override fun writeToBytesStream(outputStream: ByteArrayOutputStream) {
		outputStream wr sampleTime wr zeroDrift wr unit1 wr pw1stThreshold wr
				tofUpperBound wr tofLowerBound wr tofMax wr initialFlow wr unit2 wr
				pipeHorizontalLength wr pipeVerticalLength wr pipeRadius
	}

}
