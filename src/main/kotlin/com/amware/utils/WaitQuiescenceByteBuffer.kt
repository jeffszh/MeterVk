package com.amware.utils

import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * <h1>等待静态字节缓冲区</h1>
 * 主要用于串口分包，当间隔时间较短地连续填入数据，数据会保持在缓存中，
 * 当没有数据到达，超过指定时间，就一次性将之前存入的数据输出。
 */
class WaitQuiescenceByteBuffer(private val timeout: Int, private val onQuiescence: (ByteArray) -> Unit) {

	private val buffer = ByteArrayOutputStream()
	private var onQuiescenceTask = DUMMY_TASK

	@Synchronized
	fun putBytes(vararg bytes: Byte) {
		onQuiescenceTask.abort()
		try {
			buffer.write(bytes)
		} catch (e: IOException) {
			e.printStackTrace()
		}

		onQuiescenceTask = DelayedTask.createAndStart(timeout, this::finalOut)
	}

	@Synchronized
	private fun finalOut() {
		val bytes = buffer.toByteArray()
		buffer.reset()
		onQuiescence(bytes)
	}

	companion object {
		private val DUMMY_TASK = DelayedTask.createAndStart(1) { }
	}

}
