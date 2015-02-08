package com.alex.antdemo.antctrl;

/**
 * Ant连接状态监听
 * @author caisenchuan
 *
 */
public interface AntConnectListener {

	/**
	 * 连接成功
	 */
	public void onConnectSuccess();
	
	/**
	 * 连接失败
	 */
	public void onConnectFaild();
	
}
