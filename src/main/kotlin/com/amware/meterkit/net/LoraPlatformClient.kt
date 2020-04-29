package com.amware.meterkit.net

import cn.amware.app.net.NetUtils
import cn.amware.utils.DataUtils
import com.alibaba.fastjson.JSON
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

import java.io.IOException
import java.util.Comparator
import java.util.LinkedHashMap
import java.util.TreeMap

object LoraPlatformClient {

	private const val SECRET = "3611fa"
	private const val PLATFORM_URL = "http://182.61.56.51:8096/api"

	private fun generateAccessToken(params: LinkedHashMap<String, String>): String {
		val sb = StringBuilder()
		for (key in params.keys) {
			if (sb.isNotEmpty()) {
				sb.append("&")
			}
			sb.append(key).append("=").append(params[key])
		}
		println(sb)
		return NetUtils.makeMd5(sb.toString()).toLowerCase()
	}

	@Throws(IOException::class)
	fun createDevice(companyCode: String?, devEui: String, timeStamp: String = NetUtils.timeStamp): String {
		val allParams = LinkedHashMap<String, String>()
		val params = TreeMap<String, String>(Comparator.reverseOrder())
		params["dev_eui"] = DataUtils.noSpaceHexStr(devEui)
		params.forEach { allParams[it.key] = it.value }
		if (companyCode != null) {
			allParams["company_code"] = companyCode
		}
		allParams["timestamp"] = timeStamp
		allParams["secret"] = SECRET
		allParams["access_token"] = generateAccessToken(allParams)
		allParams.remove("secret")
		val paramsStr = JSON.toJSONString(allParams)
		println(paramsStr)

		HttpClients.createDefault().use { httpClient ->
			val post = HttpPost("$PLATFORM_URL/create_device")
			val entity = StringEntity(paramsStr)
			entity.setContentEncoding("UTF-8")
			entity.setContentType("application/json")
			post.entity = entity
			httpClient.execute(post).use { response -> return EntityUtils.toString(response.entity) }
		}
	}

}
