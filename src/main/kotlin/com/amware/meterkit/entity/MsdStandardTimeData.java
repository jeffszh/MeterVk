package com.amware.meterkit.entity;

/**
 * <h1>标准时间数据</h1>
 * 用于读写水表内的标准时间。
 */
public class MsdStandardTimeData extends MeterServiceData {

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

	/**
	 * 时，0-23
	 */
	public int hour;

	/**
	 * 分，0-59
	 */
	public int minute;

	/**
	 * 秒，0-59
	 */
	public int second;

}
