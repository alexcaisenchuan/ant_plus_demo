package com.alex.antdemo.activities;

import com.alex.antdemo.R;

import android.app.Activity;
import android.os.Bundle;

public class ActivityDisplay extends Activity {

	private static final String TAG = ActivityDisplay.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
}
