package com.amware.meterkit.service;

import com.amware.meterkit.entity.MsdDebuggingUiData;
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
	private static final String DEBUGGING_UI_DATA = "/debugging-ui-data";

	@Autowired
	public MeterService(MeterServiceKt serviceKt) {
		this.serviceKt = serviceKt;
	}

	/**
	 * 读流量数据
	 *
	 * @param address 水表地址，若缺席表示广播。
	 * @return 流量数据
	 * @see MsdFlowData
	 */
	@GetMapping(FLOW_DATA)
	@RequestEntry(value = SERVICE_ENTRY + FLOW_DATA, method = RequestMethod.GET)
	public MsdFlowData getFlowData(@RequestParam(required = false) String address) {
		return serviceKt.getFlowData(address);
	}

	/**
	 * 读高精度流量数据
	 *
	 * @param address 水表地址，若缺席表示广播。
	 * @return 高精度流量数据
	 * @see MsdPreciseFlowData
	 */
	@GetMapping(PRECISE_FLOW_DATA)
	@RequestEntry(value = SERVICE_ENTRY + PRECISE_FLOW_DATA, method = RequestMethod.GET)
	public MsdPreciseFlowData getPreciseFlowData(@RequestParam(required = false) String address) {
		return serviceKt.getPreciseFlowData(address);
	}

	/**
	 * 读调试界面数据
	 *
	 * @param address 水表地址，若缺席表示广播。
	 * @return 调试界面数据
	 * @see MsdDebuggingUiData
	 */
	@GetMapping(DEBUGGING_UI_DATA)
	@RequestEntry(value = SERVICE_ENTRY + DEBUGGING_UI_DATA, method = RequestMethod.GET)
	public MsdDebuggingUiData getDebuggingUiData(@RequestParam(required = false) String address) {
		return serviceKt.getDebuggingUiData(address);
	}

}
