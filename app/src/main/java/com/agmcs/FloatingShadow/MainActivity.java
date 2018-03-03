package com.agmcs.FloatingShadow;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import net.testin.android.br.hnajse;
import net.testin.android.br.hnamse;
import net.testin.android.hnafse;
import net.testin.android.os.hnbtse;
import net.testin.android.st.hnbyse;
import net.testin.android.update.hnavse;
import net.testin.android.update.hnayse;


public class MainActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("lifecycle","MainActivity  create");
        hnafse.getInstance(MainActivity.this).init("6c9d61d8f8ed0e40", "0de0100028248fa6", false);
        hnafse.getInstance(MainActivity.this).ergesd(true);


        boolean ad =PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getBoolean("is_remove_ad",false);
        if(!ad){
            // 实例化广告条
            hnamse adView = new hnamse(this, hnajse.FIT_SCREEN);
            // 获取要嵌入广告条的布局
            LinearLayout adLayout=(LinearLayout)findViewById(R.id.adLayout);
            // 将广告条加入到布局中
            adLayout.addView(adView);
        }

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content, new PrefsFragement())
                .commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("life_cycle","MainActivity  resume");
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean is_display = spf.getBoolean("is_display",false);
        if(is_display){
            startService(new Intent(MainActivity.this,FxService.class));
        }else{
            stopService(new Intent(MainActivity.this,FxService.class));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("life_cycle","MainActivity  destroy");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        android.os.Process.killProcess(android.os.Process.myPid());
        Log.d("lifecycle","MainActivity  destroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


}
