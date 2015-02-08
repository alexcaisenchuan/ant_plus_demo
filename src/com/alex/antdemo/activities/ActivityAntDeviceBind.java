package com.alex.antdemo.activities;

import java.util.List;

import com.alex.antdemo.App;
import com.alex.antdemo.R;
import com.alex.antdemo.antctrl.AntBase;
import com.alex.antdemo.antctrl.AntConnectListener;
import com.alex.antdemo.utils.ThreadUtils;
import com.arglass.common.ARCardHUD;
import com.arglass.common.ARCardListActivity;
import com.arglass.common.ARCardView;
import com.dsi.ant.plugins.antplus.pccbase.AsyncScanController.AsyncScanResultDeviceInfo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * 绑定Ant设备
 * @author caisenchuan
 */
public class ActivityAntDeviceBind extends ARCardListActivity implements OnClickListener {

	protected static final String TAG = ActivityAntDeviceBind.class.getSimpleName();
	
	public static final String INTENT_TYPE = "type";
	public static final int TYPE_HR    = 1;
	public static final int TYPE_CAD   = 2;
	public static final int TYPE_POWER = 3;
	public static final int TYPE_SPEED = 4;
	
	App mApp;
	AntBase mAntControl;
	List<AsyncScanResultDeviceInfo> mDevList;
	
	ARCardHUD mHud;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mApp = (App)getApplication();
        
        Intent it = getIntent();
        int type = it.getIntExtra(INTENT_TYPE, TYPE_HR);
        if(type == TYPE_CAD) {
        	mAntControl = mApp.getAntBikeCadence();
        } else if(type == TYPE_POWER) {
        	mAntControl = mApp.getAntBikePower();
        } else if(type == TYPE_SPEED) {
        	mAntControl = mApp.getAntBikeSpeedDistance();
        } else {
        	mAntControl = mApp.getAntHR();
        }
        
        Log.d(TAG, "type : " + type);
        mDevList = mAntControl.getDevList();
        
        //刷新列表
        refreshList();
    }
    
    /**
     * 刷新列表
     */
    private void refreshList() {
    	if(mDevList.size() == 0) {
    		//显示提示
    		ARCardView c = new ARCardView(this);
	        c.textCenter.setText("搜索中...");
	        mViewList.add(c);
    	} else {
    		//显示所有设备
    		int index = 0;
    		for(AsyncScanResultDeviceInfo info : mDevList) {
    			ARCardView c = new ARCardView(this);
    	        c.setId(index);
    	        c.textCenter.setText(info.getDeviceDisplayName());
    	        c.textBottom.setText("点击绑定");
    	        c.setOnClickListener(this);
    	        mViewList.add(c);
    	        
    	        index++;
    		}
    	}
    	
    	mPagerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
    	super.onDestroy();
    }

	@Override
	public void onClick(View v) {
		int index = -1;
		if(v.getId() >= 0) {
			index = v.getId();
			if(index < mDevList.size()) {
				try {
	        		AsyncScanResultDeviceInfo info = mDevList.get(index);
	        		mAntControl.requestConnectToResult(info);
	        		mAntControl.setConnectListener(new AntConnectListener() {
						
						@Override
						public void onConnectSuccess() {
							dissmissHudWithSuccess(true);
						}
						
						@Override
						public void onConnectFaild() {
							dissmissHudWithSuccess(false);
						}
					});
	        		
	        		mHud = new ARCardHUD(this, R.style.MyDialog);
	                mHud.setCancelable(false);
	        		mHud.setTitle("正在连接...");
	        		mHud.show();
	        	} catch (Exception e) {
	        		e.printStackTrace();
	        	}
			}
		}
	}
	
	/**
	 * 显示文字，暂停几秒，关闭Hud
	 * @param text
	 */
	private void dissmissHudWithSuccess(final boolean success) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(success) {
					mHud.setTitle("连接成功!");
				} else {
					mHud.setTitle("连接失败!");
				}
				ThreadUtils.postOnUiThreadDelayed(new Runnable() {
					@Override
					public void run() {
						//关闭提示框
						mHud.dismiss();
						//如果成功，关闭界面
						if(success) {
							finish();
						}
					}
				}, 800);
			}
		});
	}
}
