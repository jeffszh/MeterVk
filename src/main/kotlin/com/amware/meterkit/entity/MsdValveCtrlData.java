package com.amware.meterkit.entity;

/**
 * <h1>阀控数据</h1>
 * 包括控制和状态，上位机进行控制时只需要填{@link #valveCtrl}，
 * 其余的在读阀控状态的时候返回。
 */
public class MsdValveCtrlData extends MeterServiceData {

	/**
	 * <h1>阀门控制</h1>
	 * <ul>
	 * <li>0x00 - 关阀</li>
	 * <li>0x01 - 开阀</li>
	 * </ul>
	 */
	public Byte valveCtrl;

	/**
	 * <h1>阀门状态</h1>
	 * <ul>
	 * <li>0x00 水表不支持阀控</li>
	 * <li>0x01 已开</li>
	 * <li>0x02 正在开</li>
	 * <li>0x03 正在关</li>
	 * <li>0x04 已关</li>
	 * <li>0x05 故障</li>
	 * </ul>
	 */
	public Byte valveStatus;

	/**
	 * <h1>故障代码</h1>
	 * <ul>
	 * <li>00000001b 开阀超时</li>
	 * <li>00000010b 关阀超时</li>
	 * </ul>
	 */
	public Byte errorCode;

}
