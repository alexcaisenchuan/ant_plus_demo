package com.alex.antdemo.broadcast;

import java.math.BigDecimal;

import android.content.Context;
import android.content.Intent;

public class AntValueBroadcast {
	private static String ANT_VALUE_PREFIX = "com.alex.antdemo.";
	
	public static String INTENT_ANT_VALUE_HR      	= ANT_VALUE_PREFIX + "ant_value_hr";
	public static String INTENT_ANT_VALUE_CADENCE 	= ANT_VALUE_PREFIX + "ant_value_cadence";
	public static String INTENT_ANT_VALUE_SPEED   	= ANT_VALUE_PREFIX + "ant_value_speed";
	public static String INTENT_ANT_VALUE_DISTANCE  = ANT_VALUE_PREFIX + "ant_value_distance";
	public static String INTENT_ANT_VALUE_POWER   	= ANT_VALUE_PREFIX + "ant_value_power";
	
	public static String INTENT_TAG_VALUE 			= "value";
	
	/**
	 * 发出HR广播
	 * @param context
	 * @param hr
	 */
	public static void sendHR(Context context, BigDecimal hr) {
		Intent intent = new Intent(INTENT_ANT_VALUE_HR);
		intent.putExtra(INTENT_TAG_VALUE, hr);
		context.sendBroadcast(intent);
	}
	
	/**
	 * 发出踏频广播
	 * @param context
	 * @param cad
	 */
	public static void sendCadence(Context context, BigDecimal cad) {
		Intent intent = new Intent(INTENT_ANT_VALUE_CADENCE);
		intent.putExtra(INTENT_TAG_VALUE, cad);
		context.sendBroadcast(intent);
	}
	
	/**
	 * 发出速度广播
	 * @param context
	 * @param speed
	 * @param distance
	 */
	public static void sendSpeed(Context context, BigDecimal speed) {
		Intent intent = new Intent(INTENT_ANT_VALUE_SPEED);
		intent.putExtra(INTENT_TAG_VALUE, speed);
		context.sendBroadcast(intent);
	}
	
	/**
	 * 发出距离广播
	 * @param context
	 * @param distance
	 */
	public static void sendDistance(Context context, BigDecimal distance) {
		Intent intent = new Intent(INTENT_ANT_VALUE_DISTANCE);
		intent.putExtra(INTENT_TAG_VALUE, distance);
		context.sendBroadcast(intent);
	}
	
	/**
	 * 发出功率广播
	 * @param context
	 * @param power
	 */
	public static void sendPower(Context context, BigDecimal power) {
		Intent intent = new Intent(INTENT_ANT_VALUE_POWER);
		intent.putExtra(INTENT_TAG_VALUE, power);
		context.sendBroadcast(intent);
	}
}
