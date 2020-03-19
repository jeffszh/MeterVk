package com.amware.meterkit.entity;

/**
 * <h1>串口信息</h1>
 * 用于描述一个串口的名称、参数和状态。
 */
@SuppressWarnings({"unused"})
public class SerialPortInfo {

	/**
	 * <h1>串口名</h1>
	 * 例如： "COM1", "ttyS0"之类。
	 */
	public String name;

	/**
	 * <h1>波特率</h1>
	 * 例如：2400、9600、115200等，必须是合法的数值否则报错。
	 */
	public int baudRate;

	/**
	 * <h1>数据位</h1>
	 * 通常是：8
	 */
	public int dataBits;

	/**
	 * <h1>停止位</h1>
	 * 通常是：1
	 */
	public int stopBits;

	/**
	 * <h1>奇偶校验</h1>
	 * 有5个可能值：
	 * <ul>
	 * <li>无  = 0</li>
	 * <li>奇  = 1</li>
	 * <li>偶  = 2</li>
	 * <li>常1 = 3</li>
	 * <li>常0 = 4</li>
	 * </ul>
	 * 在水表通讯中使用偶校验，即此参数应为2。
	 */
	public int parity;

	/**
	 * true - 串口打开
	 * false - 串口关闭
	 */
	public boolean active;

}
