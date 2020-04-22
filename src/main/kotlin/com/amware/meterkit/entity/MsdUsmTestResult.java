package com.amware.meterkit.entity;

/**
 * <h1>USM测试结果</h1>
 */
@SuppressWarnings("WeakerAccess")
public class MsdUsmTestResult extends MeterServiceData {

	/**
	 * <h1>测试状态</h1>
	 * <ul>
	 * <li>0x00 - 测试完成</li>
	 * <li>0x01 - 测试中</li>
	 * </ul>
	 */
	public byte testStatus;

	/**
	 * <h1>故障代码1</h1>
	 * <ul>
	 * <li>00000001b - TIMEOUT_UP</li>
	 * <li>00000010b - TIMEOUT_DOWN</li>
	 * <li>00000100b - PW1ST_UP</li>
	 * <li>00001000b - PW1ST_DOWN</li>
	 * </ul>
	 */
	public byte errorCode1;

	/**
	 * <h1>故障代码2</h1>
	 */
	public byte errorCode2;

	/**
	 * <h1>PW1ST_UP</h1>
	 * 取值范围：0-999999.99
	 */
	public double pw1stUp;

	/**
	 * <h1>PW1ST_DOWN</h1>
	 * 取值范围：0-999999.99
	 */
	public double pw1stDown;

	/**
	 * <h1>ToF_UP</h1>
	 * 取值范围：0-999999.99，单位：uS
	 */
	public double tofUp;

	/**
	 * <h1>ToF_DOWN</h1>
	 * 取值范围：0-999999.99，单位：uS
	 */
	public double tofDown;

	/**
	 * <h1>dTof</h1>
	 * 取值范围：0-999999.99，单位：nS
	 */
	public double dTof;

	/**
	 * <h1>dTof_filt</h1>
	 * 取值范围：0-999999.99，单位：nS
	 */
	public double dTofFilt;

	public MsdUsmTestResult(
			String address,
			byte testStatus,
			byte errorCode1,
			byte errorCode2,
			double pw1stUp,
			double pw1stDown,
			double tofUp,
			double tofDown,
			double dTof,
			double dTofFilt) {
		this.address = address;
		this.testStatus = testStatus;
		this.errorCode1 = errorCode1;
		this.errorCode2 = errorCode2;
		this.pw1stUp = pw1stUp;
		this.pw1stDown = pw1stDown;
		this.tofUp = tofUp;
		this.tofDown = tofDown;
		this.dTof = dTof;
		this.dTofFilt = dTofFilt;
	}

}
