package com.agmcs.FloatingShadow.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import com.agmcs.FloatingShadow.FxService;

/**
 * Created by agmcs on 2015/2/13.
 */
//实现开机启动
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean is_autorun = PreferenceManager
                .getDefaultSharedPreferences(context)
                .getBoolean("is_autorun", false);
        if(is_autorun){
            context.startService(new Intent(context,FxService.class));
        }
    }
}