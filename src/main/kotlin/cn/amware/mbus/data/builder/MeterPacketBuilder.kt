package cn.amware.mbus.data.builder

import cn.amware.mbus.data.*
import java.io.ByteArrayOutputStream

object MeterPacketBuilder {

	fun buildReadNormalDataPacket(addressAsHex:String, dataTag: String): MeterPacket {
		val address = HexData7()
		address.asHex = addressAsHex

		val meterData = MeterData()
		meterData.head.dataId.asHex = dataTag
		meterData.head.seq = generateNextSeq()
		meterData.body = null

		val bs = ByteArrayOutputStream()
		bs wr meterData
		return MeterPacket(address, 0x21.toByte(), bs.toByteArray())
	}

	fun buildReadNormalDataPacket(addressAsHex:String, meterDataType: MeterDataType): MeterPacket {
		return buildReadNormalDataPacket(addressAsHex,meterDataType.tag)
	}

}
