package com.amware.meterkit.service;

import com.amware.meterkit.entity.MsdCurrentCumulativeData;
import com.amware.meterkit.entity.MsdDebuggingUiData;
import com.amware.meterkit.entity.MsdFlowData;
import com.amware.meterkit.entity.MsdPreciseFlowData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <h1>操作水表的服务</h1>
 * 通过此服务，可对水表进行各种数据的读写。
 *
 * <h2>关于水表的地址</h2>
 * 每个水表在出厂的时候，都会赋予一个唯一地址。
 * 此地址是7个字节的十六进制数，但取值只会用0-9，不会出现A-F，也就是BCD码。
 * 特殊地址 AA AA AA AA AA AA AA 表示广播。
 * 地址分成两段，前面3个字节是厂商前缀，后面4个字节由厂商自行写入。
 * 我们公司的厂商前缀是：11 11 00 ，前台程序无法修改（当然，实际上可以在配置文件中修改）。
 * 使用本服务，传入的地址参数可以是7个字节的完整地址；也可以是4个字节的简略地址，会自动补前缀；
 * 也可以不传入地址参数，会自动使用广播地址。
 * 使用广播地址的时候，应确保串口上只连接了一个水表，否则会乱套。
 */
@RestController
@CrossOrigin
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
	private static final String CURRENT_CUMULATIVE_DATA = "/current-cumulative-data";
	private static final String START_TESTING = "/start-testing";

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

	/**
	 * 写当前数据
	 *
	 * @param currentCumulativeData 当前累计数据
	 */
	@PostMapping(CURRENT_CUMULATIVE_DATA)
	@RequestEntry(value = SERVICE_ENTRY + CURRENT_CUMULATIVE_DATA, method = RequestMethod.POST)
	public void writeCurrentCumulativeData(
			@RequestBody MsdCurrentCumulativeData currentCumulativeData) {
		serviceKt.writeCurrentCumulativeData(currentCumulativeData);
	}

	/**
	 * 启动检定
	 *
	 * @param address 水表地址，若缺席表示广播。
	 */
	@PostMapping(START_TESTING)
	@RequestEntry(value = SERVICE_ENTRY + START_TESTING, method = RequestMethod.POST)
	public void startTesting(@RequestParam(required = false) String address) {
		serviceKt.startTesting(address);
	}

}
