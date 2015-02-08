package com.alex.antdemo.antctrl;

import java.math.BigDecimal;
import java.util.EnumSet;

import android.content.Context;
import android.util.Log;

import com.alex.antdemo.broadcast.AntValueBroadcast;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeCadencePcc;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeCadencePcc.ICalculatedCadenceReceiver;
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult;
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusBikeSpdCadCommonPcc.BikeSpdCadAsyncScanResultDeviceInfo;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusBikeSpdCadCommonPcc.IBikeSpdCadAsyncScanResultReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AsyncScanController.AsyncScanResultDeviceInfo;

public class AntBikeCadence extends AntBase<AntPlusBikeCadencePcc> {

	protected static final String TAG = AntBikeCadence.class.getSimpleName();
	
	AntPlusBikeSpeedDistancePcc bsPcc = null;
	PccReleaseHandle<AntPlusBikeSpeedDistancePcc> bsReleaseHandle = null;
	
	public AntBikeCadence(Context context) {
		super(context);
	}
	
    @Override
    public void startScan() {
        antScanCtrl = AntPlusBikeCadencePcc.requestAsyncScanController(context, 0, new IBikeSpdCadAsyncScanResultReceiver() {
        	@Override
			public void onSearchStopped(RequestAccessResult arg0) {
				Log.d(TAG, "onSearchStopped");
			}
			
			@Override
			public void onSearchResult(BikeSpdCadAsyncScanResultDeviceInfo info) {
				final AsyncScanResultDeviceInfo deviceFound = info.resultInfo;
				Log.d(TAG, "onSearchResult : " + deviceFound.getDeviceDisplayName() + ", combo : " + info.isSpdAndCadComboSensor);
				onDeviceFound(deviceFound);
			}
		});
    }
    
    /**
     * Combo设备中的连接
     * @param device
     */
    public void comboConnect(AsyncScanResultDeviceInfo device) {
    	mCurrentConnectingDevice = device;
    	antReleaseHandle = AntPlusBikeCadencePcc.requestAccess(
			this.context, 
			device.getAntDeviceNumber(), 
			0, 
			true,
			base_IPluginAccessResultReceiver, 
            base_IDeviceStateChangeReceiver
    	);
    }
    
    @Override
    public void subscribeToDataEvents() {
    	Log.d(TAG, "subscribeToDataEvents cadence");
    	
    	antPcc.subscribeCalculatedCadenceEvent(new ICalculatedCadenceReceiver() {
            @Override
            public void onNewCalculatedCadence(final long estTimestamp,
                final EnumSet<EventFlag> eventFlags, final BigDecimal calculatedCadence)
            {
            	Log.d(TAG, "calculatedCadence : " + String.valueOf(calculatedCadence));
            	AntValueBroadcast.sendCadence(context, calculatedCadence);
            }
        });
    	
    	if (antPcc.isSpeedAndCadenceCombinedSensor()) {
    		mApp.getAntBikeSpeedDistance().comboConnect(mCurrentBindDevice);
        }
    }

}
