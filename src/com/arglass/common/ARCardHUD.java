package com.arglass.common;

import com.alex.antdemo.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

/**
 * 模态提示框
 * @author caisenchuan
 */
public class ARCardHUD extends Dialog {

	private String title;
	
	private TextView titleview;
	
	public ARCardHUD(Context context) {
		super(context);
	}
	
	public ARCardHUD(Context context, int theme) {
		super(context, theme);
	}
	
	protected ARCardHUD(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.arglass_hud);
		
		//设置标题
		titleview = (TextView)findViewById(R.id.text_title);
		titleview.setText(title);
	}
	
	/**
	 * 设置标题
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
		if(titleview != null) {
			titleview.setText(title);
		}
	}
}
