package com.alex.antdemo.antctrl;

import java.math.BigDecimal;
import java.util.EnumSet;

import android.content.Context;
import android.util.Log;

import com.alex.antdemo.broadcast.AntValueBroadcast;
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc;
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc.DataState;
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc.IHeartRateDataReceiver;
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult;
import com.dsi.ant.plugins.antplus.pccbase.AsyncScanController.AsyncScanResultDeviceInfo;
import com.dsi.ant.plugins.antplus.pccbase.AsyncScanController.IAsyncScanResultReceiver;

public class AntHeartRate extends AntBase<AntPlusHeartRatePcc> {

	protected static final String TAG = AntHeartRate.class.getSimpleName();

	public AntHeartRate(Context ctx) {
		super(ctx);
	}
	
	@Override
	public void startScan() {
		antScanCtrl = AntPlusHeartRatePcc.requestAsyncScanController(context, 0,
			new IAsyncScanResultReceiver() {
				@Override
				public void onSearchStopped(RequestAccessResult reasonStopped) {
					Log.d(TAG, "onSearchStopped");
				}

				@Override
				public void onSearchResult(final AsyncScanResultDeviceInfo deviceFound) {
					Log.d(TAG,"onSearchResult : " + deviceFound.getDeviceDisplayName());
					onDeviceFound(deviceFound);
				}
			}
		);
	}
	
	@Override
	protected void subscribeToDataEvents() {
		antPcc.subscribeHeartRateDataEvent(new IHeartRateDataReceiver() {
			@Override
			public void onNewHeartRateData(final long estTimestamp,
					EnumSet<EventFlag> eventFlags, final int computedHeartRate,
					final long heartBeatCount,
					final BigDecimal heartBeatEventTime,
					final DataState dataState) {
				
				final String textHeartRate = String.valueOf(computedHeartRate)
						+ ((DataState.ZERO_DETECTED.equals(dataState)) ? "*" : "");
				
				BigDecimal dec = new BigDecimal(computedHeartRate);
				
				Log.d(TAG, "hr : " + textHeartRate);
				
				AntValueBroadcast.sendHR(context, dec);
			}
		});
	}

}
