package com.amware.meterkit.entity;

/**
 * <h1>当前累积数据</h1>
 */
public class MsdCurrentCumulativeData extends MeterServiceData {

	/**
	 * 累计冷量（此数值对于水表无意义），取值范围：0-9999999999
	 */
	public long sumOfCooling;

	/**
	 * 累计冷量的单位
	 */
	public String unit1;

	/**
	 * 累计热量（此数值对于水表无意义），取值范围：0-9999999999
	 */
	public long sumOfHeat;

	/**
	 * 累计热量的单位
	 */
	public String unit2;

	/**
	 * 累积流量，取值范围：0-999999.99
	 */
	public double sumOfFlow;

	/**
	 * 累积流量的单位
	 */
	public String unit3;

}
