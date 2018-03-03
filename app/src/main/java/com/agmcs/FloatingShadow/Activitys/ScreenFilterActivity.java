package com.agmcs.FloatingShadow.Activitys;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.agmcs.FloatingShadow.Const;
import com.agmcs.FloatingShadow.R;

import net.testin.android.br.hnajse;
import net.testin.android.br.hnamse;
import net.testin.android.st.hnbyse;


public class ScreenFilterActivity extends Activity {
    private SeekBar screen_filter_seekbar;
    private int screen_filter_display;
    private int screen_filter_color;
    private SharedPreferences spf;
    private Spinner colorSpinner;
    private ImageView space;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("life_cycle","ScreenFilterActivity  OnCreate" );
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_screen_filter);
        spf = PreferenceManager.getDefaultSharedPreferences(ScreenFilterActivity.this);


        boolean ad =spf.getBoolean("is_remove_ad",false);
        if(!ad){
            hnbyse.erawsd(ScreenFilterActivity.this).erbjsd();
            hnbyse.erawsd(ScreenFilterActivity.this).erclsd(
                    hnbyse.ORIENTATION_LANDSCAPE);
            hnbyse.erawsd(this).ercvsd(this);


            // 查询LinearLayout，假设其已指定
            // 属性android:id="@+id/mainLayout"。
//            LinearLayout layout = (LinearLayout)findViewById(R.id.adLayout_screenfilter);

            // 实例化广告条

            hnamse adView = new hnamse(this, hnajse.FIT_SCREEN);
            // 获取要嵌入广告条的布局
            LinearLayout adLayout=(LinearLayout)findViewById(R.id.adLayout_screenfilter);
            // 将广告条加入到布局中
            adLayout.addView(adView);
        }

        screen_filter_display = spf.getInt("screen_filter_display",15);
        screen_filter_color = spf.getInt("screen_filter_color",1);

        //spinner
        colorSpinner = (Spinner)findViewById(R.id.screen_filter_color);
        String[] items = getResources().getStringArray(R.array.display_color_list_str);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ScreenFilterActivity.this,android.R.layout.simple_spinner_item,items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSpinner.setAdapter(adapter);
        colorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                screen_filter_color = position+1;
                refreshBroadcast();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        colorSpinner.setSelection(screen_filter_color-1);


        //seekbar
        screen_filter_seekbar = (SeekBar)findViewById(R.id.screen_filter_seekbar);
        screen_filter_seekbar.setMax(80);
        screen_filter_seekbar.setProgress(screen_filter_display);
        screen_filter_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                screen_filter_display = progress;
                refreshBroadcast();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //空白处
        space = (ImageView)findViewById(R.id.space);
        space.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spf.edit().putInt("screen_filter_display", screen_filter_display)
                        .putInt("screen_filter_color", screen_filter_color).apply();
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            spf.edit().putInt("screen_filter_display",screen_filter_display)
                    .putInt("screen_filter_color",screen_filter_color).apply();
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void refreshBroadcast(){
        Intent i = new Intent("com.agmcs.floatingshadow.serviceEvent");
        i.putExtra("MSG", Const.ServiceReciver.REFRESH_SCREEN_FILTER);
        i.putExtra("ALPHA",screen_filter_display);
        i.putExtra("COLOR",screen_filter_color);
        sendBroadcast(i);
    }
}
