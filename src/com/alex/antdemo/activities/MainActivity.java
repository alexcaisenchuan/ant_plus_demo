package com.alex.antdemo.activities;

import com.alex.antdemo.App;
import com.alex.antdemo.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {
	
	App mApp;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mApp = (App)getApplication();
        
        Button btn = (Button)findViewById(R.id.btn_display);
        btn.setOnClickListener(this);
        
        btn = (Button)findViewById(R.id.btn_hr);
        btn.setOnClickListener(this);
        
        btn = (Button)findViewById(R.id.btn_power);
        btn.setOnClickListener(this);
        
        btn = (Button)findViewById(R.id.btn_speed);
        btn.setOnClickListener(this);
        
        btn = (Button)findViewById(R.id.btn_cadence);
        btn.setOnClickListener(this);
	}
	
	@Override
	protected void onDestroy() {
		mApp.getAntHR().close();
		mApp.getAntBikeCadence().close();
		mApp.getAntBikeSpeedDistance().close();
		mApp.getAntBikePower().close();
		
		super.onDestroy();
	}

	@Override
	public void onClick(View arg0) {
		switch(arg0.getId()) {
			case R.id.btn_display:
				startActivity(new Intent(this, DisplayActivity.class));
				break;
				
			case R.id.btn_hr: {
				Intent it = new Intent(this, DevListActivity.class);
				it.putExtra(DevListActivity.INTENT_TYPE, DevListActivity.TYPE_HR);
				startActivity(it);
				break;
			}
				
			case R.id.btn_power:{
				Intent it = new Intent(this, DevListActivity.class);
				it.putExtra(DevListActivity.INTENT_TYPE, DevListActivity.TYPE_POWER);
				startActivity(it);
				break;
			}
			
			case R.id.btn_speed:{
				Intent it = new Intent(this, DevListActivity.class);
				it.putExtra(DevListActivity.INTENT_TYPE, DevListActivity.TYPE_SPEED);
				startActivity(it);
				break;
			}
				
			case R.id.btn_cadence:{
				Intent it = new Intent(this, DevListActivity.class);
				it.putExtra(DevListActivity.INTENT_TYPE, DevListActivity.TYPE_CAD);
				startActivity(it);
				break;
			}
		}
	}
	
}
