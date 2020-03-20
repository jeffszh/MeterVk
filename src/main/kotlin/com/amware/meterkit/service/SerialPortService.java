package com.amware.meterkit.service;

import com.amware.meterkit.entity.SerialPortInfo;
import com.amware.meterkit.mbus.SerialPortMan;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <h1>串口相关服务</h1>
 * 包括读取单个或所有串口的状态，打开或关闭某个串口。
 */
@RestController
@RequestMapping(SerialPortService.CLASS_ENTRY)
public class SerialPortService {

	static final String CLASS_ENTRY = "/serial-port";
	private static final String SERVICE_ENTRY = MeterService.LOCAL_HOST_PREFIX + ":" +
			MeterService.SERVER_PORT + CLASS_ENTRY;

	/**
	 * <h1>列出所有串口</h1>
	 * 返回的数组的元素的结构是 {@link SerialPortInfo}。
	 * 数组中只会有一个串口处于打开的状态，其波特率、数据位、停止位等信息有意义，
	 * 其他处于关闭状态的串口，只有串口名有意义。
	 *
	 * @return 系统中存在的所有串口的信息
	 */
	@RequestEntry(value = SERVICE_ENTRY, method = RequestMethod.GET)
	@GetMapping
	public List<SerialPortInfo> listSerialPorts() {
		SerialPortMan.INSTANCE.findCommPorts();
		return SerialPortMan.INSTANCE.listSerialPortsInfo();
	}

	/**
	 * <h1>获取单个串口的信息</h1>
	 * 若串口处于打开状态，其波特率、数据位、停止位、奇偶校验等信息有意义，
	 * 否则只有串口名有意义。
	 * 若请求的串口名是不存在的，则出错。
	 *
	 * @param portName 串口名
	 * @return 串口信息
	 */
	@RequestEntry(value = SERVICE_ENTRY + "/{id}", method = RequestMethod.GET)
	@GetMapping("/{id}")
	public SerialPortInfo getSerialPort(
			@PathVariable("id") String portName) {
		return SerialPortMan.INSTANCE.querySerialPortInfo(portName);
	}

	/**
	 * <h1>打开或关闭串口</h1>
	 * 传入参数的 active 为 true，表示打开串口，此时串口名以及波特率等所有参数必须设置为合法值，否则出错；
	 * 当传入参数的 active 为 false，表示关闭串口，此时只有串口名是有用的，其他参数可以缺席。
	 *
	 * @param portInfo 串口参数
	 * @return 操作后串口的实际状态
	 */
	@RequestEntry(value = SERVICE_ENTRY, method = RequestMethod.POST)
	@PostMapping
	public SerialPortInfo postSerialPort(
			@RequestBody SerialPortInfo portInfo) {
		SerialPortMan.INSTANCE.closeSerialPort();
		if (portInfo.active) {
			SerialPortMan.INSTANCE.openSerialPort(
					portInfo.name,
					portInfo.baudRate,
					portInfo.dataBits,
					portInfo.stopBits,
					portInfo.parity);
		}
		return getSerialPort(portInfo.name);
	}

}
