package com.amware.meterkit.entity;

/**
 * <h1>高精度流量数据</h1>
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class MsdPreciseFlowData extends MeterServiceData {

	/**
	 * 累计冷量（这个数值在水表中是无意义的，其他仪表才会有用，只是保持数据格式的一致性。下同）
	 */
	public double sumOfCooling;
	/**
	 * 累计冷量的单位
	 */
	public String unit1;

	/**
	 * 累计热量（无意义）
	 */
	public double sumOfHeat;
	/**
	 * 累计热量的单位
	 */
	public String unit2;

	/**
	 * 瞬时功率（无意义）
	 */
	public double instantPower;
	/**
	 * 瞬时功率的单位
	 */
	public String unit3;

	/**
	 * 瞬时流量
	 */
	public double instantFlow;
	/**
	 * 瞬时流量的单位
	 */
	public String unit4;

	/**
	 * 累计流量
	 */
	public double sumOfFlow;
	/**
	 * 累计流量的单位
	 */
	public String unit5;

	public MsdPreciseFlowData() {
	}

	public MsdPreciseFlowData(
			String address,
			double sumOfCooling, String unit1,
			double sumOfHeat, String unit2,
			double instantPower, String unit3,
			double instantFlow, String unit4,
			double sumOfFlow, String unit5) {
		this.address = address;
		this.sumOfCooling = sumOfCooling;
		this.unit1 = unit1;
		this.sumOfHeat = sumOfHeat;
		this.unit2 = unit2;
		this.instantPower = instantPower;
		this.unit3 = unit3;
		this.instantFlow = instantFlow;
		this.unit4 = unit4;
		this.sumOfFlow = sumOfFlow;
		this.unit5 = unit5;
	}

}
