package com.arglass.common;

import com.alex.antdemo.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * 模态提示框
 * @author caisenchuan
 */
public class ARCardHUD extends Dialog {

	/**
	 * 整个View
	 */
	View mViewAll;
	/**
	 * 标题文字
	 */
	private String mCenterText;
	/**
	 * 标题View
	 */
	private TextView mTextCenter;
	
	private View.OnClickListener mClickListener;
	
	public ARCardHUD(Context context) {
		super(context, R.style.MyDialog);
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
		
		//整个View
		mViewAll = findViewById(R.id.view_all);
		if(mClickListener != null) {
			mViewAll.setOnClickListener(mClickListener);
		}
		
		//设置标题
		mTextCenter = (TextView)findViewById(R.id.text_center);
		mTextCenter.setText(mCenterText);
	}
	
	/**
	 * 设置标题
	 * @param title
	 */
	public void setCenterText(String title) {
		this.mCenterText = title;
		if(mTextCenter != null) {
			mTextCenter.setText(title);
		}
	}
	
	/**
	 * 绑定整个Hud的点击事件监听
	 * @param l
	 */
	public void setOnClickListener(View.OnClickListener l) {
		mClickListener = l;
		if(mViewAll != null) {
			mViewAll.setOnClickListener(l);
		}
	}
}
