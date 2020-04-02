package com.amware.meterkit.entity;

/**
 * <h1>流量数据</h1>
 * 此为水表最基本的数据，包括累计的正负流量和水表内的系统时间。<br><br>
 * <p>
 * 注：关于单位，水表协议对单位的定义如下表，我们传递参数时单位使用字符串，即下表中双引号的部分。
 * <ul>
 * <li>WattHour(0x02, "Wh")</li>
 * <li>KiloWattHour(0x05, "KWh")</li>
 * <li>MegaWattHour(0x08, "MWh")</li>
 * <li>HundredMegaWattHour(0x0A, "MWh x 100")</li>
 * <li>Joule(0x01, "J")</li>
 * <li>KiloJoule(0x0B, "KJ")</li>
 * <li>MegaJoule(0x0E, "MJ")</li>
 * <li>GigaJoule(0x11, "GJ")</li>
 * <li>HundredGigaJoule(0x13, "GJ x 100")</li>
 * <li>Watt(0x14, "W")</li>
 * <li>KiloWatt(0x17, "KW")</li>
 * <li>MegaWatt(0x1A, "MW")</li>
 * <li>Litre(0x29, "L")</li>
 * <li>CubicMeter(0x2C, "m3")</li>
 * <li>LitrePerHour(0x32, "L/h")</li>
 * <li>CubicMeterPerHour(0x35, "m3/h")</li>
 * </ul>
 * 其他水表数据中凡是涉及到单位的，也是同样如此，不再赘述。
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class MsdFlowData extends MeterServiceData {

	/**
	 * 累计流量
	 */
	public double sumOfFlow;

	/**
	 * 累计流量的单位
	 */
	public String unit1;

	/**
	 * 负累计流量
	 */
	public double negSumOfFlow;

	/**
	 * 负累计流量的单位
	 */
	public String unit2;

	/**
	 * 水表内的实时时钟
	 */
	public String realTimeClock;

	/**
	 * <h1>标志位</h1>
	 * <ul>
	 * <li>b1b0 – 阀门状态，00开，01关，11异常</li>
	 * <li>b2   – 电池电压，0正常，1欠压</li>
	 * <li>b3   – 阀门电池电压，0正常，1欠压</li>
	 * <li>b4   – 阀门电子铅封，0正常，1被打开过</li>
	 * <li>b5   – 水表电子铅封，0正常，1被打开过</li>
	 * <li>b6   – 温度传感器，0正常，1故障</li>
	 * <li>b7   – 阀门响应标志位，0无响应，1有响应"</li>
	 * </ul>
	 */
	public byte bitField;

	/**
	 * 保留<br>
	 * 保持全0
	 */
	public byte reserved;

	public MsdFlowData() {
	}

	public MsdFlowData(
			String address,
			double sumOfFlow, String unit1, double negSumOfFlow, String unit2,
			String realTimeClock, byte bitField, byte reserved) {
		this.address = address;
		this.sumOfFlow = sumOfFlow;
		this.unit1 = unit1;
		this.negSumOfFlow = negSumOfFlow;
		this.unit2 = unit2;
		this.realTimeClock = realTimeClock;
		this.bitField = bitField;
		this.reserved = reserved;
	}

}
