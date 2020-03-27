package cn.amware.mbus.data.body

//import cn.amware.app.BitwiseAgent
import cn.amware.mbus.data.Bcd42
import cn.amware.mbus.data.BytesStreamable
import cn.amware.mbus.data.HexData1
import cn.amware.mbus.data.MeterDataBody
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import kotlin.reflect.KMutableProperty1

data class UsmTestResult(
		var testStatus: Byte = 0x00,
		var errorCode1: Byte = 0x00,
		var errorCode2: HexData1 = HexData1(),
		var pw1stUp: Bcd42 = Bcd42(),
		var pw1stDown: Bcd42 = Bcd42(),
		var tofUp: Bcd42 = Bcd42(),
		var tofDown: Bcd42 = Bcd42(),
		var dTof: Bcd42 = Bcd42(),
		var dTofFilt: Bcd42 = Bcd42()
) : MeterDataBody() {

//	var errTimeOutUp: Boolean by BitwiseAgent(UsmTestResult::errorCode1, 0x01)
//	var errTimeOutDown: Boolean by BitwiseAgent(UsmTestResult::errorCode1, 0x02)
//	var errPw1stUp: Boolean by BitwiseAgent(UsmTestResult::errorCode1, 0x04)
//	var errPw1stDown:Boolean by BitwiseAgent(UsmTestResult::errorCode1,0x08)

	private val streamProperties = arrayOf(
			UsmTestResult::testStatus,
			UsmTestResult::errorCode1,
			UsmTestResult::errorCode2,
			UsmTestResult::pw1stUp,
			UsmTestResult::pw1stDown,
			UsmTestResult::tofUp,
			UsmTestResult::tofDown,
			UsmTestResult::dTof,
			UsmTestResult::dTofFilt
	)

	@Throws(IOException::class)
	override fun readFromBytesStream(inputStream: ByteArrayInputStream) {
		streamProperties.forEach {
			val fieldValue = it.get(this)
			println(fieldValue)
			when (fieldValue) {
				is BytesStreamable -> fieldValue.readFromBytesStream(inputStream)
				is Byte -> {
					@Suppress("UNCHECKED_CAST")
					it as KMutableProperty1<UsmTestResult, Byte>
					val b = inputStream.read()
					if (b < 0) {
						throw IOException("读数据出错！")
					}
					it.set(this, b.toByte())
				}
			}
		}
	}

	@Throws(IOException::class)
	override fun writeToBytesStream(outputStream: ByteArrayOutputStream) {
		streamProperties.forEach {
			val fieldValue = it.get(this)
			println(fieldValue)
			when (fieldValue) {
				is BytesStreamable -> fieldValue.writeToBytesStream(outputStream)
				is Byte -> {
					@Suppress("UNCHECKED_CAST")
					it as KMutableProperty1<UsmTestResult, Byte>
					outputStream.write(fieldValue.toInt())
				}
			}
		}
	}

}
