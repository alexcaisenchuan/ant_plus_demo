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

	public static final String TAG = ARCardListActivity.class.getSimpleName();
	
	protected ViewPager mPager;
	protected List<View> mViewList;
	protected MyPagerAdapter mPagerAdapter;
	
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
		mViewList = new ArrayList<View>();
		mPagerAdapter = new MyPagerAdapter(mViewList);
		mPager = (ViewPager) findViewById(R.id.vPager);
		mPager.setAdapter(mPagerAdapter);
		mPager.setCurrentItem(0);
	}
	
	/**
	 * ViewPager适配器
	 */
	public class MyPagerAdapter extends PagerAdapter {
		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View collection, int position, Object view) {
			((ViewPager) collection).removeView((View) view);
		}

		@Override
		public int getCount() {
			int size = mListViews.size();
			return size;
		}

		@Override
		public Object instantiateItem(View collection, int position) {
			View v = mListViews.get(position);
			((ViewPager) collection).addView(v);
			return v;
		}

		@Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
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

		@Override
		public void finishUpdate(View arg0) {
			//...
		}
		
		@Override
		public int getItemPosition(Object object) {
			//为了解决无法刷新View内容的问题
			//http://stackoverflow.com/questions/7263291/viewpager-pageradapter-not-updating-the-view
		    return POSITION_NONE;
		}
	}
}
