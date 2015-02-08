package com.alex.antdemo.antctrl;

import com.dsi.ant.plugins.antplus.pccbase.AsyncScanController.AsyncScanResultDeviceInfo;

/**
 * Ant连接状态监听
 * @author caisenchuan
 *
 */
public interface AntDeviceControlListener {

	/**
	 * 找到新的设备
	 */
	public void onDeviceFound(AsyncScanResultDeviceInfo device);
	
	/**
	 * 连接成功
	 */
	public void onConnectSuccess(AsyncScanResultDeviceInfo device);
	
	/**
	 * 连接失败
	 */
	public void onConnectFaild(AsyncScanResultDeviceInfo device);
	
}
