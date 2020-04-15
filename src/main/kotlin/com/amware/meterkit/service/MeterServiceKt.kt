package com.amware.meterkit.service

import cn.amware.mbus.data.*
import cn.amware.mbus.data.body.CurrentCumulativeData
import cn.amware.mbus.data.body.DebuggingUiData
import cn.amware.mbus.data.body.FlowData
import cn.amware.mbus.data.body.PreciseFlowData
import cn.amware.mbus.data.builder.MeterPacketBuilder
import cn.amware.utils.DataUtils
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import com.amware.meterkit.entity.MsdCurrentCumulativeData
import com.amware.meterkit.entity.MsdDebuggingUiData
import com.amware.meterkit.entity.MsdFlowData
import com.amware.meterkit.entity.MsdPreciseFlowData
import com.amware.meterkit.mbus.SerialPortMan
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import kotlin.experimental.and

@Service
class MeterServiceKt {

	private var addressPrefix = "11 11 00"

	companion object {
		private const val configFileName = "MeterServiceConfig.json"
		private const val START_TESTING_DATA_TAG = "02A0"
	}

	private class MeterServiceConfig {
		var addressPrefix: String = ""
	}

	init {
		loadConfig()
	}

	private fun loadConfig() {
		try {
			FileInputStream(configFileName).use {
				val config = JSON.parseObject<MeterServiceConfig>(
						it, StandardCharsets.UTF_8, MeterServiceConfig::class.java)
				if (DataUtils.hexStrToBytes(config.addressPrefix).size != 3) {
					throw IOException("配置文件错误。")
				}
				addressPrefix = config.addressPrefix.trim()
			}
		} catch (e: Exception) {
			e.printStackTrace()
			val config = MeterServiceConfig()
			config.addressPrefix = "11 11 00"
			FileOutputStream(configFileName).use {
				JSON.writeJSONString(it, config, SerializerFeature.PrettyFormat)
			}
		}
	}

