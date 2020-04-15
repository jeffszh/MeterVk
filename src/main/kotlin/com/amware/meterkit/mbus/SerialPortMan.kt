package com.amware.meterkit.mbus

import cn.amware.mbus.data.MeterData
import cn.amware.mbus.data.MeterPacket
import cn.amware.mbus.data.rd
import cn.amware.mbus.data.wr
import com.amware.meterkit.entity.SerialPortInfo
import com.amware.meterkit.service.BadRequestException
import gnu.io.CommPortIdentifier
import gnu.io.PortInUseException
import gnu.io.SerialPort
import gnu.io.SerialPortEvent
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

object SerialPortMan {

	private var serialPort: SerialPort? = null
	private val commPortIdentifierMap = sortedMapOf<String, CommPortIdentifier>()
	private val dataAvailableSignal = Semaphore(0)
	private val parityErrorSignal = Semaphore(0)

	init {
		findCommPorts()
	}

	private fun clearSignal(semaphore: Semaphore) {
		while (semaphore.tryAcquire()) {
			// do nothing
		}
	}

	fun findCommPorts(): List<String> {
		println("进入findCommPorts()")
		commPortIdentifierMap.clear()
		@Suppress("UNCHECKED_CAST")
		val portIdentifiers: Enumeration<CommPortIdentifier> =
				CommPortIdentifier.getPortIdentifiers() as Enumeration<CommPortIdentifier>
		while (portIdentifiers.hasMoreElements()) {
			val id = portIdentifiers.nextElement()
			if (id.portType == CommPortIdentifier.PORT_SERIAL) {
				commPortIdentifierMap[id.name] = id
			}
		}
//		Thread.sleep(800)
		return commPortIdentifierMap.keys.toList()
	}

	fun listSerialPortsInfo(): List<SerialPortInfo> {
		println("进入listSerialPortsInfo()")
		val infoList = mutableListOf<SerialPortInfo>()

		commPortIdentifierMap.keys.forEach { portName ->
			infoList.add(SerialPortInfo().apply {
				name = portName
				serialPort?.also {
					active = nameIsSame(portName, it.name)
					if (active) {
						baudRate = it.baudRate
						dataBits = it.dataBits
						stopBits = it.stopBits
						parity = it.parity
					}
				}
			})
		}

		return infoList
	}

	fun querySerialPortInfo(portName: String): SerialPortInfo {
		if (!commPortIdentifierMap.keys.contains(portName)) {
			throw BadRequestException("串口不存在：$portName")
		}
		return SerialPortInfo().apply {
			name = portName
			serialPort?.also {
				active = nameIsSame(portName, it.name)
				if (active) {
					baudRate = it.baudRate
					dataBits = it.dataBits
					stopBits = it.stopBits
					parity = it.parity
				}
			}
		}
	}

	fun closeSerialPort() {
		serialPort?.close()
		serialPort = null
	}

	fun openSerialPort(portName: String, baudRate: Int, dataBits: Int, stopBits: Int, parity: Int) {
		closeSerialPort()

		val portIdentifier = commPortIdentifierMap[portName] ?: throw BadRequestException("串口不存在：$portName")

		when (baudRate) {
			2400, 9600, 115200 -> {
				// do nothing
			}
			else -> throw BadRequestException("波特率必须是：2400、9600或115200。")
		}

		if (dataBits !in 7..8) {
			throw BadRequestException("数据位必须是7或8。")
		}

		if (stopBits !in 1..2) {
			throw BadRequestException("停止位必须是1或2。")
		}

		if (parity !in 0..2) {
			throw BadRequestException("奇偶校验必须是0到2。")
		}

		if (portIdentifier.isCurrentlyOwned) {
			throw IOException("端口 $portName 被 ${portIdentifier.currentOwner} 占用。")
		}

		try {
			// 第一个参数是使用者的名字还是什么？第二个参数应该是超时时间。
			val commPort = portIdentifier.open(javaClass.simpleName, 1500)
			if (commPort is SerialPort) {
				serialPort = commPort
			} else {
				commPort.close()
				throw IOException("端口 $portName 不是串行口。")
			}
		} catch (e: PortInUseException) {
			e.printStackTrace()
			throw IOException("端口 $portName 被 ${portIdentifier.currentOwner} 占用。")
		}

		println("串口 ${serialPort!!.name} 已成功打开。")
		with(serialPort!!) {
			setSerialPortParams(baudRate, dataBits, stopBits, parity)
			notifyOnDataAvailable(true)
			notifyOnParityError(true)
			inputBufferSize = 2048
			addEventListener {
				when (it.eventType) {
					SerialPortEvent.PE -> parityErrorSignal.release()
					SerialPortEvent.DATA_AVAILABLE -> dataAvailableSignal.release()
				}
			}
		}
		println("串口参数已经设置。")
	}

