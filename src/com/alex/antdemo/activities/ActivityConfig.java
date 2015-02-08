package com.alex.antdemo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.alex.antdemo.App;
import com.alex.antdemo.antctrl.AntBase;
import com.alex.antdemo.antctrl.AntBikeCadence;
import com.alex.antdemo.antctrl.AntBikePower;
import com.alex.antdemo.antctrl.AntBikeSpeedDistance;
import com.alex.antdemo.antctrl.AntHeartRate;
import com.alex.antdemo.utils.ThreadUtils;
import com.arglass.common.ARCardHUD;
import com.arglass.common.ARCardListActivity;
import com.arglass.common.ARCardView;

public class ActivityConfig extends ARCardListActivity implements OnClickListener {
	
	App mApp;
	
	AntHeartRate mAntHR;
	AntBikeSpeedDistance mAntSpeed;
	AntBikeCadence mAntCadence;
	AntBikePower mAntPower;
	
	ARCardView card_hr;
	ARCardView card_speed;
	ARCardView card_cadence;
	ARCardView card_power;
	
	private static final int ID_HR 		= 1; 
	private static final int ID_SPEED 	= 2;
	private static final int ID_CADENCE = 3;
	private static final int ID_POWER 	= 4;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mApp = (App)getApplication();
        
        mAntHR = mApp.getAntHR();
        mAntSpeed = mApp.getAntBikeSpeedDistance();
        mAntCadence = mApp.getAntBikeCadence();
        mAntPower = mApp.getAntBikePower();
        
        card_hr = new ARCardView(this);
        card_hr.setId(ID_HR);
        card_hr.textTop.setText("心率计");
        card_hr.setOnClickListener(this);
        mViewList.add(card_hr);
        
        card_speed = new ARCardView(this);
        card_speed.setId(ID_SPEED);
        card_speed.textTop.setText("速度计");
        card_speed.setOnClickListener(this);
        mViewList.add(card_speed);

        card_cadence = new ARCardView(this);
        card_cadence.setId(ID_CADENCE);
        card_cadence.textTop.setText("踏频计");
        card_cadence.setOnClickListener(this);
        mViewList.add(card_cadence);

        card_power = new ARCardView(this);
        card_power.setId(ID_POWER);
        card_power.textTop.setText("功率计");
        card_power.setOnClickListener(this);
        mViewList.add(card_power);
        
        mPagerAdapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		refreshViews();
	}
	
	/**
	 * 刷新显示
	 */
	private void refreshViews() {
		if(mAntHR.getCurrentBindDevice() == null) {
			card_hr.textCenter.setText("未绑定");
        	card_hr.textBottom.setText("点击搜索");
		} else {
			card_hr.textCenter.setText(mAntHR.getCurrentBindDevice().getDeviceDisplayName());
			card_hr.textBottom.setText("点击解除绑定");
		}
        
		if(mAntSpeed.getCurrentBindDevice() == null) {
			card_speed.textCenter.setText("未绑定");
        	card_speed.textBottom.setText("点击搜索");
		} else {
			card_speed.textCenter.setText(mAntSpeed.getCurrentBindDevice().getDeviceDisplayName());
			card_speed.textBottom.setText("点击解除绑定");			
		}
        
		if(mAntCadence.getCurrentBindDevice() == null) {
			card_cadence.textCenter.setText("未绑定");
        	card_cadence.textBottom.setText("点击搜索");
		} else {
			card_cadence.textCenter.setText(mAntCadence.getCurrentBindDevice().getDeviceDisplayName());
			card_cadence.textBottom.setText("点击解除绑定");
		}
        
		if(mAntPower.getCurrentBindDevice() == null) {
			card_power.textCenter.setText("未绑定");
        	card_power.textBottom.setText("点击搜索");
		} else {
			card_power.textCenter.setText(mAntPower.getCurrentBindDevice().getDeviceDisplayName());
			card_power.textBottom.setText("点击解除绑定");
		}
	}
	
	/**
	 * 展示解除绑定的弹出提示
	 * @param ant
	 */
	private void showDisconnect(final AntBase ant) {
		final ARCardHUD hud = new ARCardHUD(this);
		hud.setCenterText("解除绑定?");
		hud.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ant.close();
				hud.setCenterText("解绑成功!");
				
				ThreadUtils.postOnUiThreadDelayed(new Runnable() {
					@Override
					public void run() {
						hud.dismiss();
						refreshViews();		//刷新界面状态
					}
				}, 800);
			}
		});
		hud.show();
	}
	
	@Override
	public void onClick(View arg0) {
		switch(arg0.getId()) {
			case ID_HR: {
				if(mAntHR.getCurrentBindDevice() == null) {
					Intent it = new Intent(this, ActivityAntDeviceBind.class);
					it.putExtra(ActivityAntDeviceBind.INTENT_TYPE, ActivityAntDeviceBind.TYPE_HR);
					startActivity(it);
				} else {
					showDisconnect(mAntHR);
				}
				break;
			}
				
			case ID_SPEED: {
				if(mAntSpeed.getCurrentBindDevice() == null) {
					Intent it = new Intent(this, ActivityAntDeviceBind.class);
					it.putExtra(ActivityAntDeviceBind.INTENT_TYPE, ActivityAntDeviceBind.TYPE_SPEED);
					startActivity(it);
				} else {
					showDisconnect(mAntSpeed);
				}
				break;
			}
				
			case ID_CADENCE: {
				if(mAntCadence.getCurrentBindDevice() == null) {
					Intent it = new Intent(this, ActivityAntDeviceBind.class);
					it.putExtra(ActivityAntDeviceBind.INTENT_TYPE, ActivityAntDeviceBind.TYPE_CAD);
					startActivity(it);
				} else {
					showDisconnect(mAntCadence);
				}
				break;
			}
			
			case ID_POWER: {
				if(mAntPower.getCurrentBindDevice() == null) {
					Intent it = new Intent(this, ActivityAntDeviceBind.class);
					it.putExtra(ActivityAntDeviceBind.INTENT_TYPE, ActivityAntDeviceBind.TYPE_POWER);
					startActivity(it);
				} else {
					showDisconnect(mAntPower);
				}
				break;
			}
		}
	}
}
