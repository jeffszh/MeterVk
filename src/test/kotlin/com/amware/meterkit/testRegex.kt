package com.amware.meterkit

fun main() {
	val input = "  12 34  56 78    ".trim()
	println(
			if (Regex("([0-9]{2} +)*").matches("$input ")) {
				"matched"
			} else {
				"bad"
			}
	)
}
