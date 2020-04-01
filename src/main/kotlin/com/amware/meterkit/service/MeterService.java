package com.amware.meterkit.service;

import com.amware.meterkit.entity.MsdFlowData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <h1>操作水表的服务</h1>
 */
@RestController
@RequestMapping(MeterService.CLASS_ENTRY)
public class MeterService {

	static final String SERVER_PORT = "7406";
	static final String LOCAL_HOST_PREFIX = "http://localhost";
	static final String CLASS_ENTRY = "/meter-service";
	private static final String SERVICE_ENTRY = LOCAL_HOST_PREFIX + ":" + SERVER_PORT + CLASS_ENTRY;

	private final MeterServiceKt serviceKt;

	private static final String FLOW_DATA = "/flow-data";

	@Autowired
	public MeterService(MeterServiceKt serviceKt) {
		this.serviceKt = serviceKt;
	}

	@GetMapping(FLOW_DATA)
	@RequestEntry(value = SERVICE_ENTRY + FLOW_DATA, method = RequestMethod.GET)
	public MsdFlowData getFlowData(String address) {
		return serviceKt.getFlowData(address);
	}

}
