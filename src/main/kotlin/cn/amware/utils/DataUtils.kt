package cn.amware.utils

import java.io.ByteArrayOutputStream
import java.util.Scanner
import kotlin.math.abs

object DataUtils {

	fun bcdToLong(vararg bytes: Byte): Long {
		val hexChars = noSpaceHexStr(bytesToHexStr(*bytes.reversedArray())).toCharArray()
		if (hexChars[0] == 'F') {
			hexChars[0] = '-'
		}
		val hexStr = String(hexChars)
//		println("""
//			#############################################################
//			bcdToLong($hexStr)
//			#############################################################
//		""".trimIndent())
		return hexStr.toLong()

//		var sum = 0L
//		for (i in bytes.indices.reversed()) {
//			val x: Int = bytes[i].toInt() and 0xFF
//			val l = x and 0x0F
//			val h = x shr 4 and 0x0F
//			sum = sum * 10 + h
//			sum = sum * 10 + l
//		}
//		return sum
	}

	fun longToBcd(value: Long, len: Int): ByteArray {
		val isNeg = value < 0
		val s = abs(value).toString()
		val charList = mutableListOf<Char>()
		repeat(len * 2) {
			charList += if (len * 2 - it > s.length) {
				'0'
			} else {
				s[it - len * 2 + s.length]
			}
		}
		if (isNeg) {
			charList[0] = 'F'
		}
		val hexStr = String(charList.toCharArray())
		return hexStrToBytes(hexStr).reversedArray()

//		val bs = ByteArrayOutputStream()
//		var intVar = value
//		repeat(len) {
//			val l = intVar % 10
//			intVar /= 10
//			val h = intVar % 10
//			intVar /= 10
//			val bcd = h shl 4 or l
//			bs.write(bcd.toInt())
//		}
//		return bs.toByteArray()
	}

	fun bytesToHexStr(vararg bytes: Byte): String {
		val result = StringBuilder()
		for (b in bytes) {
			result.append(String.format("%02X ", b))
		}
		return result.toString().trim { it <= ' ' }
	}

	private fun internalHexStrToBytes(hexString: String): ByteArray {
		val bs = ByteArrayOutputStream()
		val scanner = Scanner(hexString)
		while (scanner.hasNext()) {
			bs.write(scanner.nextInt(16))
		}
		return bs.toByteArray()
	}

	fun hexStrToBytes(hexString: String): ByteArray {
		if (!hexString.trim().contains(" ")) {
			// 若字符串是紧缩无空格的，先每隔两个字符加插一个空格。
			val sb = StringBuilder()
			var i = 0
			var n = 0
			while (i < hexString.length) {
				if (n >= 2) {
					n = 0
					sb.append(' ')
				}
				sb.append(hexString[i])
				i++
				n++
			}
			return internalHexStrToBytes(sb.toString())
		} else {
			return internalHexStrToBytes(hexString)
		}
	}

	/**
	 * 去掉16进制字符串中间的空格。
	 * @param hexStr 原始的带空格的16进制字符串
	 * @return 紧排的没有空格的16进制字符串
	 */
	fun noSpaceHexStr(hexStr: String): String {
		val sb = StringBuilder()
		for (c in hexStr.toCharArray()) {
			if (c != ' ') {
				sb.append(c)
			}
		}
		return sb.toString()
	}

	fun reversedArray(bytes: ByteArray): ByteArray {
		return bytes.reversed().toByteArray()
	}

	fun reverseArrayContent(bytes: ByteArray) {
		bytes.reverse()
	}

}
