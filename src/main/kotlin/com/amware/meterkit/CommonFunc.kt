package com.amware.meterkit

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONException
import com.amware.meterkit.service.CustomException
import com.vaadin.flow.component.notification.Notification
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.DefaultResponseErrorHandler
import org.springframework.web.client.RestTemplate
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

const val SERVER_PORT = 7406

fun showToast(text: String, duration: Int = 3000) {
	Notification.show(text, duration, Notification.Position.MIDDLE)
}

val myRestTemplate = RestTemplate().apply {
	errorHandler = object : DefaultResponseErrorHandler() {

		override fun handleError(response: ClientHttpResponse) {
			try {
				val body = convertStreamToString(response.body)
				println("body=$body")
				try {
					val jsonObj = JSON.parseObject(body)
					if (jsonObj["exceptionClass"] == CustomException::class.simpleName) {
						showToast("錯誤: ${jsonObj["errorMessage"]}", 5000)
					} else {
						showToast("status=${jsonObj["status"]}\r\n" +
								"message=${jsonObj["message"]}", 8000)
					}
					return
				} catch (e: JSONException) {
					e.printStackTrace()
				}

				super.handleError(response)
			} catch (e: Exception) {
				e.printStackTrace()
				showToast(e.message ?: e.toString())
			}
		}

		private fun convertStreamToString(inputStream: InputStream): String {
			val reader = BufferedReader(InputStreamReader(inputStream))
			val sb = StringBuilder()
			var line: String?
			try {
				while (reader.readLine().also { line = it } != null) {
					sb.append(line)
				}
			} catch (e: IOException) {
				e.printStackTrace()
			} finally {
				try {
					inputStream.close()
				} catch (e: IOException) {
					e.printStackTrace()
				}
			}
			return sb.toString()
		}

	}
}
