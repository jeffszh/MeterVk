package com.amware.meterkit.service

import cn.amware.mbus.data.MeterDataType
import cn.amware.mbus.data.body.FlowData
import cn.amware.mbus.data.builder.MeterPacketBuilder
import com.amware.meterkit.entity.MeterServiceData
import com.amware.meterkit.entity.MsdFlowData
import com.amware.meterkit.mbus.SerialPortMan.sendAndReceive
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class MeterServiceKt {

	fun getFlowData(inputParam: MeterServiceData): MsdFlowData {
		val address = inputParam.address ?: "AA AA AA AA AA AA AA"
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
						meterAddress,
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
