package com.alex.antdemo;

import com.alex.antdemo.antctrl.AntBikeCadence;
import com.alex.antdemo.antctrl.AntBikePower;
import com.alex.antdemo.antctrl.AntBikeSpeedDistance;
import com.alex.antdemo.antctrl.AntHeartRate;

import android.app.Application;

public class App extends Application {

	private AntHeartRate 			mAntHR;
	private AntBikeCadence 			mAntBikeCadence;
	private AntBikePower 			mAntBikePower;
	private AntBikeSpeedDistance 	mAntBikeSpeedDistance;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		mAntHR = new AntHeartRate(this);
		mAntHR.startScan();
		
		mAntBikeCadence = new AntBikeCadence(this);
		mAntBikeCadence.startScan();
		
		mAntBikePower = new AntBikePower(this);
		mAntBikePower.startScan();
		
		mAntBikeSpeedDistance = new AntBikeSpeedDistance(this);
		mAntBikeSpeedDistance.startScan();
	}
	
	public AntHeartRate getAntHR() {
		return mAntHR;
	}
	
	public AntBikeCadence getAntBikeCadence() {
		return mAntBikeCadence;
	}

	public AntBikePower getAntBikePower() {
		return mAntBikePower;
	}

	public AntBikeSpeedDistance getAntBikeSpeedDistance() {
		return mAntBikeSpeedDistance;
	}
}
