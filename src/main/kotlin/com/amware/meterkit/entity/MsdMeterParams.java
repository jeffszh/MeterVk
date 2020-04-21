package com.amware.meterkit.entity;

/**
 * 水表参数
 */
public class MsdMeterParams extends MeterServiceData {

	/**
	 * <h1>采样时间</h1>
	 * 取值范围：0-999999，单位：ms
	 */
	public int sampleTime;

	/**
	 * <h1>零漂</h1>
	 * 取值范围：0-999999.99，单位：后跟，通常是L/h
	 */
	public double zeroDrift;

	/**
	 * <h1>零漂的单位</h1>
	 * 通常是：L/h
	 */
	public String unit1;

	/**
	 * <h1>PW1ST阈值</h1>
	 * 取值范围：0-9.9
	 */
	public double pw1stThreshold;

	/**
	 * <h1>ToF上限</h1>
	 * 取值范围：0-999999.99，单位：uS
	 */
	public double tofUpperBound;

	/**
	 * <h1>ToF下限</h1>
	 * 取值范围：0-999999.99，单位：uS
	 */
	public double tofLowerBound;

	/**
	 * <h1>dToF最大值</h1>
	 * 取值范围：0-999999.99，单位：uS
	 */
	public double tofMax;

	/**
	 * <h1>起始流量</h1>
	 * 取值范围：0-999999.99，单位：后跟，通常是L/h
	 */
	public double initialFlow;

	/**
	 * <h1>起始流量的单位</h1>
	 * 通常是：L/h
	 */
	public String unit2;

	/**
	 * <h1>管段横向长度</h1>
	 * 取值范围：0-999999.99，单位：mm
	 */
	public double pipeHorizontalLength;

	/**
	 * <h1>管段纵向长度</h1>
	 * 取值范围：0-999999.99，单位：mm
	 */
	public double pipeVerticalLength;

	/**
	 * <h1>管段半径</h1>
	 * 取值范围：0-999999.99，单位：mm
	 */
	public double pipeRadius;

	public MsdMeterParams(
			String address,
			int sampleTime,
			double zeroDrift,
			String unit1,
			double pw1stThreshold,
			double tofUpperBound,
			double tofLowerBound,
			double tofMax,
			double initialFlow,
			String unit2,
			double pipeHorizontalLength,
			double pipeVerticalLength,
			double pipeRadius) {
		this.address = address;
		this.sampleTime = sampleTime;
		this.zeroDrift = zeroDrift;
		this.unit1 = unit1;
		this.pw1stThreshold = pw1stThreshold;
		this.tofUpperBound = tofUpperBound;
		this.tofLowerBound = tofLowerBound;
		this.tofMax = tofMax;
		this.initialFlow = initialFlow;
		this.unit2 = unit2;
		this.pipeHorizontalLength = pipeHorizontalLength;
		this.pipeVerticalLength = pipeVerticalLength;
		this.pipeRadius = pipeRadius;
	}

}
