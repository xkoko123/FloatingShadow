package com.agmcs.FloatingShadow.adapter;

import android.content.Context;
import android.provider.Settings;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.agmcs.FloatingShadow.R;
import com.agmcs.FloatingShadow.Views.VerticalPagerAdapter;

import java.util.List;

/**
 * Created by agmcs on 2015/4/6.
 */
public class MyPagerAdapter extends VerticalPagerAdapter {
    private List<View> mListViews;

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mListViews.get(position));//删除页卡
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mListViews.get(position);
        container.addView(view, 0);//添加页卡
        return mListViews.get(position);
    }

    public MyPagerAdapter(List<View> mListViews) {
        this.mListViews = mListViews;
    }

    @Override
    public int getCount() {
        return  mListViews.size();//返回页卡的数量
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;//官方提示这样写
    }
}
