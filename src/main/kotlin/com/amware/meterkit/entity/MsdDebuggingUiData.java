package com.amware.meterkit.entity;

/**
 * 调试界面数据
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class MsdDebuggingUiData extends MeterServiceData {

	/**
	 * 程序版本号，取值范围：XXXX
	 */
	public int programVersion;

	/**
	 * 供回水标志，true-供水，false-回水
	 */
	public boolean supplyReturnFlag;

	/**
	 * 仪表系数，取值范围：X.XXX
	 */
	public double instrumentFactor;

	/**
	 * 低流速切除，取值范围：XX.XX
	 */
	public double lowFlowExcision;

	/**
	 * 流量补偿值1，单位：%
	 */
	public double flowCompensation1;

	/**
	 * 流量补偿值2，单位：%
	 */
	public double flowCompensation2;

	/**
	 * 流量补偿值3，单位：%
	 */
	public double flowCompensation3;

	/**
	 * 流量补偿值4，单位：%
	 */
	public double flowCompensation4;

	/**
	 * 供水温度补偿值，取值范围：XX.XX，单位：℃
	 */
	public double supplyingTemperatureCompensation;

	/**
	 * 回水温度补偿值，取值范围：XX.XX，单位：℃
	 */
	public double returningTemperatureCompensation;

	/**
	 * 启用日期，格式：YYMMDD
	 */
	public String introductionDate;

	/**
	 * 口径，取值范围：XXXX，单位：DN
	 */
	public double caliber;

	public MsdDebuggingUiData() {
	}

	public MsdDebuggingUiData(
			String address,
			int programVersion,
			boolean supplyReturnFlag,
			double instrumentFactor, double lowFlowExcision,
			double flowCompensation1, double flowCompensation2, double flowCompensation3, double flowCompensation4,
			double supplyingTemperatureCompensation, double returningTemperatureCompensation,
			String introductionDate, double caliber) {
		this.address = address;
		this.programVersion = programVersion;
		this.supplyReturnFlag = supplyReturnFlag;
		this.instrumentFactor = instrumentFactor;
		this.lowFlowExcision = lowFlowExcision;
		this.flowCompensation1 = flowCompensation1;
		this.flowCompensation2 = flowCompensation2;
		this.flowCompensation3 = flowCompensation3;
		this.flowCompensation4 = flowCompensation4;
		this.supplyingTemperatureCompensation = supplyingTemperatureCompensation;
		this.returningTemperatureCompensation = returningTemperatureCompensation;
		this.introductionDate = introductionDate;
		this.caliber = caliber;
	}

}
