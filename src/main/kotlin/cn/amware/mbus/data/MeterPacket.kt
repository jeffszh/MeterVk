package cn.amware.mbus.data

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * # 水表数据包
 *
 * 用于生成和解析水表MBus协议的数据包。
 * <nl>
 * - 可生成二进制流，用于输出到水表——
 * > [.writeToBytesStream]
 * - 可将从水表收到的二进制流，解析填入对象的各个字段——
 * > [.readFromBytesStream]
 * </nl>
 */
class MeterPacket(
		var startMark: Byte = 0,
		var instrumentType: Byte = 0,
		var address: HexData7 = HexData7(),
		var ctrlCode: Byte = 0,
		var dataLen: Byte = 0,
		var data: ByteArray = ByteArray(3),
		var checkSum: Byte = 0,
		var endMark: Byte = 0
) : BytesStreamable {

	init {
		startMark = START_MARK
		endMark = END_MARK
		checkSum = 0
	}

	/**
	 * @see [MeterPacket]
	 */
	constructor(address: HexData7, ctrlCode: Byte, data: ByteArray) : this(0x10.toByte(), address, ctrlCode, data)

	/**
	 * @see [MeterPacket]
	 */
	constructor(instrumentType: Byte, address: HexData7, ctrlCode: Byte, data: ByteArray) : this() {
		this.instrumentType = instrumentType
		this.address = address
		this.ctrlCode = ctrlCode
		dataLen = data.size.toByte()
		this.data = data
	}

	@Throws(IOException::class)
	override fun writeToBytesStream(outputStream: ByteArrayOutputStream) {
		checkSum = 0
		with(outputStream) {
			write(startMark.toInt())
			write(instrumentType.toInt())
			write(address.bytes)
			write(ctrlCode.toInt())
			write(dataLen.toInt())
			write(data)
			for (b in toByteArray()) {
				checkSum = (checkSum + b).toByte()
			}
			write(checkSum.toInt())
			write(endMark.toInt())
		}
	}

	@Throws(IOException::class)
	private fun readOneByteAndAddToCheckSum(inputStream: ByteArrayInputStream): Byte {
		return inputStream.read().also {
			if (it < 0) {
				throw IOException("读数据出错！")
			}
		}.also {
			checkSum = (checkSum + it).toByte()
		}.toByte()
	}

	@Throws(IOException::class)
	private fun readBytesAndAddToCheckSum(inputStream: ByteArrayInputStream, bytes: ByteArray) {
		val len = inputStream.read(bytes)
		if (len != bytes.size) {
			throw IOException("读数据出错！")
		}
		for (b in bytes) {
			checkSum = (checkSum + b).toByte()
		}
	}

	@Throws(IOException::class)
	override fun readFromBytesStream(inputStream: ByteArrayInputStream) {
		// 先跳过前面的同步字节
		do {
			val st = inputStream.read()
			if (st < 0) {
				throw IOException("读数据出错！")
			}
		} while (st.toByte() != startMark)

		// 加到checkSum
		checkSum = startMark

		instrumentType = readOneByteAndAddToCheckSum(inputStream)
		readBytesAndAddToCheckSum(inputStream, address.bytes)
		ctrlCode = readOneByteAndAddToCheckSum(inputStream)
		dataLen = readOneByteAndAddToCheckSum(inputStream)
		data = ByteArray(dataLen.toInt() and 0xFF)
		readBytesAndAddToCheckSum(inputStream, data)
		if (checkSum != readOneByteAndAddToCheckSum(inputStream)) {
			throw IOException("CheckSum error!")
//			System.err.println("CheckSum error!")
		}
		if (endMark != readOneByteAndAddToCheckSum(inputStream)) {
			throw IOException("读数据出错！")
//			System.err.println("读数据出错！")
		}
	}

	override fun toString(): String {
		val sb = StringBuilder()
		sb.append("长度：").append(String.format("%02X", dataLen.toInt() and 0xFF)).append("\n")
		sb.append("数据：")
		for (x in data) {
			sb.append(String.format("%02X ", x.toInt() and 0xFF))
		}
		sb.append("\n")
		sb.append("地址：$address\n")
		sb.append("校验和：").append(String.format("%02X", checkSum)).append("\n")
		sb.append("st: ").append(String.format("%02X", startMark)).append("\n")
		sb.append("end:").append(String.format("%02X", endMark))
		return sb.toString()
	}

	companion object {
		private const val START_MARK: Byte = 0x68
		private const val END_MARK: Byte = 0x16
	}

}
