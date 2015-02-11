package com.alex.antdemo.views;

import java.math.BigDecimal;

import com.alex.antdemo.R;
import com.alex.antdemo.broadcast.AntValueBroadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.TypefaceSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ViewAntParamDisplay extends LinearLayout {

	private static final String TAG = ViewAntParamDisplay.class.getSimpleName();
	
	private MyBroadcastReceiver mReceiver;
	private IntentFilter mIntentFilter;
	
	private TextView mTextTop;
	private TextView mTextBottomLeft;
	private TextView mTextBottomRight;
	
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
		
		mTextTop = (TextView)findViewById(R.id.text_top);
		mTextTop.setText(getHRString(0));
		
		mTextBottomLeft = (TextView)findViewById(R.id.text_bottom_left);
		mTextBottomLeft.setText(getSpeedString(0));
		
		mTextBottomRight = (TextView)findViewById(R.id.text_bottom_right);
		mTextBottomRight.setText(getCadenceString(0));
		
		mReceiver = new MyBroadcastReceiver();
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(AntValueBroadcast.INTENT_ANT_VALUE_HR);
		mIntentFilter.addAction(AntValueBroadcast.INTENT_ANT_VALUE_CADENCE);
		mIntentFilter.addAction(AntValueBroadcast.INTENT_ANT_VALUE_SPEED);
		mIntentFilter.addAction(AntValueBroadcast.INTENT_ANT_VALUE_DISTANCE);
		mIntentFilter.addAction(AntValueBroadcast.INTENT_ANT_VALUE_POWER);
	}

	/**
	 * 获得样式文本
	 */
	private SpannableString getSpanString(String title, String value, String unit) {
		SpannableString ret = new SpannableString(String.format("%s\n\n%s  %s", title, value, unit));
		
		int start = 0;
		ret.setSpan(new AbsoluteSizeSpan(15, true), 0, (start + title.length()), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		start += (title.length() + 2);		//跳过分隔符
		ret.setSpan(new TypefaceSpan("default-bold"), start, start + value.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		ret.setSpan(new AbsoluteSizeSpan(55, true), start, (start + value.length()), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		start += (value.length() + 2);
		ret.setSpan(new AbsoluteSizeSpan(16, true), start, (start + unit.length()), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		return ret;
	}
	
	/**
	 * 获得心率字符串
	 */
	private SpannableString getHRString(float value) {
		if(value > 0.0) {
			return getSpanString("心率", String.format("%.0f", value), "bpm");
		} else {
			return getSpanString("心率", "--", "bpm");
		}
	}
	
	/**
	 * 获得速度字符串
	 */
	private SpannableString getSpeedString(float value) {
		if(value > 0.0) {
			return getSpanString("速度", String.format("%.1f", value), "m/s");
		} else {
			return getSpanString("速度", "--", "m/s");
		}
	}
	
	/**
	 * 获得距离字符串
	 */
	private SpannableString getDistanceString(float value) {
		if(value > 0.0) {
			return getSpanString("里程", String.format("%.1f", value), "m");
		} else {
			return getSpanString("里程", "--", "m");
		}
	}

	/**
	 * 获得踏频字符串
	 */
	private SpannableString getCadenceString(float value) {
		if(value > 0.0) {
			return getSpanString("踏频", String.format("%.0f", value), "rpm");
		} else {
			return getSpanString("踏频", "--", "rpm");
		}
	}
	
	/**
	 * 获得功率字符串
	 */
	private SpannableString getPowerString(float value) {
		if(value > 0.0) {
			return getSpanString("功率", String.format("%.0f", value), "W");
		} else {
			return getSpanString("功率", "--", "W");
		}
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
					mTextTop.setText(getHRString(value));
				} else if(action.equals(AntValueBroadcast.INTENT_ANT_VALUE_CADENCE)) {
					mTextBottomLeft.setText(getCadenceString(value));
				} else if(action.equals(AntValueBroadcast.INTENT_ANT_VALUE_SPEED)) {
					mTextBottomRight.setText(getSpeedString(value));
				} else if(action.equals(AntValueBroadcast.INTENT_ANT_VALUE_DISTANCE)) {
					//...
				} else if(action.equals(AntValueBroadcast.INTENT_ANT_VALUE_POWER)) {
					//...
				} else {
					//...
				}
			}
		}
	}
}
