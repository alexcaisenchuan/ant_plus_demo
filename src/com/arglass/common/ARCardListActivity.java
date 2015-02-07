package com.arglass.common;

import java.util.ArrayList;
import java.util.List;

import com.alex.antdemo.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * 基础的卡片流Activity
 * @author caisenchuan
 */
public class ARCardListActivity extends Activity {

	private ViewPager mPager;//页卡内容
	private List<ARCardView> listViews; // Tab页面列表
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.arglass_activity_base);
		
		initViewPager();
	}
	
	/**
	 * 初始化ViewPager
	 */
	private void initViewPager() {
		mPager = (ViewPager) findViewById(R.id.vPager);
		
		ARCardView v1 = new ARCardView(this);
		v1.textTop.setText("111");
		ARCardView v2 = new ARCardView(this);
		v2.textTop.setText("222");
		ARCardView v3 = new ARCardView(this);
		v3.textTop.setText("333");
		
		listViews = new ArrayList<ARCardView>();
		listViews.add(v1);
		listViews.add(v2);
		listViews.add(v3);
		
		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setCurrentItem(0);
	}
	
	/**
	 * ViewPager适配器
	 */
	public class MyPagerAdapter extends PagerAdapter {
		public List<ARCardView> mListViews;

		public MyPagerAdapter(List<ARCardView> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
			//...
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			return mListViews.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			//...
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
			//...
		}
	}
}
