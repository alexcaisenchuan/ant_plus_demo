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

	public abstract void startScan();
	protected abstract void subscribeToDataEvents();
	
    protected IDeviceStateChangeReceiver base_IDeviceStateChangeReceiver =
    new IDeviceStateChangeReceiver() {
        @Override
        public void onDeviceStateChange(final DeviceState newDeviceState) {
        	Log.d(TAG, "onDeviceStateChange : " + newDeviceState);
        }
    };
    
    protected IPluginAccessResultReceiver<T> base_IPluginAccessResultReceiver =
    new IPluginAccessResultReceiver<T>() {
        @Override
        public void onResultReceived(T result, RequestAccessResult resultCode, DeviceState initialDeviceState) {
        	Log.d(TAG, "resultCode : " + resultCode + "," + result.getDeviceName() + " : " + initialDeviceState);
        	switch(resultCode) {
            	case SUCCESS: {
                    antPcc = result;
                    subscribeToDataEvents();
                    break;
            	}
            	
                default: {
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
}
