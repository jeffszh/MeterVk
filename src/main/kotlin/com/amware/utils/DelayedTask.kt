package com.amware.utils

class DelayedTask private constructor(delayTime: Int, task: () -> Unit) {

	private val thread: Thread = Thread {
		try {
			Thread.sleep(delayTime.toLong())
			task()
		} catch (e: InterruptedException) {
			// 不要打印 e.printStackTrace();
		}
	}

	fun abort() {
		thread.interrupt()
	}

	companion object {
		fun createAndStart(delayTime: Int, task: () -> Unit): DelayedTask {
			val delayedTask = DelayedTask(delayTime, task)
			delayedTask.thread.start()
			return delayedTask
		}
	}

}
