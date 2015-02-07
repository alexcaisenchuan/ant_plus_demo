package com.arglass.common;

import com.alex.antdemo.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 卡片View
 * @author caisenchuan
 */
public class ARCardView extends LinearLayout {

	public TextView textTop;
	public TextView textCenter;
	public TextView textBottom;
	
	public ARCardView(Context context) {
		super(context);
		initViews();
	}
	
	public ARCardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViews();
	}
	
	public ARCardView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initViews();
	}
	
	/**
	 * 初始化视图
	 */
	private void initViews() {
		LayoutInflater.from(getContext()).inflate(R.layout.arglass_view_card, this);
		
		textTop = (TextView)findViewById(R.id.text_top);
		textCenter = (TextView)findViewById(R.id.text_center);
		textBottom = (TextView)findViewById(R.id.text_bottom);
	}
	
}
