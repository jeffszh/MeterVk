package com.amware.meterkit.entity;

/**
 * <h1>运行模式数据</h1>
 * 表示正常运行或工厂模式的数据。
 */
public class MsdWorkModeData extends MeterServiceData {

	/**
	 * <ul>
	 * <li>false - 正常运行模式</li>
	 * <li>true - 工厂模式</li>
	 * </ul>
	 */
	public boolean factoryMode;

}
