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
    		bsReleaseHandle = AntPlusBikeSpeedDistancePcc.requestAccess(this.context, antPcc.getAntDeviceNumber(), 0, true,
            new IPluginAccessResultReceiver<AntPlusBikeSpeedDistancePcc>() {
                    @Override
                    public void onResultReceived(
                        AntPlusBikeSpeedDistancePcc result,
                        RequestAccessResult resultCode,
                        DeviceState initialDeviceStateCode)
                    {
                    	Log.d(TAG, "onResultReceived : " + resultCode);
                    	
                        switch (resultCode) {
                            case SUCCESS: {
                                bsPcc = result;
                                bsPcc.subscribeCalculatedSpeedEvent(new CalculatedSpeedReceiver(new BigDecimal(2.095)) 
                                {
	                                    @Override
	                                    public void onNewCalculatedSpeed(
	                                        long estTimestamp,
	                                        EnumSet<EventFlag> eventFlags,
	                                        final BigDecimal calculatedSpeed)
	                                    {
	                                        Log.d(TAG, "calculatedSpeed : " + String.valueOf(calculatedSpeed));
	                                        AntValueBroadcast.sendSpeed(context, calculatedSpeed);
	                                    }
	                                }
                                );
                                bsPcc.subscribeCalculatedAccumulatedDistanceEvent(new CalculatedAccumulatedDistanceReceiver(new BigDecimal(2.095)) 
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
                                break;
                            }
                            
                            default: {
                                break;
                            }
                        }
                    }
                },
                new IDeviceStateChangeReceiver() {
                    @Override
                    public void onDeviceStateChange(final DeviceState newDeviceState) {
                        Log.d(TAG, "onDeviceStateChange : " + newDeviceState);
                        if (newDeviceState == DeviceState.DEAD) {
                            bsPcc = null;
                        }
                    }
                }
            );
        }
    }

}
