package com.alex.antdemo.activities;

import com.alex.antdemo.App;
import com.alex.antdemo.views.ViewAntParamDisplay;
import com.arglass.common.ARCardListActivity;
import com.arglass.common.ARCardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class ActivityMain extends ARCardListActivity implements OnClickListener {
	
	private static final int ID_CONFIG = 1;
	
	App mApp;
	ViewAntParamDisplay mAntDisplay;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mApp = (App)getApplication();
        
        mAntDisplay = new ViewAntParamDisplay(this);
        mViewList.add(mAntDisplay);
        
        ARCardView c2 = new ARCardView(this);
        c2.setId(ID_CONFIG);
        c2.textCenter.setText("配置");
        c2.setOnClickListener(this);
        mViewList.add(c2);
        
        mPagerAdapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		mAntDisplay.registerBroadcastReceiver();
	}
	
	@Override
	protected void onStop() {
		mAntDisplay.unregisterBroadcastReceiver();
		
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		//退出程序不关闭
		//mApp.getAntHR().close();
		//mApp.getAntBikeCadence().close();
		//mApp.getAntBikeSpeedDistance().close();
		//mApp.getAntBikePower().close();
		
		super.onDestroy();
	}

	@Override
	public void onClick(View arg0) {
		switch(arg0.getId()) {
			case ID_CONFIG: {
				startActivity(new Intent(this, ActivityConfig.class));
				break;
			}
		}
	}
	
}
