package com.amware.meterkit.service

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import java.io.IOException

@ControllerAdvice
class CustomExceptionHandler {

	@ExceptionHandler(CustomException::class)
	fun handleCustomException(ex: CustomException): ResponseEntity<Map<String, Any>> {
		println("##### ##### ##### ##### ##### ###############################")
		val result: MutableMap<String, Any> = HashMap()
		result["exceptionClass"] = CustomException::class.simpleName!!
		result["errorMessage"] = ex.message ?: ex.toString()
		return ResponseEntity(result, HttpStatus.INTERNAL_SERVER_ERROR)
	}

}

class CustomException(msg: String) : Exception(msg)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class BadRequestException(msg: String) : IOException(msg)
