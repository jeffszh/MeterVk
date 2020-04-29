package cn.amware.app.net

import cn.amware.utils.DataUtils

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Date

object NetUtils {

	val timeStamp: String
		get() = getTimeStamp(Date())

	private fun getTimeStamp(time: Date): String {
		return "" + time.time / 1000
	}

	fun makeMd5(input: String): String {
		try {
			val digest = MessageDigest.getInstance("MD5")
			val md5Bytes = digest.digest(input.toByteArray(StandardCharsets.UTF_8))
			return DataUtils.noSpaceHexStr(DataUtils.bytesToHexStr(*md5Bytes))
		} catch (e: NoSuchAlgorithmException) {
			e.printStackTrace()
			throw RuntimeException(e)    // 不可能
		}
	}

}
