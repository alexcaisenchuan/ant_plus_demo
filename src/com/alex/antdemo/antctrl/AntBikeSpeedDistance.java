package com.alex.antdemo.antctrl;

import java.math.BigDecimal;
import java.util.EnumSet;

import android.content.Context;
import android.util.Log;

import com.alex.antdemo.broadcast.AntValueBroadcast;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeCadencePcc;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeCadencePcc.ICalculatedCadenceReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc.CalculatedAccumulatedDistanceReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc.CalculatedSpeedReceiver;
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceState;
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult;
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IDeviceStateChangeReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IPluginAccessResultReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusBikeSpdCadCommonPcc.BikeSpdCadAsyncScanResultDeviceInfo;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusBikeSpdCadCommonPcc.IBikeSpdCadAsyncScanResultReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AsyncScanController.AsyncScanResultDeviceInfo;

public class AntBikeSpeedDistance extends AntBase<AntPlusBikeSpeedDistancePcc> {
	
	protected static final String TAG = AntBikeSpeedDistance.class.getSimpleName();
    
	AntPlusBikeCadencePcc bcPcc = null;
    PccReleaseHandle<AntPlusBikeCadencePcc> bcReleaseHandle = null;
    
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
				onDeviceFound(deviceFound);
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
            	AntValueBroadcast.sendSpeed(context, calculatedSpeed);
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
            	AntValueBroadcast.sendDistance(context, calculatedAccumulatedDistance);
            }
        });
    	
    	if (antPcc.isSpeedAndCadenceCombinedSensor())
        {
    		bcReleaseHandle = AntPlusBikeCadencePcc.requestAccess(
	            this.context,
	            antPcc.getAntDeviceNumber(), 0, true,
	            new IPluginAccessResultReceiver<AntPlusBikeCadencePcc>()
	            {
	                @Override
	                public void onResultReceived(AntPlusBikeCadencePcc result,
	                    RequestAccessResult resultCode,
	                    DeviceState initialDeviceStateCode)
	                {
	                    switch (resultCode)
	                    {
	                        case SUCCESS:
	                            bcPcc = result;
	                            bcPcc.subscribeCalculatedCadenceEvent(new ICalculatedCadenceReceiver()
	                                {
	                                    @Override
	                                    public void onNewCalculatedCadence(
	                                        long estTimestamp,
	                                        EnumSet<EventFlag> eventFlags,
	                                        final BigDecimal calculatedCadence)
	                                    {
	                                    	Log.d(TAG, "calculatedCadence : " + String.valueOf(calculatedCadence));
	                                    	AntValueBroadcast.sendCadence(context, calculatedCadence);
	                                    }
	                                });
	                            break;
	                        default:
	                            break;
	                    }
	                }
	            },
	            new IDeviceStateChangeReceiver()
	            {
	                @Override
	                public void onDeviceStateChange(final DeviceState newDeviceState)
	                {
	                	Log.d(TAG, "onDeviceStateChange : " + newDeviceState);
	                	if (newDeviceState == DeviceState.DEAD) {
                            bcPcc = null;
	                	}
	                }
	            }
            );
        }
    }

}
