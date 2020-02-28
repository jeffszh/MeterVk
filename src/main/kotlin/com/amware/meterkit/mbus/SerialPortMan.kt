package com.amware.meterkit.mbus

import gnu.io.CommPortIdentifier
import gnu.io.PortInUseException
import gnu.io.SerialPort
import gnu.io.UnsupportedCommOperationException
import javafx.scene.control.Alert
import java.util.*

object SerialPortMan {

	private var serialPort: SerialPort? = null
	private val commPortIdentifierMap = sortedMapOf<String, CommPortIdentifier>()

	fun findCommPorts(): List<String> {
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

	fun closeSerialPort() {
		serialPort?.close()
		serialPort = null
	}

	fun openSerialPort(portName: String, baudRate: Int, dataBits: Int, stopBits: Int, parity: Int): Boolean {
		closeSerialPort()

		val portIdentifier = commPortIdentifierMap[portName] ?: return false

		if (portIdentifier.isCurrentlyOwned) {
			Alert(Alert.AlertType.ERROR, "端口被占用。").showAndWait()
			return false
		}

		try {
			// 第一个参数是使用者的名字还是什么？第二个参数应该是超时时间。
			val commPort = portIdentifier.open(javaClass.simpleName, 1500)
			if (commPort is SerialPort) {
				serialPort = commPort
			} else {
				commPort.close()
				Alert(Alert.AlertType.ERROR, "不是串行口。").showAndWait()
				return false
			}
		} catch (e: PortInUseException) {
			e.printStackTrace()
			Alert(Alert.AlertType.ERROR, "端口被占用。").showAndWait()
			return false
		}

		try {
			with(serialPort!!) {
				setSerialPortParams(baudRate, dataBits, stopBits, parity)
//				addEventListener {
//					MbusReceiver.onSerialEvent(this@with, it)
//				}
				notifyOnDataAvailable(true)
				notifyOnParityError(true)
				return true
			}
		} catch (e: UnsupportedCommOperationException) {
			e.printStackTrace()
			Alert(Alert.AlertType.ERROR, "参数错误。").showAndWait()
		} catch (e: TooManyListenersException) {
			e.printStackTrace()
		}

		closeSerialPort()
		return false
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
