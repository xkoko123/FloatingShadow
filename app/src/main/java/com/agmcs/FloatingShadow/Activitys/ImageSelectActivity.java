package com.agmcs.FloatingShadow.Activitys;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Window;

import com.agmcs.FloatingShadow.FragLocalImage;
import com.agmcs.FloatingShadow.FragSDImage;
import com.agmcs.FloatingShadow.R;
import com.agmcs.FloatingShadow.adapter.FragAdapter;

import java.util.ArrayList;
import java.util.List;


public class ImageSelectActivity extends FragmentActivity {
    private ViewPager vp;
    String[] titleList = new String[]{"内置图标","本地图标"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_image_select);

        vp = (ViewPager)findViewById(R.id.vp2);
        List<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(new FragLocalImage());
        fragments.add(new FragSDImage());
        FragAdapter adapter = new FragAdapter(getSupportFragmentManager(),fragments,titleList);
        vp.setAdapter(adapter);
    }

}
