package com.arglass.common;

import com.alex.antdemo.R;

import android.app.ActionBar.LayoutParams;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
		
		//这句代码不用我说也会知道是干嘛用的，是吧
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //这句代码换掉dialog默认背景，否则dialog的边缘发虚透明而且很宽, 总之达不到想要的效果
        getWindow().setBackgroundDrawableResource(android.R.color.black);
        
		setContentView(R.layout.arglass_hud);
		
		//这句话起全屏的作用
        getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        
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
