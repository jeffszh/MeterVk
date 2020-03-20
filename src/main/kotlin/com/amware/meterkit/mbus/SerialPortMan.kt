package com.amware.meterkit.mbus

import com.amware.meterkit.entity.SerialPortInfo
import com.amware.meterkit.service.BadRequestException
import gnu.io.CommPortIdentifier
import gnu.io.PortInUseException
import gnu.io.SerialPort
import java.io.IOException
import java.util.*

object SerialPortMan {

	private var serialPort: SerialPort? = null
	private val commPortIdentifierMap = sortedMapOf<String, CommPortIdentifier>()

	init {
		findCommPorts()
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
		}
		println("串口参数已经设置。")
	}

	private fun nameIsSame(stdName: String, fullName: String): Boolean =
			fullName == stdName || fullName == "//./$stdName"

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
