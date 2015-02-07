package com.alex.antdemo.antctrl;

import java.math.BigDecimal;
import java.util.EnumSet;

import android.content.Context;
import android.util.Log;

import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc.CalculatedAccumulatedDistanceReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc.CalculatedSpeedReceiver;
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusBikeSpdCadCommonPcc.BikeSpdCadAsyncScanResultDeviceInfo;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusBikeSpdCadCommonPcc.IBikeSpdCadAsyncScanResultReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AsyncScanController.AsyncScanResultDeviceInfo;

public class AntBikeSpeedDistance extends AntBase<AntPlusBikeSpeedDistancePcc> {
	
	protected static final String TAG = AntBikeSpeedDistance.class.getSimpleName();
	
	public AntBikeSpeedDistance(Context ctx) {
		super(ctx);
	}
	
    @Override
    public void startScan() {
    	antScanCtrl = AntPlusBikeSpeedDistancePcc.requestAsyncScanController(context, 0, new IBikeSpdCadAsyncScanResultReceiver() {
			@Override
			public void onSearchStopped(RequestAccessResult arg0) {
				Log.d(TAG, "onSearchStopped");
			}
			
			@Override
			public void onSearchResult(BikeSpdCadAsyncScanResultDeviceInfo info) {
				final AsyncScanResultDeviceInfo deviceFound = info.resultInfo;
				Log.d(TAG, "onSearchResult : " + deviceFound.getDeviceDisplayName() + ", combo : " + info.isSpdAndCadComboSensor);
                for(AsyncScanResultDeviceInfo i: mScannedDeviceInfos) {
                    if(i.getAntDeviceNumber() == deviceFound.getAntDeviceNumber()) {
                        return;
                    }
                }

                mScannedDeviceInfos.add(deviceFound);
			}
		});
    }
    
    @Override
    protected void subscribeToDataEvents() {
    	// 2.095m circumference = an average 700cx23mm road tire
    	antPcc.subscribeCalculatedSpeedEvent(new CalculatedSpeedReceiver(new BigDecimal(2.095))
        {
            @Override
            public void onNewCalculatedSpeed(final long estTimestamp,
                final EnumSet<EventFlag> eventFlags, final BigDecimal calculatedSpeed)
            {
            	Log.d(TAG, "calculatedSpeed : " + calculatedSpeed);
            }
        });

        // 2.095m circumference = an average 700cx23mm road tire
    	antPcc.subscribeCalculatedAccumulatedDistanceEvent(new CalculatedAccumulatedDistanceReceiver(new BigDecimal(2.095)) 
        {
            @Override
            public void onNewCalculatedAccumulatedDistance(final long estTimestamp,
                final EnumSet<EventFlag> eventFlags,
                final BigDecimal calculatedAccumulatedDistance)
            {
            	Log.d(TAG, "calculatedAccumulatedDistance : " + calculatedAccumulatedDistance);
            }
        });
    }

}
