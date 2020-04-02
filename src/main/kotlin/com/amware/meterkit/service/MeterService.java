package com.amware.meterkit.service;

import com.amware.meterkit.entity.MsdFlowData;
import com.amware.meterkit.entity.MsdPreciseFlowData;
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
	private static final String PRECISE_FLOW_DATA = "/precise-flow-data";

	@Autowired
	public MeterService(MeterServiceKt serviceKt) {
		this.serviceKt = serviceKt;
	}

	/**
	 * 读流量数据
	 * @param address 水表地址，若缺席表示广播。
	 * @return 流量数据
	 * @see MsdFlowData
	 */
	@GetMapping(FLOW_DATA)
	@RequestEntry(value = SERVICE_ENTRY + FLOW_DATA, method = RequestMethod.GET)
	public MsdFlowData getFlowData(@RequestParam String address) {
		return serviceKt.getFlowData(address);
	}

	/**
	 * 读高精度流量数据
	 * @param address 水表地址，若缺席表示广播。
	 * @return 高精度流量数据
	 * @see  MsdPreciseFlowData
	 */
	@GetMapping(PRECISE_FLOW_DATA)
	@RequestEntry(value = SERVICE_ENTRY + PRECISE_FLOW_DATA, method = RequestMethod.GET)
	public MsdPreciseFlowData getPreciseFlowData(@RequestParam String address) {
		return serviceKt.getPreciseFlowData(address);
	}

}