	private fun nameIsSame(stdName: String, fullName: String): Boolean =
			fullName == stdName || fullName == "//./$stdName"

	private fun clearResidua() {
		serialPort?.inputStream?.use { inputStream ->
			while (inputStream.available() > 0) {
				inputStream.read(ByteArray(1024))
			}
		}
		clearSignal(dataAvailableSignal)
		clearSignal(parityErrorSignal)
	}

	fun sendAndReceive(data: ByteArray): ByteArray {
		val serialPort = serialPort ?: throw BadRequestException("串口未打开")
		println("serialPort.inputBufferSize = ${serialPort.inputBufferSize}")

		// 清除掉可能存在的上次通讯的残余
		clearResidua()

		serialPort.outputStream.use { outputStream ->
			repeat(6) {
				outputStream.write(0xFE)
			}
			outputStream.write(data)
			outputStream.flush()
		}
//		Thread.sleep(500)

		serialPort.inputStream.use { inputStream ->
			if (!dataAvailableSignal.tryAcquire(2, TimeUnit.SECONDS)) {
				throw IOException("串口响应超时。")
			}

			val resultStream = ByteArrayOutputStream()
			while (inputStream.available() > 0) {
				val buffer = ByteArray(1024)
				val recLen = inputStream.read(buffer)
				if (recLen > 0) {
//					println("收到 $recLen 个字节")
					resultStream.write(buffer, 0, recLen)
				} else {
					break
				}
				dataAvailableSignal.tryAcquire(100, TimeUnit.MILLISECONDS)
			}
			if (resultStream.size() <= 0) {
				throw IOException("收不到串口数据。")
			}
			if (parityErrorSignal.tryAcquire()) {
				throw IOException("串口奇偶校验错！")
			}
			return resultStream.toByteArray()
		}
	}

	fun sendAndReceive(packet: MeterPacket): List<Triple<String, Byte, MeterData>> {
		val outStream = ByteArrayOutputStream()
		outStream wr packet
		val recData = sendAndReceive(outStream.toByteArray())
		val inStream = ByteArrayInputStream(recData)
		val result = mutableListOf<Triple<String, Byte, MeterData>>()
		inStream.use {
			while (it.available() > 0) {
				val meterPacket = MeterPacket()
				it rd meterPacket
				val meterData = MeterData()
				ByteArrayInputStream(meterPacket.data) rd meterData
				result.add(Triple(meterPacket.address.asHex, meterPacket.ctrlCode, meterData))
			}
		}
		return result
	}

	/*
	private object MbusReceiver {
		private val buffer = WaitQuiescenceByteBuffer(300, this::receiveCompletedData)

		fun onSerialEvent(serialPort: SerialPort, serialPortEvent: SerialPortEvent) {
			when (serialPortEvent.eventType) {
				SerialPortEvent.DATA_AVAILABLE -> try {
					serialPort.inputStream.use { inputStream ->
						if (inputStream.available() > 0) {
							val byteBuffer = ByteArray(2048)
							val recLen = inputStream.read(byteBuffer)
							buffer.putBytes(*byteBuffer.copyOfRange(0, recLen))
						}
					}
				} catch (e: IOException) {
					e.printStackTrace()
				}

				SerialPortEvent.PE -> fire(Events.OutTextEvent("PE!"))
			}
		}

		private fun receiveCompletedData(bytes: ByteArray) {
			fire(Events.ReceivedSerialDataEvent(bytes))
		}

	}

	@Synchronized
	fun onTxEvent(e: Events.SendSerialDataEvent) {
		if (serialPort.value == null) {
			return
		}

		try {
			serialPort.value.outputStream.use { outputStream ->
				repeat(4) {
					outputStream.write(0xFE)
				}
				outputStream.write(e.data)
				outputStream.flush()
				Thread.sleep(1500)
			}
		} catch (ex: IOException) {
			ex.printStackTrace()
		} catch (ex: InterruptedException) {
			ex.printStackTrace()
		}

	}

	init {
		subscribe<Events.ReceivedSerialDataEvent> {
			println("RX---------------------------")
			val text = DataUtils.bytesToHexStr(*it.data)
			fire(Events.OutTextEvent("收到串口数据：\n$text"))
		}
		subscribe<Events.SendSerialDataEvent> {
			println("TX---------------------------")
			onTxEvent(it)
		}
	}
	 */

}
