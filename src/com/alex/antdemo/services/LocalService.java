package com.alex.antdemo.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class LocalService extends Service {

	private static final String TAG = LocalService.class.getSimpleName();
	
    private IBinder binder = new LocalService.LocalBinder();
    
    //定义内容类继承Binder
    public class LocalBinder extends Binder {
        //返回本地服务
        LocalService getService(){
            return LocalService.this;
        }
    }
    
    @Override
    public IBinder onBind(Intent intent) {
    	Log.i(TAG, "onBind"); 
        return binder;
    }
    
    @Override 
    public void onCreate() { 
        Log.i(TAG, "onCreate");
        super.onCreate(); 
    } 

    @Override 
    public void onStart(Intent intent, int startId) { 
        Log.i(TAG, "onStart"); 
        super.onStart(intent, startId); 
    } 

    @Override 
    public int onStartCommand(Intent intent, int flags, int startId) { 
    	Log.i(TAG, "onStartCommand"); 
    	return START_STICKY;
    }
    
    @Override 
    public void onDestroy() { 
        Log.i(TAG, "onDestroy"); 
        super.onDestroy(); 
    } 
}
