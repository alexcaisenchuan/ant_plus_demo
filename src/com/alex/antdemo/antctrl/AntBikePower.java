package com.alex.antdemo.antctrl;

import java.math.BigDecimal;
import java.util.EnumSet;

import android.content.Context;
import android.util.Log;

import com.alex.antdemo.broadcast.AntValueBroadcast;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikePowerPcc;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikePowerPcc.DataSource;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikePowerPcc.ICalculatedPowerReceiver;
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult;
import com.dsi.ant.plugins.antplus.pccbase.AsyncScanController.AsyncScanResultDeviceInfo;
import com.dsi.ant.plugins.antplus.pccbase.AsyncScanController.IAsyncScanResultReceiver;

public class AntBikePower extends AntBase<AntPlusBikePowerPcc> {

	protected static final String TAG = AntBikePower.class.getSimpleName();
	
	public AntBikePower(Context ctx) {
		super(ctx);
	}
	
    @Override
    public void startScan() {
    	antScanCtrl = AntPlusBikePowerPcc.requestAsyncScanController(context, 0, new IAsyncScanResultReceiver() {
            @Override
            public void onSearchStopped(RequestAccessResult reasonStopped) {
            	Log.d(TAG, "onSearchStopped");
            }

            @Override
            public void onSearchResult(final AsyncScanResultDeviceInfo deviceFound) {
            	Log.d(TAG, "onSearchResult : " + deviceFound.getDeviceDisplayName());
            	onDeviceFound(deviceFound);
            }
        });
    }
    
    @Override
    protected void subscribeToDataEvents() {
    	antPcc.subscribeCalculatedPowerEvent(new ICalculatedPowerReceiver() {
            @Override
            public void onNewCalculatedPower(
                final long estTimestamp, final EnumSet<EventFlag> eventFlags,
                final DataSource dataSource,
                final BigDecimal calculatedPower)
            {
            	Log.d(TAG, calculatedPower.toString() + "W");
            	AntValueBroadcast.sendPower(context, calculatedPower);
            }
        });
    }

}
