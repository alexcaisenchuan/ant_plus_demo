package com.alex.antdemo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.EnumSet;

import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc.CalculatedAccumulatedDistanceReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc.CalculatedSpeedReceiver;
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceState;
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusBikeSpdCadCommonPcc.IBikeSpdCadAsyncScanResultReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AsyncScanController;
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IDeviceStateChangeReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IPluginAccessResultReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusBikeSpdCadCommonPcc.BikeSpdCadAsyncScanResultDeviceInfo;
import com.dsi.ant.plugins.antplus.pccbase.AsyncScanController.AsyncScanResultDeviceInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class BikeSpeedDistanceActivity extends Activity {

	protected static final String TAG = BikeSpeedDistanceActivity.class.getSimpleName();
	
	TextView mTextStatus;
	TextView mTextValue;
	ListView mListDevices;
	ArrayAdapter<String> mAdapterDeviveList;
	
	//Ant
	AntPlusBikeSpeedDistancePcc speedPcc = null;
	AsyncScanController<AntPlusBikeSpeedDistancePcc> hrScanCtrl;
	ArrayList<AsyncScanController.AsyncScanResultDeviceInfo> mScannedDeviceInfos;
	PccReleaseHandle<AntPlusBikeSpeedDistancePcc> releaseHandle = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ant_dev);
        
        //初始化数据
        mScannedDeviceInfos = new ArrayList<AsyncScanController.AsyncScanResultDeviceInfo>();
        
        //绑定视图元素
        mTextStatus = (TextView)findViewById(R.id.text_status);
        mTextValue = (TextView)findViewById(R.id.text_value);
        mListDevices = (ListView)findViewById(R.id.list_devices);
        mAdapterDeviveList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        mListDevices.setAdapter(mAdapterDeviveList);
        mListDevices.setOnItemClickListener(new OnItemClickListener()
            {
                //Return the id of the selected already connected device
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int pos, long id)
                {
                	requestConnectToResult(mScannedDeviceInfos.get(pos));
                }
            }
        );
        
        //启动扫描
        startScan();
    }

    @Override
    protected void onDestroy()
    {
    	if(hrScanCtrl != null)
    	{
    		hrScanCtrl.closeScanController();
    	}
        if(releaseHandle != null)
        {
            releaseHandle.close();
        }
        super.onDestroy();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    //启动扫描
    protected void startScan()
    {
        hrScanCtrl = AntPlusBikeSpeedDistancePcc.requestAsyncScanController(this, 0, new IBikeSpdCadAsyncScanResultReceiver() {
			
			@Override
			public void onSearchStopped(RequestAccessResult arg0) {
				Log.d(TAG, "onSearchStopped");
			}
			
			@Override
			public void onSearchResult(BikeSpdCadAsyncScanResultDeviceInfo info) {
				final AsyncScanController.AsyncScanResultDeviceInfo deviceFound = info.resultInfo;
				
				Log.d(TAG, "onSearchResult : " + deviceFound.getDeviceDisplayName() + ", combo : " + info.isSpdAndCadComboSensor);
                for(AsyncScanResultDeviceInfo i: mScannedDeviceInfos) {
                    if(i.getAntDeviceNumber() == deviceFound.getAntDeviceNumber()) {
                        return;
                    }
                }

                mScannedDeviceInfos.add(deviceFound);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    	mAdapterDeviveList.add(deviceFound.getDeviceDisplayName());
                    	mAdapterDeviveList.notifyDataSetChanged();
                    }
                });
			}
		});
    }
    
    protected void requestConnectToResult(final AsyncScanResultDeviceInfo asyncScanResultDeviceInfo)
    {
        //Inform the user we are connecting
        runOnUiThread(new Runnable()
            {
                public void run()
                {
                    mTextStatus.setText("Connecting to " + asyncScanResultDeviceInfo.getDeviceDisplayName());
                    releaseHandle = hrScanCtrl.requestDeviceAccess(asyncScanResultDeviceInfo,
                        new IPluginAccessResultReceiver<AntPlusBikeSpeedDistancePcc>()
                        {
                            @Override
                            public void onResultReceived(AntPlusBikeSpeedDistancePcc result,
                                RequestAccessResult resultCode, DeviceState initialDeviceState)
                            {
                                if(resultCode == RequestAccessResult.SEARCH_TIMEOUT)
                                {
                                    //On a connection timeout the scan automatically resumes, so we inform the user, and go back to scanning
                                    runOnUiThread(new Runnable()
                                    {
                                        public void run()
                                        {
                                            Toast.makeText(BikeSpeedDistanceActivity.this, "Timed out attempting to connect, try again", Toast.LENGTH_LONG).show();
                                            mTextStatus.setText("Scanning for heart rate devices asynchronously...");
                                        }
                                    });
                                }
                                else
                                {
                                    //Otherwise the results, including SUCCESS, behave the same as
                                    base_IPluginAccessResultReceiver.onResultReceived(result, resultCode, initialDeviceState);
                                    hrScanCtrl = null;
                                }
                            }
                        }, 
                        base_IDeviceStateChangeReceiver
                    );
                }
            }
        );
    }
    
    protected IPluginAccessResultReceiver<AntPlusBikeSpeedDistancePcc> base_IPluginAccessResultReceiver =
        new IPluginAccessResultReceiver<AntPlusBikeSpeedDistancePcc>()
        {
	        //Handle the result, connecting to events on success or reporting failure to user.
	        @Override
	        public void onResultReceived(AntPlusBikeSpeedDistancePcc result, RequestAccessResult resultCode,
	            DeviceState initialDeviceState)
	        {
	            switch(resultCode)
	            {
	                case SUCCESS:
	                    speedPcc = result;
	                    mTextStatus.setText(result.getDeviceName() + ": " + initialDeviceState);
	                    subscribeToHrEvents();
	                    break;
	                case CHANNEL_NOT_AVAILABLE:
	                    Toast.makeText(BikeSpeedDistanceActivity.this, "Channel Not Available", Toast.LENGTH_SHORT).show();
	                    mTextStatus.setText("Error. Do Menu->Reset.");
	                    break;
	                case ADAPTER_NOT_DETECTED:
	                    Toast.makeText(BikeSpeedDistanceActivity.this, "ANT Adapter Not Available. Built-in ANT hardware or external adapter required.", Toast.LENGTH_SHORT).show();
	                    mTextStatus.setText("Error. Do Menu->Reset.");
	                    break;
	                case BAD_PARAMS:
	                    //Note: Since we compose all the params ourself, we should never see this result
	                    Toast.makeText(BikeSpeedDistanceActivity.this, "Bad request parameters.", Toast.LENGTH_SHORT).show();
	                    mTextStatus.setText("Error. Do Menu->Reset.");
	                    break;
	                case OTHER_FAILURE:
	                    Toast.makeText(BikeSpeedDistanceActivity.this, "RequestAccess failed. See logcat for details.", Toast.LENGTH_SHORT).show();
	                    mTextStatus.setText("Error. Do Menu->Reset.");
	                    break;
	                case DEPENDENCY_NOT_INSTALLED:
	                	mTextStatus.setText("Error. Do Menu->Reset.");
	                    AlertDialog.Builder adlgBldr = new AlertDialog.Builder(BikeSpeedDistanceActivity.this);
	                    adlgBldr.setTitle("Missing Dependency");
	                    adlgBldr.setMessage("The required service\n\"" + AntPlusBikeSpeedDistancePcc.getMissingDependencyName() + "\"\n was not found. You need to install the ANT+ Plugins service or you may need to update your existing version if you already have it. Do you want to launch the Play Store to get it?");
	                    adlgBldr.setCancelable(true);
	                    adlgBldr.setPositiveButton("Go to Store", new OnClickListener()
	                    {
	                        @Override
	                        public void onClick(DialogInterface dialog, int which)
	                        {
	                            Intent startStore = null;
	                            startStore = new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=" + AntPlusBikeSpeedDistancePcc.getMissingDependencyPackageName()));
	                            startStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	
	                            BikeSpeedDistanceActivity.this.startActivity(startStore);
	                        }
	                    });
	                    adlgBldr.setNegativeButton("Cancel", new OnClickListener()
	                    {
	                        @Override
	                        public void onClick(DialogInterface dialog, int which)
	                        {
	                            dialog.dismiss();
	                        }
	                    });
	
	                    final AlertDialog waitDialog = adlgBldr.create();
	                    waitDialog.show();
	                    break;
	                case USER_CANCELLED:
	                	mTextStatus.setText("Cancelled. Do Menu->Reset.");
	                    break;
	                case UNRECOGNIZED:
	                    Toast.makeText(BikeSpeedDistanceActivity.this,
	                        "Failed: UNRECOGNIZED. PluginLib Upgrade Required?",
	                        Toast.LENGTH_SHORT).show();
	                    mTextStatus.setText("Error. Do Menu->Reset.");
	                    break;
	                default:
	                    Toast.makeText(BikeSpeedDistanceActivity.this, "Unrecognized result: " + resultCode, Toast.LENGTH_SHORT).show();
	                    mTextStatus.setText("Error. Do Menu->Reset.");
	                    break;
	            }
	        }
        };

        //Receives state changes and shows it on the status display line
        protected  IDeviceStateChangeReceiver base_IDeviceStateChangeReceiver =
            new IDeviceStateChangeReceiver() {
            @Override
            public void onDeviceStateChange(final DeviceState newDeviceState) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTextStatus.setText(speedPcc.getDeviceName() + ": " + newDeviceState);
                    }
                });
            }
        };
        
        public void subscribeToHrEvents()
        {
        	// 2.095m circumference = an average 700cx23mm road tire
            speedPcc.subscribeCalculatedSpeedEvent(new CalculatedSpeedReceiver(new BigDecimal(2.095))
            {
                @Override
                public void onNewCalculatedSpeed(final long estTimestamp,
                    final EnumSet<EventFlag> eventFlags, final BigDecimal calculatedSpeed)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            mTextValue.setText(String.valueOf(calculatedSpeed));
                        }
                    });
                }
            });

            speedPcc.subscribeCalculatedAccumulatedDistanceEvent(new CalculatedAccumulatedDistanceReceiver(
                    new BigDecimal(2.095)) // 2.095m circumference = an average
                                           // 700cx23mm road tire
            {

                @Override
                public void onNewCalculatedAccumulatedDistance(final long estTimestamp,
                    final EnumSet<EventFlag> eventFlags,
                    final BigDecimal calculatedAccumulatedDistance)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            mTextValue.setText(String.valueOf(calculatedAccumulatedDistance.setScale(3, RoundingMode.HALF_UP)));
                        }
                    });
                }
            });
        }
}
