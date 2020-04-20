package com.amware.meterkit.entity;

/**
 * <h1>“出厂日期”数据</h1>
 */
public class MsdFactoryDateData extends MeterServiceData {

	/**
	 * 年，1900-2099
	 */
	public int year;

	/**
	 * 月，1-12
	 */
	public int month;

	/**
	 * 日，1-31
	 */
	public int day;

}
