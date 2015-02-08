package com.alex.antdemo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.alex.antdemo.App;
import com.arglass.common.ARCardListActivity;
import com.arglass.common.ARCardView;

public class ActivityConfig extends ARCardListActivity implements OnClickListener {
	App mApp;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mApp = (App)getApplication();
        
        ARCardView c1 = new ARCardView(this);
        c1.setId(1);
        c1.textCenter.setText("心率计");
        c1.textBottom.setText("点击绑定");
        c1.setOnClickListener(this);
        mViewList.add(c1);
        
        ARCardView c2 = new ARCardView(this);
        c2.setId(2);
        c2.textCenter.setText("速度计");
        c2.textBottom.setText("点击绑定");
        c2.setOnClickListener(this);
        mViewList.add(c2);

        ARCardView c3 = new ARCardView(this);
        c3.setId(3);
        c3.textCenter.setText("踏频计");
        c3.textBottom.setText("点击绑定");
        c3.setOnClickListener(this);
        mViewList.add(c3);

        ARCardView c4 = new ARCardView(this);
        c4.setId(4);
        c4.textCenter.setText("功率计");
        c4.textBottom.setText("点击绑定");
        c4.setOnClickListener(this);
        mViewList.add(c4);
        
        mPagerAdapter.notifyDataSetChanged();
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
			case 1: {
				Intent it = new Intent(this, ActivityAntDeviceBind.class);
				it.putExtra(ActivityAntDeviceBind.INTENT_TYPE, ActivityAntDeviceBind.TYPE_HR);
				startActivity(it);
				break;
			}
				
			case 2:{
				Intent it = new Intent(this, ActivityAntDeviceBind.class);
				it.putExtra(ActivityAntDeviceBind.INTENT_TYPE, ActivityAntDeviceBind.TYPE_SPEED);
				startActivity(it);
				break;
			}
				
			case 3:{
				Intent it = new Intent(this, ActivityAntDeviceBind.class);
				it.putExtra(ActivityAntDeviceBind.INTENT_TYPE, ActivityAntDeviceBind.TYPE_CAD);
				startActivity(it);
				break;
			}
			
			case 4:{
				Intent it = new Intent(this, ActivityAntDeviceBind.class);
				it.putExtra(ActivityAntDeviceBind.INTENT_TYPE, ActivityAntDeviceBind.TYPE_POWER);
				startActivity(it);
				break;
			}
		}
	}
}