	private fun checkAndReverseAddress(nullableAddress: String?): String {
		println("nullableAddress=[$nullableAddress]")
		val address = nullableAddress ?: "AA AA AA AA AA AA AA"
		try {
			val addressBytes = DataUtils.hexStrToBytes(address)
			return when (addressBytes.size) {
				4 -> {
					val revAddressBytes = DataUtils.reversedArray(addressBytes)
					val revPrefix = DataUtils.reversedArray(DataUtils.hexStrToBytes(addressPrefix))
					DataUtils.bytesToHexStr(*revAddressBytes, *revPrefix)
				}
				7 -> {
					val revAddressBytes = DataUtils.reversedArray(addressBytes)
					DataUtils.bytesToHexStr(*revAddressBytes)
				}
				else -> throw BadRequestException("address必须是4或7个字节的16进制字符串。")
			}
		} catch (e: Exception) {
			e.printStackTrace()
			throw BadRequestException("address必须是4或7个字节的16进制字符串。")
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

		val resultList = SerialPortMan.sendAndReceive(meterPacket)
		return if (resultList.isNotEmpty()) {
			val (meterAddress, _, meterData) = resultList[0]
			val (head, body) = meterData
			if (head.dataTag != MeterDataType.FLOW_DATA.tag ||
					body !is FlowData) {
				throw IOException("收到错误的数据。")
			}
			with(body) {
				MsdFlowData(
						reverseAddress(meterAddress),
						sumOfFlow.asString.toDoubleOrNull() ?: Double.NaN,
						unit1.value.text,
						negSumOfFlow.asString.toDoubleOrNull() ?: Double.NaN,
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

	fun getPreciseFlowData(nullableAddress: String?): MsdPreciseFlowData {
		val address = checkAndReverseAddress(nullableAddress)
		val meterPacket = MeterPacketBuilder.buildReadNormalDataPacket(
				address, MeterDataType.PRECISE_FLOW_DATA)
		meterPacket.ctrlCode = 0x01

		val resultList = SerialPortMan.sendAndReceive(meterPacket)
		return if (resultList.isNotEmpty()) {
			val (meterAddress, _, meterData) = resultList[0]
			val (head, body) = meterData
			if (head.dataTag != MeterDataType.PRECISE_FLOW_DATA.tag ||
					body !is PreciseFlowData) {
				throw IOException("收到错误的数据。")
			}
			with(body) {
				MsdPreciseFlowData(
						reverseAddress(meterAddress),
						sumOfCooling.asString.toDoubleOrNull() ?: Double.NaN,
						unit1.text,
						sumOfHeat.asString.toDoubleOrNull() ?: Double.NaN,
						unit2.text,
						instantPower.asString.toDoubleOrNull() ?: Double.NaN,
						unit3.text,
						instantFlow.asString.toDoubleOrNull() ?: Double.NaN,
						unit4.text,
						sumOfFlow.asString.toDoubleOrNull() ?: Double.NaN,
						unit5.text
				)
			}
		} else {
			throw IOException("收不到串口数据。")
		}
	}

	fun getDebuggingUiData(nullableAddress: String?): MsdDebuggingUiData {
		val address = checkAndReverseAddress(nullableAddress)
		val meterPacket = MeterPacketBuilder.buildReadNormalDataPacket(
				address, MeterDataType.DEBUGGING_UI_DATA)
		meterPacket.ctrlCode = 0x01

		val resultList = SerialPortMan.sendAndReceive(meterPacket)
		return if (resultList.isNotEmpty()) {
			val (meterAddress, _, meterData) = resultList[0]
			val (head, body) = meterData
			if (head.dataTag != MeterDataType.DEBUGGING_UI_DATA.tag ||
					body !is DebuggingUiData) {
				throw IOException("收到错误的数据。")
			}
			with(body) {
				MsdDebuggingUiData(
						reverseAddress(meterAddress),
						programVersion.value.toInt(),
						supplyReturnFlag.bytes[0] > 0,
						instrumentFactor.asString.toDoubleOrNull() ?: Double.NaN,
						lowFlowExcision.asString.toDoubleOrNull() ?: Double.NaN,
						flowCompensation1.asString.toDoubleOrNull() ?: Double.NaN,
						flowCompensation2.asString.toDoubleOrNull() ?: Double.NaN,
						flowCompensation3.asString.toDoubleOrNull() ?: Double.NaN,
						flowCompensation4.asString.toDoubleOrNull() ?: Double.NaN,
						supplyingTemperatureCompensation.asString
								.toDoubleOrNull() ?: Double.NaN,
						returningTemperatureCompensation.asString
								.toDoubleOrNull() ?: Double.NaN,
						introductionDate.asString,
						caliber.asString.toDoubleOrNull() ?: Double.NaN
				)
			}
		} else {
			throw IOException("收不到串口数据。")
		}
	}

	fun writeCurrentCumulativeData(msdCurrentCumulativeData: MsdCurrentCumulativeData) {
		val address = checkAndReverseAddress(msdCurrentCumulativeData.address)
		val currentCumulativeData = try {
			with(msdCurrentCumulativeData) {
				CurrentCumulativeData(
						Bcd50().apply {
							value = sumOfCooling
						},
						Units.fromString(unit1) ?: throw BadRequestException("$unit1 不是合法的单位。"),
						Bcd50().apply {
							value = sumOfHeat
						},
						Units.fromString(unit2) ?: throw BadRequestException("$unit2 不是合法的单位。"),
						Bcd42().apply {
							asString = sumOfFlow.toString()
						},
						Units.fromString(unit3) ?: throw BadRequestException("$unit3 不是合法的单位。")
				)
			}
		} catch (e: Exception) {
			e.printStackTrace()
			throw BadRequestException(e.message ?: "输入参数错误。")
		}

		val meterData = MeterData()
		meterData.body = currentCumulativeData
		meterData.head.dataId.asHex = MeterDataType.CURRENT_CUMULATIVE_DATA.tag
		meterData.head.seq = generateNextSeq()
		val bs = ByteArrayOutputStream()
		bs wr meterData
		val meterPacket = MeterPacket(0x20, HexData7().apply {
			asHex = address
		}, 0x04, bs.toByteArray())
		println(meterPacket)

		val resultList = SerialPortMan.sendAndReceive(meterPacket)
		if (resultList.isNotEmpty()) {
			val (_, _, recMeterData) = resultList[0]
			val (head, _) = recMeterData
			if (head.dataTag != MeterDataType.CURRENT_CUMULATIVE_DATA.tag) {
				throw IOException("收到错误的数据。")
			}
		} else {
			throw IOException("收不到串口数据。")
		}
	}

	fun startTesting(nullableAddress: String?) {
		val address = checkAndReverseAddress(nullableAddress)
		val meterPacket = MeterPacketBuilder.buildReadNormalDataPacket(
				address, START_TESTING_DATA_TAG)
		meterPacket.ctrlCode = 0x04

		val resultList = SerialPortMan.sendAndReceive(meterPacket)
		if (resultList.isNotEmpty()) {
			val (meterAddress, ctrlCode, meterData) = resultList[0]
			println("启动检定回应包，地址=$meterAddress，" +
					"控制码=${String.format("0x%02X", ctrlCode.toInt() and 0xFF)}")
			if (ctrlCode and 0x40 != 0.toByte()) {
				throw IOException("通讯异常，收到控制码为：$ctrlCode")
			}
			val (head, _) = meterData
			println("dataTag=${head.dataTag}")
			if (head.dataTag != START_TESTING_DATA_TAG) {
				throw IOException("收到错误的数据。")
			}
		} else {
			throw IOException("收不到串口数据。")
		}
	}

}
