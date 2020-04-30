package com.amware.meterkit.service

import cn.amware.mbus.data.*
import cn.amware.mbus.data.body.*
import cn.amware.mbus.data.builder.MeterPacketBuilder
import cn.amware.utils.DataUtils
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import com.amware.meterkit.entity.*
import com.amware.meterkit.mbus.SerialPortMan
import com.amware.meterkit.net.LoraPlatformClient
import org.springframework.http.HttpStatus
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
		fun BcdData.toDoubleOrNaN() = asString.toDoubleOrNull() ?: Double.NaN

		fun <T : BcdData> Number.toBcd(bcd: T) = bcd.apply {
			asString = this@toBcd.toString()
		}
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

	fun checkAndGetResult(
			resultList: List<Triple<String, Byte, MeterData>>,
			dataTag: String): Pair<String, MeterDataBody?> {
		if (resultList.isEmpty()) {
			throw IOException("收不到串口数据。")
		}
		val exceptionList = mutableListOf<IOException>()
		resultList.forEachIndexed { i, (meterAddress, ctrlCode, meterData) ->
			if (ctrlCode and 0x40 != 0.toByte()) {
				exceptionList.add(IOException("通讯异常，收到控制码为：${String.format("0x%02X",
						ctrlCode.toInt() and 0xFF)}"))
			} else {
				val (head, body) = meterData
				if (head.dataTag == dataTag) {
					println("很好，第 $i 个结果类型匹配。")
					return meterAddress to body
				}
			}
		}
		if (exceptionList.isNotEmpty()) {
			throw exceptionList[0]
		}
		throw IOException("串口收到错误的数据。")
	}

	fun <T : MeterDataBody> checkAndGetTypedResult(
			resultList: List<Triple<String, Byte, MeterData>>,
			dataTag: String,
			bodyClass: Class<T>): Pair<String, T> {
		val (meterAddress, body) = checkAndGetResult(resultList, dataTag)
		if (bodyClass.isInstance(body)) {
			return meterAddress to bodyClass.cast(body)
		} else {
			throw IOException("串口收到错误的数据类型。")
		}
	}

	fun getFlowData(nullableAddress: String?): MsdFlowData {
		val address = checkAndReverseAddress(nullableAddress)
		val meterPacket = MeterPacketBuilder.buildReadNormalDataPacket(
				address, MeterDataType.FLOW_DATA)
		meterPacket.ctrlCode = 0x01

		val resultList = SerialPortMan.sendAndReceive(meterPacket)
		val (meterAddress, body) = checkAndGetTypedResult(resultList,
				MeterDataType.FLOW_DATA.tag, FlowData::class.java
		)
		return with(body) {
			MsdFlowData(
					reverseAddress(meterAddress),
					sumOfFlow.toDoubleOrNaN(),
					unit1.value.text,
					negSumOfFlow.toDoubleOrNaN(),
					unit2.value.text,
					realTimeClock.toString(),
					bitField.bytes[0],
					reserved.bytes[0]
			)
		}
	}

	fun getPreciseFlowData(nullableAddress: String?): MsdPreciseFlowData {
		val address = checkAndReverseAddress(nullableAddress)
		val meterPacket = MeterPacketBuilder.buildReadNormalDataPacket(
				address, MeterDataType.PRECISE_FLOW_DATA)
		meterPacket.ctrlCode = 0x01

		val resultList = SerialPortMan.sendAndReceive(meterPacket)
		val (meterAddress, body) = checkAndGetTypedResult(resultList,
				MeterDataType.PRECISE_FLOW_DATA.tag, PreciseFlowData::class.java)
		return with(body) {
			MsdPreciseFlowData(
					reverseAddress(meterAddress),
					sumOfCooling.toDoubleOrNaN(),
					unit1.text,
					sumOfHeat.toDoubleOrNaN(),
					unit2.text,
					instantPower.toDoubleOrNaN(),
					unit3.text,
					instantFlow.toDoubleOrNaN(),
					unit4.text,
					sumOfFlow.toDoubleOrNaN(),
					unit5.text
			)
		}
	}

	fun getDebuggingUiData(nullableAddress: String?): MsdDebuggingUiData {
		val address = checkAndReverseAddress(nullableAddress)
		val meterPacket = MeterPacketBuilder.buildReadNormalDataPacket(
				address, MeterDataType.DEBUGGING_UI_DATA)
		meterPacket.ctrlCode = 0x01

		val resultList = SerialPortMan.sendAndReceive(meterPacket)
		val (meterAddress, body) = checkAndGetTypedResult(resultList,
				MeterDataType.DEBUGGING_UI_DATA.tag, DebuggingUiData::class.java)
		return with(body) {
			MsdDebuggingUiData(
					reverseAddress(meterAddress),
					programVersion.value.toInt(),
					supplyReturnFlag.bytes[0] > 0,
					instrumentFactor.toDoubleOrNaN(),
					lowFlowExcision.toDoubleOrNaN(),
					flowCompensation1.toDoubleOrNaN(),
					flowCompensation2.toDoubleOrNaN(),
					flowCompensation3.toDoubleOrNaN(),
					flowCompensation4.toDoubleOrNaN(),
					supplyingTemperatureCompensation.toDoubleOrNaN(),
					returningTemperatureCompensation.toDoubleOrNaN(),
					introductionDate.asString,
					caliber.toDoubleOrNaN()
			)
		}
	}

	fun writeCurrentCumulativeData(msdCurrentCumulativeData: MsdCurrentCumulativeData) {
		val address = checkAndReverseAddress(msdCurrentCumulativeData.address)
		val currentCumulativeData = try {
			with(msdCurrentCumulativeData) {
				CurrentCumulativeData(
						sumOfCooling.toBcd(Bcd50()),
						Units.fromString(unit1) ?: throw BadRequestException("$unit1 不是合法的单位。"),
						sumOfHeat.toBcd(Bcd50()),
						Units.fromString(unit2) ?: throw BadRequestException("$unit2 不是合法的单位。"),
						sumOfFlow.toBcd(Bcd42()),
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
		checkAndGetResult(resultList, MeterDataType.CURRENT_CUMULATIVE_DATA.tag)
	}

	fun startTesting(nullableAddress: String?) {
		val address = checkAndReverseAddress(nullableAddress)
		val meterPacket = MeterPacketBuilder.buildReadNormalDataPacket(
				address, START_TESTING_DATA_TAG)
		meterPacket.ctrlCode = 0x04

		val resultList = SerialPortMan.sendAndReceive(meterPacket)
		checkAndGetResult(resultList, START_TESTING_DATA_TAG)
	}

	fun readStandardTime(nullableAddress: String?): MsdStandardTimeData {
		val address = checkAndReverseAddress(nullableAddress)
		val meterPacket = MeterPacketBuilder.buildReadNormalDataPacket(
				address, MeterDataType.STANDARD_TIME_DATA)
		meterPacket.instrumentType = 0x20
		meterPacket.ctrlCode = 0x01

		val resultList = SerialPortMan.sendAndReceive(meterPacket)
		val (meterAddress, body) = checkAndGetTypedResult(resultList,
				MeterDataType.STANDARD_TIME_DATA.tag, StandardTimeData::class.java)
//			val dateFormat = SimpleDateFormat("yyyyMMddHHmmss")
//			val date = dateFormat.parse(body.standardTime.asHex)
		return with(DataUtils.noSpaceHexStr(DataUtils.bytesToHexStr(
				*body.standardTime.bytes.reversedArray()
		))) {
			MsdStandardTimeData().apply {
				this.address = reverseAddress(meterAddress)
				year = substring(0, 4).toInt()
				month = substring(4, 6).toInt()
				day = substring(6, 8).toInt()
				hour = substring(8, 10).toInt()
				minute = substring(10, 12).toInt()
				second = substring(12, 14).toInt()
			}
		}
	}

	fun writeStandardTime(msdStandardTimeData: MsdStandardTimeData) {
		val address = checkAndReverseAddress(msdStandardTimeData.address)
		with(msdStandardTimeData) {
			if (year !in 1900..2099) {
				throw BadRequestException("年份必须是：1900--2099。")
			}
			if (month !in 1..12) {
				throw BadRequestException("月份必须是：1--12。")
			}
			if (day !in 1..31) {
				throw BadRequestException("日必须是：1--31。")
			}
			if (hour !in 0..23) {
				throw BadRequestException("小时必须是：0--23。")
			}
			if (minute !in 0..59) {
				throw BadRequestException("分钟必须是：0--59。")
			}
			if (second !in 0..59) {
				throw BadRequestException("秒必须是：0--59。")
			}
		}
		val dateString = with(msdStandardTimeData) {
			String.format("%04d%02d%02d%02d%02d%02d", year, month, day, hour, minute, second)
		}
		val standardTimeData = StandardTimeData(Bcd70().apply {
			asHex = dateString
			DataUtils.reverseArrayContent(bytes)
		})

		val meterData = MeterData()
		meterData.body = standardTimeData
		meterData.head.dataId.asHex = MeterDataType.STANDARD_TIME_DATA.tag
		meterData.head.seq = generateNextSeq()
		val bs = ByteArrayOutputStream()
		bs wr meterData
		val meterPacket = MeterPacket(0x20, HexData7().apply {
			asHex = address
		}, 0x04, bs.toByteArray())

		val resultList = SerialPortMan.sendAndReceive(meterPacket)
		checkAndGetResult(resultList, MeterDataType.STANDARD_TIME_DATA.tag)
	}

	fun readMeterNumAddress(nullableAddress: String?): MsdMeterNumAddressData {
		val address = checkAndReverseAddress(nullableAddress)
		val meterPacket = MeterPacketBuilder.buildReadNormalDataPacket(
				address, MeterDataType.METER_NUM_ADDRESS_DATA)
		meterPacket.instrumentType = 0x20
		meterPacket.ctrlCode = 0x01

		val resultList = SerialPortMan.sendAndReceive(meterPacket)
		return if (resultList.isNotEmpty()) {
			val (meterAddress, ctrlCode, recMeterData) = resultList[0]
//			if (ctrlCode and 0x40 != 0.toByte()) {
//				throw IOException("通讯异常，收到控制码为：$ctrlCode")
//			}
			val (head, body) = recMeterData

			// 注：实际上A018修改表号地址，水表并没有提供读的功能，所以是自己拼凑数据给外面一个读的功能。
			println("读表号地址，返回的控制码=${String.format("0x%02X", ctrlCode.toInt() and 0xFF)}，body=$body")

			if (head.dataTag != MeterDataType.METER_NUM_ADDRESS_DATA.tag /*||
					body !is MeterNumAddressData*/) {
				throw IOException("串口收到错误的数据。")
			}
			MsdMeterNumAddressData().apply {
				this.address = reverseAddress(meterAddress)
				newAddress = this.address.substring(9)    // 拼凑一个地址，权作为给外面一个读的结果。
			}
		} else {
			throw IOException("收不到串口数据。")
		}
	}

	fun writeMeterNumAddress(msdMeterNumAddressData: MsdMeterNumAddressData) {
		if (msdMeterNumAddressData.newAddress == null) {
			throw BadRequestException("newAddress不能为空！")
		}
		val address = checkAndReverseAddress(msdMeterNumAddressData.address)
		val newAddress = msdMeterNumAddressData.newAddress.trim()
		println("newAddress=$newAddress")
		if (!Regex("([0-9]{2} +)*").matches("$newAddress ")) {
			throw BadRequestException("$newAddress 不是合法的BCD字符串。")
		}
		val addrBytes = DataUtils.hexStrToBytes(newAddress)
		if (addrBytes.size != 4) {
			throw BadRequestException("输入错误：$newAddress，必须输入4个字节的BCD码。")
		}
		val meterNumAddressData = MeterNumAddressData(HexData7().apply {
			asHex = "$addressPrefix $newAddress"
			DataUtils.reverseArrayContent(bytes)
		})

		val meterData = MeterData()
		meterData.body = meterNumAddressData
		meterData.head.dataId.asHex = MeterDataType.METER_NUM_ADDRESS_DATA.tag
		meterData.head.seq = generateNextSeq()
		val bs = ByteArrayOutputStream()
		bs wr meterData
		val meterPacket = MeterPacket(0x20, HexData7().apply {
			asHex = address
		}, 0x39, bs.toByteArray())

		val resultList = SerialPortMan.sendAndReceive(meterPacket)
		checkAndGetResult(resultList, MeterDataType.METER_NUM_ADDRESS_DATA.tag)
	}

	fun readFactoryDate(nullableAddress: String?): MsdFactoryDateData {
		val address = checkAndReverseAddress(nullableAddress)
		val meterPacket = MeterPacketBuilder.buildReadNormalDataPacket(
				address, MeterDataType.FACTORY_DATE_DATA)
		meterPacket.instrumentType = 0x20
		meterPacket.ctrlCode = 0x01

		val resultList = SerialPortMan.sendAndReceive(meterPacket)
		val (meterAddress, body) = checkAndGetTypedResult(resultList,
				MeterDataType.FACTORY_DATE_DATA.tag, FactoryDateData::class.java)
		return with(DataUtils.noSpaceHexStr(DataUtils.bytesToHexStr(
				*body.factoryDate.bytes.reversedArray()
		))) {
			MsdFactoryDateData().apply {
				this.address = reverseAddress(meterAddress)
				year = substring(0, 4).toInt()
				month = substring(4, 6).toInt()
				day = substring(6, 8).toInt()
			}
		}
	}

	fun writeFactoryDate(msdFactoryDateData: MsdFactoryDateData) {
		val address = checkAndReverseAddress(msdFactoryDateData.address)
		with(msdFactoryDateData) {
			if (year !in 1900..2099) {
				throw BadRequestException("年份必须是：1900--2099。")
			}
			if (month !in 1..12) {
				throw BadRequestException("月份必须是：1--12。")
			}
			if (day !in 1..31) {
				throw BadRequestException("日必须是：1--31。")
			}
		}
		val dateString = with(msdFactoryDateData) {
			String.format("%04d%02d%02d", year, month, day)
		}
		val factoryDateData = FactoryDateData(Bcd40().apply {
			asHex = dateString
			DataUtils.reverseArrayContent(bytes)
		})

		val meterData = MeterData()
		meterData.body = factoryDateData
		meterData.head.dataId.asHex = MeterDataType.FACTORY_DATE_DATA.tag
		meterData.head.seq = generateNextSeq()
		val bs = ByteArrayOutputStream()
		bs wr meterData
		val meterPacket = MeterPacket(0x20, HexData7().apply {
			asHex = address
		}, 0x04, bs.toByteArray())

		val resultList = SerialPortMan.sendAndReceive(meterPacket)
		checkAndGetResult(resultList, MeterDataType.FACTORY_DATE_DATA.tag)
	}

	fun changeWorkMode(msdWorkModeData: MsdWorkModeData): MsdWorkModeData {
		// 因为此操作不会得到硬件响应，所以先读取一下水表地址，确保水表是在线。
		val meterNumAddressData = readMeterNumAddress(msdWorkModeData.address)

		val address = checkAndReverseAddress(msdWorkModeData.address)
		val workModeData = WorkModeData(
				if (msdWorkModeData.factoryMode) {
					WorkModeData.WorkMode.IN_FACTORY_MODE
				} else {
					WorkModeData.WorkMode.OUT_FACTORY_MODE
				}
		)

		val meterData = MeterData()
		meterData.body = workModeData
		meterData.head.dataId.asHex = MeterDataType.WORK_MODE_DATA.tag
		meterData.head.seq = generateNextSeq()
		val bs = ByteArrayOutputStream()
		bs wr meterData
		val meterPacket = MeterPacket(0x20, HexData7().apply {
			asHex = address
		}, 0x52, bs.toByteArray())

		try {
			SerialPortMan.sendAndReceive(meterPacket)    // 注：此操作不会收到水表的应答
		} catch (e: IOException) {
			e.printStackTrace()
		}
		return msdWorkModeData.apply {
			this.address = meterNumAddressData.address
		}
	}

	fun setFlowCorrection(msdFlowCorrectionData: MsdFlowCorrectionData) {
		val address = checkAndReverseAddress(msdFlowCorrectionData.address)
		with(msdFlowCorrectionData) {
			arrayOf(correctionPoint1, correctionPoint2, correctionPoint3, correctionPoint4).forEach {
				if (it !in -99.9..99.9) {
					throw BadRequestException("修正点取值范围必须在 -99.9 至 99.9 之间。")
				}
			}
		}
		val flowCorrectionData = with(msdFlowCorrectionData) {
			FlowCorrectionData(
					0,
					correctionPoint1.toBcd(Bcd21()),
					correctionPoint2.toBcd(Bcd21()),
					correctionPoint3.toBcd(Bcd21()),
					correctionPoint4.toBcd(Bcd21())
			)
		}

		val meterData = MeterData()
		meterData.body = flowCorrectionData
		meterData.head.dataId.asHex = MeterDataType.FLOW_CORRECTION_DATA.tag
		meterData.head.seq = generateNextSeq()
		val bs = ByteArrayOutputStream()
		bs wr meterData
		val meterPacket = MeterPacket(0x20, HexData7().apply {
			asHex = address
		}, 0x43, bs.toByteArray())

		val resultList = SerialPortMan.sendAndReceive(meterPacket)
		checkAndGetResult(resultList, MeterDataType.FLOW_CORRECTION_DATA.tag)
	}

	// 这个功能似乎是不行的？怎么回事？
	fun startUsmTest(nullableAddress: String?) {
		val address = checkAndReverseAddress(nullableAddress)
		val meterPacket = MeterPacketBuilder.buildReadNormalDataPacket(
				address, MeterDataType.USM_TEST_RESULT)
		meterPacket.ctrlCode = 0x24

		val resultList = SerialPortMan.sendAndReceive(meterPacket)
		checkAndGetResult(resultList, MeterDataType.USM_TEST_RESULT.tag)
	}

	// 这个功能似乎是不行的？怎么回事？
	fun readUsmTestResult(nullableAddress: String?): MsdUsmTestResult {
		val address = checkAndReverseAddress(nullableAddress)
		val meterPacket = MeterPacketBuilder.buildReadNormalDataPacket(
				address, MeterDataType.USM_TEST_RESULT)
		meterPacket.ctrlCode = 0x21

		val resultList = SerialPortMan.sendAndReceive(meterPacket)
		val (meterAddress, body) = checkAndGetTypedResult(resultList,
				MeterDataType.USM_TEST_RESULT.tag, UsmTestResult::class.java)
		return with(body) {
			MsdUsmTestResult(
					reverseAddress(meterAddress),
					testStatus,
					errorCode1,
					errorCode2.bytes[0],
					pw1stUp.toDoubleOrNaN(),
					pw1stDown.toDoubleOrNaN(),
					tofUp.toDoubleOrNaN(),
					tofDown.toDoubleOrNaN(),
					dTof.toDoubleOrNaN(),
					dTofFilt.toDoubleOrNaN()
			)
		}
	}

	fun valveCtrl(msdValveCtrlData: MsdValveCtrlData) {
		val address = checkAndReverseAddress(msdValveCtrlData.address)
		when (msdValveCtrlData.valveCtrl) {
			null -> throw BadRequestException("valveCtrl 不能为空。")
			!in 0x00..0x01 -> throw BadRequestException("valveCtrl 必须是 0x00 或 0x01")
		}

		val meterData = MeterData()
		meterData.head.dataId.asHex = MeterDataType.VALVE_CTRL.tag
		meterData.head.seq = generateNextSeq()
		meterData.body = ValveCtrlData().apply {
			command0Status1 = false
			off0On1 = msdValveCtrlData.valveCtrl
		}
		val bs = ByteArrayOutputStream()
		bs wr meterData
		val meterPacket = MeterPacket(HexData7().apply {
			asHex = address
		}, 0x24, bs.toByteArray())

		val resultList = SerialPortMan.sendAndReceive(meterPacket)
		checkAndGetResult(resultList, MeterDataType.VALVE_CTRL.tag)
	}

	fun readValveCtrlStatus(nullableAddress: String?): MsdValveCtrlData {
		val address = checkAndReverseAddress(nullableAddress)
		val meterPacket = MeterPacketBuilder.buildReadNormalDataPacket(
				address, MeterDataType.VALVE_CTRL)
		meterPacket.ctrlCode = 0x21

		val resultList = SerialPortMan.sendAndReceive(meterPacket)
		val (meterAddress, body) = checkAndGetTypedResult(resultList,
				MeterDataType.VALVE_CTRL.tag, ValveCtrlData::class.java)
		return with(body) {
			MsdValveCtrlData().also {
				it.address = reverseAddress(meterAddress)
				it.valveStatus = valveStatus.code
				it.errorCode = errorCode
			}
		}
	}

	fun readLoraParams(nullableAddress: String?): MsdLoraParams {
		val address = checkAndReverseAddress(nullableAddress)
		val meterPacket = MeterPacketBuilder.buildReadNormalDataPacket(
				address, MeterDataType.LORA_PARAM)

		val resultList = SerialPortMan.sendAndReceive(meterPacket)
		val (meterAddress, body) = checkAndGetTypedResult(resultList,
				MeterDataType.LORA_PARAM.tag, LoraParams::class.java)

		val companyCode = try {
			val getDeviceResultString = LoraPlatformClient.getDeviceInfo(
					DataUtils.noSpaceHexStr(body.devEui.asHex))
			println(getDeviceResultString)
			val getDeviceResult = JSON.parseObject(getDeviceResultString)
			println(getDeviceResult)
			getDeviceResult.getString("company_code")
		} catch (e: Exception) {
			e.printStackTrace()
			null
		}

		return with(body) {
			MsdLoraParams().also {
				it.address = reverseAddress(meterAddress)
				it.companyCode = companyCode
				it.devEui = devEui.asHex
				it.appEui = appEui.asHex
				it.appKey = appKey.asHex
			}
		}
	}

	fun writeLoraParams(msdLoraParams: MsdLoraParams) {
		val address = checkAndReverseAddress(msdLoraParams.address)
		with(msdLoraParams) {
			when (companyCode) {
				null, "zh", "amwares", "test" -> {
				}
				else -> throw BadRequestException("companyCode 必须是 \"zh\", \"amwares\"或\"test\"")
			}
			if (devEui == null) {
				throw BadRequestException("devEui 不能为空！")
			}
			try {
				if (DataUtils.hexStrToBytes(devEui).size != 8) {
					throw BadRequestException("devEui 必须是8字节的十六进制字符串。")
				}
			} catch (e: Exception) {
				throw BadRequestException("devEui 必须是8字节的十六进制字符串。")
			}
		}

		val createDeviceResultString = LoraPlatformClient.createDevice(msdLoraParams.companyCode,
				DataUtils.noSpaceHexStr(msdLoraParams.devEui.trim()))
		val createDeviceResult = JSON.parseObject(createDeviceResultString)
		println(createDeviceResult)
		//{"code":200,
		// "application_eui":"0000000000000000",
		// "company_code":"amwares",
		// "dev_eui":"1111110000001234",
		// "application_key":"e9903e547f879b56b36e1fe749da258b"}
		when (createDeviceResult.getInteger("code")) {
			200 -> {
				// do nothing
			}
			HttpStatus.UNPROCESSABLE_ENTITY.value() -> {
				throw UnprocessableEntityException("Lora平台访问出错：$createDeviceResult")
			}
			else -> {
				throw IOException("Lora平台访问出错：$createDeviceResult")
			}
		}
		val loraParams = LoraParams()
		loraParams.devEui.asHex = createDeviceResult.getString("dev_eui")
		loraParams.appEui.asHex = createDeviceResult.getString("application_eui")
		loraParams.appKey.asHex = createDeviceResult.getString("application_key")

		val meterData = MeterData(body = loraParams)
		meterData.head.dataId.asHex = MeterDataType.LORA_PARAM.tag
		meterData.head.seq = generateNextSeq()
		val bs = ByteArrayOutputStream()
		bs wr meterData
		val meterPacket = MeterPacket(HexData7().apply {
			asHex = address
		}, 0x24.toByte(), bs.toByteArray())
		println(meterPacket)

		val resultList = SerialPortMan.sendAndReceive(meterPacket)
		checkAndGetResult(resultList, MeterDataType.LORA_PARAM.tag)
	}

	fun readMeterParams(nullableAddress: String?): MsdMeterParams {
		val address = checkAndReverseAddress(nullableAddress)
		val meterPacket = MeterPacketBuilder.buildReadNormalDataPacket(
				address, MeterDataType.METER_PARAM)

		val resultList = SerialPortMan.sendAndReceive(meterPacket)
		val (meterAddress, body) = checkAndGetTypedResult(resultList,
				MeterDataType.METER_PARAM.tag, MeterParams::class.java)
		return with(body) {
			MsdMeterParams(
					reverseAddress(meterAddress),
					sampleTime.value.toInt(),
					zeroDrift.toDoubleOrNaN(),
					unit1Value.text,
					pw1stThreshold.toDoubleOrNaN(),
					tofUpperBound.toDoubleOrNaN(),
					tofLowerBound.toDoubleOrNaN(),
					tofMax.toDoubleOrNaN(),
					initialFlow.toDoubleOrNaN(),
					unit2Value.text,
					pipeHorizontalLength.toDoubleOrNaN(),
					pipeVerticalLength.toDoubleOrNaN(),
					pipeRadius.toDoubleOrNaN()
			)
		}
	}

	fun writeMeterParams(msdMeterParams: MsdMeterParams) {
		val address = checkAndReverseAddress(msdMeterParams.address)
		val meterParams = try {
			with(msdMeterParams) {
				MeterParams(
						sampleTime = sampleTime.toBcd(Bcd30()),
						zeroDrift = zeroDrift.toBcd(Bcd42()),
						unit1 = Units.Streamable(
								Units.fromString(unit1) ?: throw BadRequestException(
										"$unit1 不是合法的单位。")
						),
						pw1stThreshold = pw1stThreshold.toBcd(Bcd11()),
						tofUpperBound = tofUpperBound.toBcd(Bcd42()),
						tofLowerBound = tofLowerBound.toBcd(Bcd42()),
						tofMax = tofMax.toBcd(Bcd42()),
						initialFlow = initialFlow.toBcd(Bcd42()),
						unit2 = Units.Streamable(
								Units.fromString(unit2) ?: throw BadRequestException(
										"$unit2 不是合法的单位。")
						),
						pipeHorizontalLength = pipeHorizontalLength.toBcd(Bcd42()),
						pipeVerticalLength = pipeVerticalLength.toBcd(Bcd42()),
						pipeRadius = pipeRadius.toBcd(Bcd42())
				)
			}
		} catch (e: Exception) {
			e.printStackTrace()
			throw BadRequestException(e.message ?: "输入参数错误。")
		}

		val meterData = MeterData()
		meterData.body = meterParams
		meterData.head.dataId.asHex = MeterDataType.METER_PARAM.tag
		meterData.head.seq = generateNextSeq()
		val bs = ByteArrayOutputStream()
		bs wr meterData
		val meterPacket = MeterPacket(HexData7().apply {
			asHex = address
		}, 0x24, bs.toByteArray())
		println(meterPacket)

		val resultList = SerialPortMan.sendAndReceive(meterPacket)
		checkAndGetResult(resultList, MeterDataType.METER_PARAM.tag)
	}

}
