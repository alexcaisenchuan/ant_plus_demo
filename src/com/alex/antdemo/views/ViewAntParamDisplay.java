package com.alex.antdemo.views;

import java.math.BigDecimal;

import com.alex.antdemo.R;
import com.alex.antdemo.broadcast.AntValueBroadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ViewAntParamDisplay extends LinearLayout {

	private static final String TAG = ViewAntParamDisplay.class.getSimpleName();
	
	private MyBroadcastReceiver mReceiver;
	private IntentFilter mIntentFilter;
	
	private TextView mTextHR;
	private TextView mTextCadence;
	private TextView mTextSpeed;
	private TextView mTextDistance;
	
	/**
	 * 构造函数
	 * @param context
	 */
	public ViewAntParamDisplay(Context context) {
		super(context);
		initViews();
	}
	
	public ViewAntParamDisplay(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViews();
	}
	
	public ViewAntParamDisplay(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initViews();
	}
	
	/**
	 * 注册监听器
	 */
	public void registerBroadcastReceiver() {
		getContext().registerReceiver(mReceiver, mIntentFilter);
	}
	
	/**
	 * 取消注册监听器
	 */
	public void unregisterBroadcastReceiver() {
		getContext().unregisterReceiver(mReceiver);
	}
	
	/**
	 * 初始化视图
	 */
	private void initViews() {
		LayoutInflater.from(getContext()).inflate(R.layout.activity_display, this);
		
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
