package com.alex.antdemo.activities;

import com.alex.antdemo.App;
import com.arglass.common.ARCardListActivity;
import com.arglass.common.ARCardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class ActivityMain extends ARCardListActivity implements OnClickListener {
	
	App mApp;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mApp = (App)getApplication();
        
        ARCardView c1 = new ARCardView(this);
        c1.setId(1);
        c1.textCenter.setText("仪表盘");
        c1.setOnClickListener(this);
        mCardList.add(c1);
        
        ARCardView c2 = new ARCardView(this);
        c2.setId(2);
        c2.textCenter.setText("配置");
        c2.setOnClickListener(this);
        mCardList.add(c2);
        
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
				startActivity(new Intent(this, ActivityDisplay.class));
				break;
			}
				
			case 2: {
				startActivity(new Intent(this, ActivityConfig.class));
				break;
			}
		}
	}
	
}
