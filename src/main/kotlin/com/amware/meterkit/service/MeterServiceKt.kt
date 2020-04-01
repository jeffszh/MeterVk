package com.amware.meterkit.service

import cn.amware.mbus.data.MeterDataType
import cn.amware.mbus.data.body.FlowData
import cn.amware.mbus.data.builder.MeterPacketBuilder
import cn.amware.utils.DataUtils
import com.amware.meterkit.entity.MsdFlowData
import com.amware.meterkit.mbus.SerialPortMan.sendAndReceive
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class MeterServiceKt {

	private fun checkAndReverseAddress(nullableAddress: String?): String {
		println("nullableAddress=[$nullableAddress]")
		val address = nullableAddress ?: "AA AA AA AA AA AA AA"
		try {
			val addressBytes = DataUtils.hexStrToBytes(address)
			if (addressBytes.size != 7) {
				throw BadRequestException("address必须是7个字节的16进制字符串。")
			}
			val revAddressBytes = DataUtils.reversedArray(addressBytes)
			return DataUtils.bytesToHexStr(*revAddressBytes)
		} catch (e: Exception) {
			e.printStackTrace()
			throw BadRequestException("address必须是7个字节的16进制字符串。")
		}
	}

	private fun reverseAddress(address: String): String =
			DataUtils.bytesToHexStr(
					* DataUtils.reversedArray(
							DataUtils.hexStrToBytes(address)
					)
			)

	fun getFlowData(nullableAddress: String?): MsdFlowData {
		val address = checkAndReverseAddress(nullableAddress)
		val meterPacket = MeterPacketBuilder.buildReadNormalDataPacket(
				address, MeterDataType.FLOW_DATA)
		meterPacket.ctrlCode = 0x01

		val resultList = sendAndReceive(meterPacket)
		return if (resultList.isNotEmpty()) {
			val (meterAddress, meterData) = resultList[0]
			val (head, body) = meterData
			if (head.dataTag != MeterDataType.FLOW_DATA.tag ||
					body !is FlowData) {
				throw IOException("收到错误的数据。")
			}
			with(body) {
				MsdFlowData(
						reverseAddress(meterAddress),
						sumOfFlow.asString.toDouble(),
						unit1.value.text,
						negSumOfFlow.asString.toDouble(),
						unit2.value.text,
						realTimeClock.toString(),
						bitField.bytes[0],
						reserved.bytes[0]
				)
			}
		} else {
			throw IOException("收不到串口数据。")
		}
	}

}
