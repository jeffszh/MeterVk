package cn.amware.mbus.data

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

@Suppress("unused")
enum class Units(val code: Byte, val text: String) {

	WattHour(0x02, "Wh"),
	KiloWattHour(0x05, "KWh"),
	MegaWattHour(0x08, "MWh"),
	HundredMegaWattHour(0x0A, "MWh x 100"),
	Joule(0x01, "J"),
	KiloJoule(0x0B, "KJ"),
	MegaJoule(0x0E, "MJ"),
	GigaJoule(0x11, "GJ"),
	HundredGigaJoule(0x13, "GJ x 100"),
	Watt(0x14, "W"),
	KiloWatt(0x17, "KW"),
	MegaWatt(0x1A, "MW"),
	Litre(0x29, "L"),
	CubicMeter(0x2C, "m3"),
	LitrePerHour(0x32, "L/h"),
	CubicMeterPerHour(0x35, "m3/h"),
	Unknown(0x00, "");

	companion object {
		val textMap: Map<Byte, String> = values().associate {
			it.code to it.text
		}
		val unitsMap: Map<Byte, Units> = values().associateBy { it.code }

		fun codeToText(code: Byte): String = textMap[code] ?: "unknown"

		fun fromNumber(n: Number): Units {
			for (u in unitsMap) {
				if (u.key == n.toByte()) {
					return u.value
				}
			}
			return Unknown
		}

		fun fromString(text: String): Units? {
			val tokens = text.split(' ', '(', ')')
			return values().find {
				tokens.any { token ->
					it.text == token || it.name == token
				}
			}
		}
	}

	override fun toString(): String {
		return "$text (${super.toString()})"
	}

	class Streamable(var value: Units = Unknown) : BytesStreamable {
		override fun writeToBytesStream(outputStream: ByteArrayOutputStream) {
			outputStream.write(value.code.toInt())
		}

		override fun readFromBytesStream(inputStream: ByteArrayInputStream) {
			inputStream.read().also {
				if (it < 0) throw IOException("读出错！")
				value = unitsMap[it.toByte()] ?: Unknown
			}
		}
	}

}
