package com.alex.antdemo.antctrl;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.alex.antdemo.App;
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
	
	protected Context context = null;
	protected App mApp = null;
	
	protected T antPcc = null;
	protected AsyncScanController<T> antScanCtrl;
	protected PccReleaseHandle<T> antReleaseHandle = null;
	protected AntDeviceControlListener connectListener = null;
	
	/**
	 * 扫描到的设备
	 */
	protected ArrayList<AsyncScanResultDeviceInfo> mScannedDeviceInfos;
	/**
	 * 当前正在连接的设备
	 */
	protected AsyncScanResultDeviceInfo mCurrentConnectingDevice;
	/**
	 * 当前绑定的设备
	 */
	protected AsyncScanResultDeviceInfo mCurrentBindDevice;
	
	/**
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
        	Log.d(TAG, "resultCode : " + resultCode + "," + initialDeviceState);
        	switch(resultCode) {
            	case SUCCESS: {
                    antPcc = result;
                    mCurrentBindDevice = mCurrentConnectingDevice;
                    subscribeToDataEvents();
                    if(connectListener != null) {
                    	connectListener.onConnectSuccess(mCurrentConnectingDevice);
                    }
                    break;
            	}
            	
                default: {
                	if(connectListener != null) {
                    	connectListener.onConnectFaild(mCurrentConnectingDevice);
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
		this.mApp = (App)ctx.getApplicationContext();
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
	 * 找到新设备时的处理
	 */
	protected void onDeviceFound(AsyncScanResultDeviceInfo deviceFound) {
		for(AsyncScanResultDeviceInfo i: mScannedDeviceInfos) {
            if(i.getAntDeviceNumber() == deviceFound.getAntDeviceNumber()) {
            	Log.d(TAG, "dev exist");
                return;
            }
        }
        mScannedDeviceInfos.add(deviceFound);
        if(connectListener != null) {
        	connectListener.onDeviceFound(deviceFound);
        }
	}
	
	/**
	 * 连接设备
	 * @param device
	 */
    public void requestConnectToResult(AsyncScanResultDeviceInfo device) {
    	Log.d(TAG, "Connecting to " + device.getDeviceDisplayName());
    	
    	if(antScanCtrl != null) {
	    	mCurrentConnectingDevice = device;
	        antReleaseHandle = antScanCtrl.requestDeviceAccess(
	        	device,
	    		base_IPluginAccessResultReceiver, 
	            base_IDeviceStateChangeReceiver
	        );
    	}
    }
    
    /**
     * 断开连接
     */
    public void disconnect() {
    	Log.d(TAG, this.getClass().getSimpleName() + "disconnect : " + antReleaseHandle);
    	
		mCurrentBindDevice = null;
		
    	if(antReleaseHandle != null) {
            antReleaseHandle.close();
            antReleaseHandle = null;
        }
    }
    
    /**
     * 关闭连接
     */
	public void close() {
		disconnect();
		
    	if(antScanCtrl != null) {
    		antScanCtrl.closeScanController();
    		antScanCtrl = null;
    	}
	}
	
	/**
	 * 设置连接监听函数
	 * @param listener
	 */
	public void setAntDeviceControlListener(AntDeviceControlListener listener) {
		connectListener = listener;
	}
	
	/**
	 * 获得当前绑定的设备
	 * @return
	 */
	public AsyncScanResultDeviceInfo getCurrentBindDevice() {
		return mCurrentBindDevice;
	}
}
