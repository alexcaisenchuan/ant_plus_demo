package com.alex.antdemo.activities;

import java.math.BigDecimal;

import com.alex.antdemo.R;
import com.alex.antdemo.broadcast.AntValueBroadcast;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

public class DisplayActivity extends Activity {

	private static final String TAG = DisplayActivity.class.getSimpleName();
	
	private MyBroadcastReceiver mReceiver;
	private IntentFilter mIntentFilter;
	
	private TextView mTextHR;
	private TextView mTextCadence;
	private TextView mTextSpeed;
	private TextView mTextDistance;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display);
		
		mTextHR = (TextView)findViewById(R.id.text_hr);
		mTextCadence = (TextView)findViewById(R.id.text_cadence);
		mTextSpeed = (TextView)findViewById(R.id.text_speed);
		mTextDistance = (TextView)findViewById(R.id.text_distance);
		
		mReceiver = new MyBroadcastReceiver();
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(AntValueBroadcast.INTENT_ANT_VALUE_HR);
		mIntentFilter.addAction(AntValueBroadcast.INTENT_ANT_VALUE_CADENCE);
		mIntentFilter.addAction(AntValueBroadcast.INTENT_ANT_VALUE_SPEED);
		mIntentFilter.addAction(AntValueBroadcast.INTENT_ANT_VALUE_DISTANCE);
		mIntentFilter.addAction(AntValueBroadcast.INTENT_ANT_VALUE_POWER);
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		registerReceiver(mReceiver, mIntentFilter);
	}
	
	@Override
	protected void onPause() {
		unregisterReceiver(mReceiver);
		
		super.onPause();
	}

	/**
	 * 广播接收器
	 */
	private class MyBroadcastReceiver extends BroadcastReceiver {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.d(TAG, "action : " + action);
			if(!TextUtils.isEmpty(action)) {
				BigDecimal dec = (BigDecimal)intent.getSerializableExtra(AntValueBroadcast.INTENT_TAG_VALUE);
				float value = dec.floatValue();
				
				if(action.equals(AntValueBroadcast.INTENT_ANT_VALUE_HR)) {
					mTextHR.setText(String.format("心率 \n %.0f bpm", value));
				} else if(action.equals(AntValueBroadcast.INTENT_ANT_VALUE_CADENCE)) {
					mTextCadence.setText(String.format("转速 \n %.0f rpm", value));
				} else if(action.equals(AntValueBroadcast.INTENT_ANT_VALUE_SPEED)) {
					mTextSpeed.setText(String.format("速度 \n %.1f m/s", value));
				} else if(action.equals(AntValueBroadcast.INTENT_ANT_VALUE_DISTANCE)) {
					mTextDistance.setText(String.format("里程 \n %.1f m", value));
				} else if(action.equals(AntValueBroadcast.INTENT_ANT_VALUE_POWER)) {
					//...
				} else {
					//...
				}
			}
		}
	}
}
