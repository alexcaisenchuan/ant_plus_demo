package com.alex.antdemo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;

import com.dsi.ant.plugins.antplus.pcc.AntPlusBikePowerPcc;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikePowerPcc.DataSource;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikePowerPcc.ICalculatedPowerReceiver;
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceState;
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult;
import com.dsi.ant.plugins.antplus.pccbase.AsyncScanController;
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IDeviceStateChangeReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IPluginAccessResultReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AsyncScanController.AsyncScanResultDeviceInfo;
import com.dsi.ant.plugins.antplus.pccbase.AsyncScanController.IAsyncScanResultReceiver;

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

public class BikePowerActivity extends Activity {

	protected static final String TAG = BikePowerActivity.class.getSimpleName();
	
	TextView mTextStatus;
	TextView mTextValue;
	ListView mListDevices;
	ArrayAdapter<String> mAdapterDeviveList;
	
	//Ant
	AntPlusBikePowerPcc hrPcc = null;
	AsyncScanController<AntPlusBikePowerPcc> hrScanCtrl;
	ArrayList<AsyncScanController.AsyncScanResultDeviceInfo> mScannedDeviceInfos;
	PccReleaseHandle<AntPlusBikePowerPcc> releaseHandle = null;
	
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
        hrScanCtrl = AntPlusBikePowerPcc.requestAsyncScanController(this, 0,
            new IAsyncScanResultReceiver()
        {
            @Override
            public void onSearchStopped(RequestAccessResult reasonStopped)
            {
                //The triggers calling this function use the same codes and require the same actions as those received by the standard access result receiver
                //base_IPluginAccessResultReceiver.onResultReceived(null, reasonStopped, DeviceState.DEAD);
            	Log.d(TAG, "onSearchStopped");
            }

            @Override
            public void onSearchResult(final AsyncScanResultDeviceInfo deviceFound)
            {
            	Log.d(TAG, "onSearchResult : " + deviceFound.getDeviceDisplayName());
            	
                for(AsyncScanResultDeviceInfo i: mScannedDeviceInfos)
                {
                    //The current implementation of the async scan will reset it's ignore list every 30s,
                    //So we have to handle checking for duplicates in our list if we run longer than that
                    if(i.getAntDeviceNumber() == deviceFound.getAntDeviceNumber())
                    {
                        //Found already connected device, ignore
                        return;
                    }
                }

                //We split up devices already connected to the plugin from un-connected devices to make this information more visible to the user,
                //since the user most likely wants to be aware of which device they are already using in another app
                mScannedDeviceInfos.add(deviceFound);
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
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
                        new IPluginAccessResultReceiver<AntPlusBikePowerPcc>()
                        {
                            @Override
                            public void onResultReceived(AntPlusBikePowerPcc result,
                                RequestAccessResult resultCode, DeviceState initialDeviceState)
                            {
                                if(resultCode == RequestAccessResult.SEARCH_TIMEOUT)
                                {
                                    //On a connection timeout the scan automatically resumes, so we inform the user, and go back to scanning
                                    runOnUiThread(new Runnable()
                                    {
                                        public void run()
                                        {
                                            Toast.makeText(BikePowerActivity.this, "Timed out attempting to connect, try again", Toast.LENGTH_LONG).show();
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
    
    protected IPluginAccessResultReceiver<AntPlusBikePowerPcc> base_IPluginAccessResultReceiver =
        new IPluginAccessResultReceiver<AntPlusBikePowerPcc>()
        {
	        //Handle the result, connecting to events on success or reporting failure to user.
	        @Override
	        public void onResultReceived(AntPlusBikePowerPcc result, RequestAccessResult resultCode,
	            DeviceState initialDeviceState)
	        {
	            switch(resultCode)
	            {
	                case SUCCESS:
	                    hrPcc = result;
	                    mTextStatus.setText(result.getDeviceName() + ": " + initialDeviceState);
	                    subscribeToHrEvents();
	                    break;
	                case CHANNEL_NOT_AVAILABLE:
	                    Toast.makeText(BikePowerActivity.this, "Channel Not Available", Toast.LENGTH_SHORT).show();
	                    mTextStatus.setText("Error. Do Menu->Reset.");
	                    break;
	                case ADAPTER_NOT_DETECTED:
	                    Toast.makeText(BikePowerActivity.this, "ANT Adapter Not Available. Built-in ANT hardware or external adapter required.", Toast.LENGTH_SHORT).show();
	                    mTextStatus.setText("Error. Do Menu->Reset.");
	                    break;
	                case BAD_PARAMS:
	                    //Note: Since we compose all the params ourself, we should never see this result
	                    Toast.makeText(BikePowerActivity.this, "Bad request parameters.", Toast.LENGTH_SHORT).show();
	                    mTextStatus.setText("Error. Do Menu->Reset.");
	                    break;
	                case OTHER_FAILURE:
	                    Toast.makeText(BikePowerActivity.this, "RequestAccess failed. See logcat for details.", Toast.LENGTH_SHORT).show();
	                    mTextStatus.setText("Error. Do Menu->Reset.");
	                    break;
	                case DEPENDENCY_NOT_INSTALLED:
	                	mTextStatus.setText("Error. Do Menu->Reset.");
	                    AlertDialog.Builder adlgBldr = new AlertDialog.Builder(BikePowerActivity.this);
	                    adlgBldr.setTitle("Missing Dependency");
	                    adlgBldr.setMessage("The required service\n\"" + AntPlusBikePowerPcc.getMissingDependencyName() + "\"\n was not found. You need to install the ANT+ Plugins service or you may need to update your existing version if you already have it. Do you want to launch the Play Store to get it?");
	                    adlgBldr.setCancelable(true);
	                    adlgBldr.setPositiveButton("Go to Store", new OnClickListener()
	                    {
	                        @Override
	                        public void onClick(DialogInterface dialog, int which)
	                        {
	                            Intent startStore = null;
	                            startStore = new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=" + AntPlusBikePowerPcc.getMissingDependencyPackageName()));
	                            startStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	
	                            BikePowerActivity.this.startActivity(startStore);
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
	                    Toast.makeText(BikePowerActivity.this,
	                        "Failed: UNRECOGNIZED. PluginLib Upgrade Required?",
	                        Toast.LENGTH_SHORT).show();
	                    mTextStatus.setText("Error. Do Menu->Reset.");
	                    break;
	                default:
	                    Toast.makeText(BikePowerActivity.this, "Unrecognized result: " + resultCode, Toast.LENGTH_SHORT).show();
	                    mTextStatus.setText("Error. Do Menu->Reset.");
	                    break;
	            }
	        }
        };

        //Receives state changes and shows it on the status display line
        protected  IDeviceStateChangeReceiver base_IDeviceStateChangeReceiver =
            new IDeviceStateChangeReceiver()
        {
            @Override
            public void onDeviceStateChange(final DeviceState newDeviceState)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mTextStatus.setText(hrPcc.getDeviceName() + ": " + newDeviceState);
                    }
                });
            }
        };
        
        public void subscribeToHrEvents()
        {
        	hrPcc.subscribeCalculatedPowerEvent(new ICalculatedPowerReceiver()
            {
                @Override
                public void onNewCalculatedPower(
                    final long estTimestamp, final EnumSet<EventFlag> eventFlags,
                    final DataSource dataSource,
                    final BigDecimal calculatedPower)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            mTextValue.setText(calculatedPower.toString() + "W");
                            String source = "";

                            // NOTE: The calculated power event will send an
                            // initial value code if it needed
                            // to calculate a NEW average. This is important if
                            // using the calculated power event to record user
                            // data, as an
                            // initial value indicates an average could not be
                            // guaranteed.
                            // The event prioritizes calculating with torque
                            // data over power only data.
                            switch (dataSource)
                            {
                                case POWER_ONLY_DATA:
                                case INITIAL_VALUE_POWER_ONLY_DATA:
                                    // New data calculated from initial
                                    // value data source
                                case WHEEL_TORQUE_DATA:
                                case INITIAL_VALUE_WHEEL_TORQUE_DATA:
                                    // New data calculated from initial
                                    // value data source
                                case CRANK_TORQUE_DATA:
                                case INITIAL_VALUE_CRANK_TORQUE_DATA:
                                    // New data calculated from initial
                                    // value data source
                                case CTF_DATA:
                                case INITIAL_VALUE_CTF_DATA:
                                    source = dataSource.toString();
                                    break;
                                case INVALID_CTF_CAL_REQ:
                                    // The event cannot calculate power
                                    // from CTF until a zero offset is
                                    // collected from the sensor.
                                case COAST_OR_STOP_DETECTED:
                                    //A coast or stop condition detected by the ANT+ Plugin.
                                    //This is automatically sent by the plugin after 3 seconds of unchanging events.
                                    //NOTE: This value should be ignored by apps which are archiving the data for accuracy.
                                    source = dataSource.toString();
                                    break;
                                case UNRECOGNIZED:
                                    Toast.makeText(BikePowerActivity.this,
                                        "Failed: UNRECOGNIZED. PluginLib Upgrade Required?",
                                        Toast.LENGTH_SHORT).show();
                                default:
                                    mTextStatus.setText("N/A");
                                    source = "N/A";
                                    break;
                            }

                            mTextStatus.setText(source);
                        }
                    });
                }
            });
        }
}
