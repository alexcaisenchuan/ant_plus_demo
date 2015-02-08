package com.alex.antdemo.antctrl;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.dsi.ant.plugins.antplus.pcc.defines.DeviceState;
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc;
import com.dsi.ant.plugins.antplus.pccbase.AsyncScanController;
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IDeviceStateChangeReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IPluginAccessResultReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AsyncScanController.AsyncScanResultDeviceInfo;

/**
 * 通用Ant设备处理
 * @author caisenchuan
 * @param <T> 必须继承自AntPluginPcc
 */
public abstract class AntBase <T extends AntPluginPcc> {
	
	private static final String TAG = AntBase.class.getSimpleName();

	protected ArrayList<AsyncScanResultDeviceInfo> mScannedDeviceInfos;
	protected Context context = null;
	
	protected T antPcc = null;
	protected AsyncScanController<T> antScanCtrl;
	protected PccReleaseHandle<T> antReleaseHandle = null;
	protected AntConnectListener connectListener = null;

	/*
	 * 需要子类实现的函数
	 */
	public abstract void startScan();
	protected abstract void subscribeToDataEvents();
	
	/**
	 * 设备状态监听接口
	 */
    protected IDeviceStateChangeReceiver base_IDeviceStateChangeReceiver =
    new IDeviceStateChangeReceiver() {
        @Override
        public void onDeviceStateChange(final DeviceState newDeviceState) {
        	Log.d(TAG, "onDeviceStateChange : " + newDeviceState);
        }
    };
    
    /**
     * 设备连接监听接口
     */
    protected IPluginAccessResultReceiver<T> base_IPluginAccessResultReceiver =
    new IPluginAccessResultReceiver<T>() {
        @Override
        public void onResultReceived(T result, RequestAccessResult resultCode, DeviceState initialDeviceState) {
        	Log.d(TAG, "resultCode : " + resultCode + "," + result.getDeviceName() + " : " + initialDeviceState);
        	switch(resultCode) {
            	case SUCCESS: {
                    antPcc = result;
                    subscribeToDataEvents();
                    if(connectListener != null) {
                    	connectListener.onConnectSuccess();
                    }
                    break;
            	}
            	
                default: {
                	if(connectListener != null) {
                    	connectListener.onConnectFaild();
                    }
                	break;
                }
            }
        }
    };
    
	/**
	 * 构造函数
	 * @param ctx
	 */
	public AntBase(Context ctx) {
		this.context = ctx;
		this.mScannedDeviceInfos = new ArrayList<AsyncScanResultDeviceInfo>();
	}
	
	/**
	 * 获得设备列表
	 * @return
	 */
	public ArrayList<AsyncScanResultDeviceInfo> getDevList() {
		return mScannedDeviceInfos;
	}
	
	/**
	 * 连接设备
	 * @param asyncScanResultDeviceInfo
	 */
    public void requestConnectToResult(final AsyncScanResultDeviceInfo asyncScanResultDeviceInfo) {
    	Log.d(TAG, "Connecting to " + asyncScanResultDeviceInfo.getDeviceDisplayName());
        antReleaseHandle = antScanCtrl.requestDeviceAccess(asyncScanResultDeviceInfo,
    		base_IPluginAccessResultReceiver, 
            base_IDeviceStateChangeReceiver
        );
    }
    
    /**
     * 关闭连接
     */
	public void close() {
    	if(antScanCtrl != null) {
    		antScanCtrl.closeScanController();
    	}
        if(antReleaseHandle != null) {
            antReleaseHandle.close();
        }
	}
	
	/**
	 * 设置连接监听函数
	 * @param listener
	 */
	public void setConnectListener(AntConnectListener listener) {
		connectListener = listener;
	}
}
