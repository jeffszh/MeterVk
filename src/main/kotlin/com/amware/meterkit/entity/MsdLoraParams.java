package com.amware.meterkit.entity;

public class MsdLoraParams extends MeterServiceData {

	/**
	 * <h1>机构代码</h1>
	 * 目前只有三个："amwares"、"test"、"zh"，若为空，则默认为"zh"。
	 */
	public String companyCode;

	/**
	 * <h1>设备EUI</h1>
	 * 必填项，必须为8字节（16个数字）的十六进制字符串。
	 */
	public String devEui;

	/**
	 * <h1>应用EUI</h1>
	 * 现在总是全零，不必理会。
	 */
	public String appEui;

	/**
	 * <h1>应用键值</h1>
	 * 由Lora管理平台内部生成，只读。
	 */
	public String appKey;

}
