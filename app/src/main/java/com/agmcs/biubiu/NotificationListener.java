package com.agmcs.biubiu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;


import com.agmcs.biubiu.Models.BarrageItem;
import com.agmcs.biubiu.Views.BarrageView;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by agmcs on 2015/6/12.
 */
public class NotificationListener extends NotificationListenerService {
    public static final int ADD_VIEW = 0;
    public static final int REMOVE_VIEW = 1;
    public static final int ADD_AND_POST = 2;
    public static final int POST = 3;


    private BarrageItem barrageItem;
    private WindowManager windowManager;
    private BarrageView barrageView;
    private boolean is_added = false;
    private Set<String> package_set;
    private WindowManager.LayoutParams layoutParams;
    private MyHandler handler = new MyHandler();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("hihi1222","startCommand");

        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(NotificationListener.this);
        package_set = spf.getStringSet("white_list", new HashSet<String>());

        windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        layoutParams.format = 1;
        layoutParams.flags =WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN|
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        layoutParams.y = 0;
        layoutParams.gravity = Gravity.START |
                Gravity.TOP;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;


        if(barrageView == null){
            barrageView = new BarrageView(NotificationListener.this);
            barrageView.setAddRemoveCallBack(new BarrageView.AddRemoveCallBack() {
                @Override
                public void removeView() {
                    if (is_added){
                        handler.sendEmptyMessage(REMOVE_VIEW);
                    }
                }
            });
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("hihi1994","destroy");
        if(is_added){
            windowManager.removeView(barrageView);
        }
    }

    @Override
    public void onNotificationPosted(final StatusBarNotification sbn) {
        if(package_set.contains(sbn.getPackageName())){
            if(!is_added){//还未添加时
                Message msg = handler.obtainMessage();
                msg.what = ADD_AND_POST;
                msg.obj = sbn.getNotification().tickerText.toString();
                handler.sendMessage(msg);
            }else{
                Message msg = handler.obtainMessage();
                msg.what = POST;
                msg.obj = sbn.getNotification().tickerText.toString();
                handler.sendMessage(msg);
            }
        }
    }


    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
//        super.onNotificationRemoved(sbn);
    }



    public class MyHandler extends Handler{
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case REMOVE_VIEW:
                    Log.d("hihi1997","remove");
                    windowManager.removeView(barrageView);
                    is_added = false;
                    break;
                case ADD_VIEW:
                    Log.d("hihi1997","added");
                    windowManager.addView(barrageView, layoutParams);
                    is_added = true;
                    break;
                case ADD_AND_POST:
                    windowManager.addView(barrageView, layoutParams);
                    Log.d("hihi1997", "addedfasfdsgsd");
                    is_added = true;
                    final String info = (String) msg.obj;
                    barrageView.post(new Runnable() {
                        @Override
                        public void run() {
                            barrageItem = new BarrageItem();
                            barrageItem.setText(info);
                            barrageView.addBarrage(barrageItem);
                        }
                    });
                    break;
                case POST:
                    final String info2 = (String) msg.obj;
                    barrageView.post(new Runnable() {
                        @Override
                        public void run() {
                            barrageItem = new BarrageItem();
                            barrageItem.setText(info2);
                            barrageView.addBarrage(barrageItem);
                        }
                    });
                    break;
            }
        }
    }
}
