package com.amware.meterkit.service;

import com.amware.meterkit.mbus.SerialPortMan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <h1>操作水表的服务</h1>
 * 入口为：{@value SERVICE_ENTRY}
 */
@RestController
@RequestMapping(MeterService.SERVICE_ENTRY)
public class MeterService {

	static final String SERVER_PORT = "7406";
	static final String LOCAL_HOST_PREFIX = "http://localhost";
	static final String CLASS_ENTRY = "/meter-service";
	//	static final String SERVICE_ENTRY2 = LOCAL_HOST_PREFIX + ":" + SERVER_PORT + CLASS_ENTRY;
	static final String SERVICE_ENTRY = LOCAL_HOST_PREFIX + ":" + SERVER_PORT + CLASS_ENTRY;

	@GetMapping
	public List<String> enumSerialPorts() {
		return SerialPortMan.INSTANCE.findCommPorts();
	}

}
