package com.amware.meterkit.entity;

/**
 * <h1>水表服务数据</h1>
 * 水表服务所用到的数据的基类。
 * 水表服务所涉及的数据全部以此为基类，并且以“Msd”作为类名前缀，例如{@link MsdFlowData}。
 * 本类只包含一个字段：{@link #address}
 */
public class MeterServiceData {

	/**
	 * <h1>地址</h1>
	 * 地址为7个字节的16进制数（通常用BCD码），为方便参数传递，此处使用字符串形式。
	 * 每个水表（或其他类似设备）在出厂前都会赋予一个唯一地址，具体规则依据厂商前缀等等而定。
	 * 特殊地址：{@code "AA AA AA AA AA AA AA"}表示广播。
	 * 在调用服务传入参数时，地址若为空或缺席，则自动使用广播地址。
	 */
	public String address;

}
