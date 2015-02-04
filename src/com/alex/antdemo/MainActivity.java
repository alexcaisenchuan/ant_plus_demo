package com.alex.antdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button btn = (Button)findViewById(R.id.btn_display);
        btn.setOnClickListener(this);
        
        btn = (Button)findViewById(R.id.btn_hr);
        btn.setOnClickListener(this);
        
        btn = (Button)findViewById(R.id.btn_power);
        btn.setOnClickListener(this);
        
        btn = (Button)findViewById(R.id.btn_speedcad);
        btn.setOnClickListener(this);
        
        btn = (Button)findViewById(R.id.btn_cadence);
        btn.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch(arg0.getId()) {
			case R.id.btn_display:
				startActivity(new Intent(this, DisplayActivity.class));
				break;
				
			case R.id.btn_hr:
				startActivity(new Intent(this, HeartRateActivity.class));
				break;
				
			case R.id.btn_power:
				startActivity(new Intent(this, BikePowerActivity.class));
				break;
				
			case R.id.btn_speedcad:
				startActivity(new Intent(this, BikeSpeedDistanceActivity.class));
				break;
				
			case R.id.btn_cadence:
				startActivity(new Intent(this, BikeCadenceActivity.class));
				break;
		}
	}
	
}
