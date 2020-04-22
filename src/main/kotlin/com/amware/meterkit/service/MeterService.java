package com.amware.meterkit.service;

import com.amware.meterkit.entity.*;
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
	private static final String STANDARD_TIME = "/standard-time";
	private static final String METER_NUM_ADDRESS = "/meter-num-address";
	private static final String FACTORY_DATE = "/factory-date";
	private static final String WORK_MODE = "/work-mode";
	private static final String FLOW_CORRECTION = "/flow-correction";
	private static final String METER_PARAMS = "/meter-params";
	private static final String USM_TEST = "/usm-test";

	@SuppressWarnings("unused")
	private static final Object SUCCESS = new Object() {
		public int status = 200;
		public String message = "success";
	};

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
	 * @return 若成功，返回SUCCESS
	 */
	@PostMapping(CURRENT_CUMULATIVE_DATA)
	@RequestEntry(value = SERVICE_ENTRY + CURRENT_CUMULATIVE_DATA, method = RequestMethod.POST)
	public Object writeCurrentCumulativeData(
			@RequestBody MsdCurrentCumulativeData currentCumulativeData) {
		serviceKt.writeCurrentCumulativeData(currentCumulativeData);
		return SUCCESS;
	}

	/**
	 * 启动检定
	 *
	 * @param address 水表地址，若缺席表示广播。
	 * @return 若成功，返回SUCCESS
	 */
	@PostMapping(START_TESTING)
	@RequestEntry(value = SERVICE_ENTRY + START_TESTING, method = RequestMethod.POST)
	public Object startTesting(@RequestParam(required = false) String address) {
		serviceKt.startTesting(address);
		return SUCCESS;
	}

	/**
	 * 读标准时间
	 *
	 * @param address 水表地址，若缺席表示广播。
	 * @return 标准时间数据
	 */
	@GetMapping(STANDARD_TIME)
	@RequestEntry(value = SERVICE_ENTRY + STANDARD_TIME, method = RequestMethod.GET)
	public MsdStandardTimeData readStandardTime(@RequestParam(required = false) String address) {
		return serviceKt.readStandardTime(address);
	}

	/**
	 * 写标准时间
	 *
	 * @param standardTimeData 标准时间数据
	 * @return 若成功，返回SUCCESS
	 */
	@PostMapping(STANDARD_TIME)
	@RequestEntry(value = SERVICE_ENTRY + STANDARD_TIME, method = RequestMethod.POST)
	public Object writeStandardTime(@RequestBody MsdStandardTimeData standardTimeData) {
		serviceKt.writeStandardTime(standardTimeData);
		return SUCCESS;
	}

	/**
	 * 读取表号地址（只包括可写入的后4个字节）
	 *
	 * @param address 水表地址，若缺席表示广播。
	 * @return 表号地址数据
	 */
	@GetMapping(METER_NUM_ADDRESS)
	@RequestEntry(value = SERVICE_ENTRY + METER_NUM_ADDRESS, method = RequestMethod.GET)
	public MsdMeterNumAddressData readMeterNumAddress(@RequestParam(required = false) String address) {
		return serviceKt.readMeterNumAddress(address);
	}

	/**
	 * 设置表号地址
	 *
	 * @param meterNumAddressData 表号地址数据
	 * @return 若成功，返回SUCCESS
	 */
	@PostMapping(METER_NUM_ADDRESS)
	@RequestEntry(value = SERVICE_ENTRY + METER_NUM_ADDRESS, method = RequestMethod.POST)
	public Object writeMeterNumAddress(@RequestBody MsdMeterNumAddressData meterNumAddressData) {
		serviceKt.writeMeterNumAddress(meterNumAddressData);
		return SUCCESS;
	}

	/**
	 * 读出厂日期
	 *
	 * @param address 水表地址，若缺席表示广播。
	 * @return 出厂日期数据
	 */
	@GetMapping(FACTORY_DATE)
	@RequestEntry(value = SERVICE_ENTRY + FACTORY_DATE, method = RequestMethod.GET)
	public MsdFactoryDateData readFactoryDate(@RequestParam(required = false) String address) {
		return serviceKt.readFactoryDate(address);
	}

	/**
	 * 写出厂日期
	 *
	 * @param factoryDateData 出厂日期数据
	 * @return 若成功，返回SUCCESS
	 */
	@PostMapping(FACTORY_DATE)
	@RequestEntry(value = SERVICE_ENTRY + FACTORY_DATE, method = RequestMethod.POST)
	public Object writeFactoryDate(@RequestBody MsdFactoryDateData factoryDateData) {
		serviceKt.writeFactoryDate(factoryDateData);
		return SUCCESS;
	}

	/**
	 * <h1>转换运行模式</h1>
	 * 水表有两种运行模式：正常运行模式和工厂模式，此功能用于切换这两种模式。<br>
	 * 在工厂模式下，才能执行诸如“设置表号地址”、“写当前累计数据”等操作，在正常运行模式下，一般只能执行读操作。
	 * 在进行模式切换后，水表硬件会重启，并停止响应一段时间，大约几秒到十几秒，视乎具体的水表硬件，
	 * 因此切换模式后，必须稍等才能再执行别的操作。<br>
	 * 另外，此功能为只写操作，就是说无法通过API不改变运行模式而获知当前运行模式。
	 * 不过，在水表本身的显示面板上，可以看到当前处于什么模式。
	 *
	 * @param workModeData 运行模式数据
	 * @return 当前运行模式
	 */
	@PostMapping(WORK_MODE)
	@RequestEntry(value = SERVICE_ENTRY + WORK_MODE, method = RequestMethod.POST)
	public MsdWorkModeData changeWorkMode(@RequestBody MsdWorkModeData workModeData) {
		return serviceKt.changeWorkMode(workModeData);
	}

	/**
	 * <h1>设置流量修正</h1>
	 * 注意：此功能为只写操作，不提供读。
	 * 不过，在读调试界面数据{@link MsdDebuggingUiData}中可看到修正值，
	 * 建议上层的界面用{@link #getDebuggingUiData}立即获取刚写入的数据。
	 *
	 * @param flowCorrectionData 流量修正数据
	 * @return 若成功，返回SUCCESS
	 */
	@PostMapping(FLOW_CORRECTION)
	@RequestEntry(value = SERVICE_ENTRY + FLOW_CORRECTION, method = RequestMethod.POST)
	public Object setFlowCorrection(@RequestBody MsdFlowCorrectionData flowCorrectionData) {
		serviceKt.setFlowCorrection(flowCorrectionData);
		return SUCCESS;
	}

	/**
	 * <h1>开始USM测试</h1>
	 * 注：在开始USM测试后，水表硬件会执行一段时间，这段时间水表也会有响应，
	 * 若此时取测试结果则会显示正在测试中。
	 *
	 * @param address 水表地址，若缺席表示广播。
	 * @return 若成功，返回SUCCESS
	 */
	@PostMapping(USM_TEST)
	@RequestEntry(value = SERVICE_ENTRY + USM_TEST, method = RequestMethod.POST)
	public Object startUsmTest(@RequestParam(required = false) String address) {
		serviceKt.startUsmTest(address);
		return SUCCESS;
	}

	/**
	 * 读USM测试结果
	 *
	 * @param address 水表地址，若缺席表示广播。
	 * @return USM测试结果
	 */
	@GetMapping(USM_TEST)
	@RequestEntry(value = SERVICE_ENTRY + USM_TEST, method = RequestMethod.GET)
	public MsdUsmTestResult readUsmTestResult(@RequestParam(required = false) String address) {
		return serviceKt.readUsmTestResult(address);
	}

	/**
	 * 读水表参数
	 *
	 * @param address 水表地址，若缺席表示广播。
	 * @return 水表参数
	 */
	@GetMapping(METER_PARAMS)
	@RequestEntry(value = SERVICE_ENTRY + METER_PARAMS, method = RequestMethod.GET)
	public MsdMeterParams readMeterParams(@RequestParam(required = false) String address) {
		return serviceKt.readMeterParams(address);
	}

	/**
	 * 写水表参数
	 *
	 * @param meterParams 水表参数
	 * @return 若成功，返回SUCCESS
	 */
	@PostMapping(METER_PARAMS)
	@RequestEntry(value = SERVICE_ENTRY + METER_PARAMS, method = RequestMethod.POST)
	public Object writeMeterParams(@RequestBody MsdMeterParams meterParams) {
		serviceKt.writeMeterParams(meterParams);
		return SUCCESS;
	}

}
