package com.alex.antdemo.activities;

import java.util.List;

import com.alex.antdemo.App;
import com.alex.antdemo.R;
import com.alex.antdemo.antctrl.AntBase;
import com.dsi.ant.plugins.antplus.pccbase.AsyncScanController.AsyncScanResultDeviceInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 绑定Ant设备
 * @author caisenchuan
 */
public class ActivityAntDeviceBind extends Activity {

	protected static final String TAG = ActivityAntDeviceBind.class.getSimpleName();
	
	public static final String INTENT_TYPE = "type";
	public static final int TYPE_HR    = 1;
	public static final int TYPE_CAD   = 2;
	public static final int TYPE_POWER = 3;
	public static final int TYPE_SPEED = 4;
	
	App mApp;
	AntBase mAntControl;
	List<AsyncScanResultDeviceInfo> mDevList;
	
	TextView mTextStatus;
	TextView mTextValue;
	ListView mListDevices;
	ArrayAdapter<String> mAdapterDeviveList;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ant_dev);
        
        mApp = (App)getApplication();
        
        Intent it = getIntent();
        int type = it.getIntExtra(INTENT_TYPE, TYPE_HR);
        if(type == TYPE_CAD) {
        	mAntControl = mApp.getAntBikeCadence();
        } else if(type == TYPE_POWER) {
        	mAntControl = mApp.getAntBikePower();
        } else if(type == TYPE_SPEED) {
        	mAntControl = mApp.getAntBikeSpeedDistance();
        } else {
        	mAntControl = mApp.getAntHR();
        }
        
        Log.d(TAG, "type : " + type);
        mDevList = mAntControl.getDevList();
        
        //绑定视图元素
        mTextStatus = (TextView)findViewById(R.id.text_status);
        mTextValue = (TextView)findViewById(R.id.text_value);
        mListDevices = (ListView)findViewById(R.id.list_devices);
        mAdapterDeviveList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        mListDevices.setAdapter(mAdapterDeviveList);
        mListDevices.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                	try {
                		AsyncScanResultDeviceInfo info = mDevList.get(pos);
                		mAntControl.requestConnectToResult(info);
                	} catch (Exception e) {
                		e.printStackTrace();
                	}
                }
            }
        );
        
        refreshList();
    }
    
    /**
     * 刷新列表
     */
    private void refreshList() {
        for(AsyncScanResultDeviceInfo info : mDevList) {
        	mAdapterDeviveList.add(info.getDeviceDisplayName());
        }
        mAdapterDeviveList.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
    	super.onDestroy();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
