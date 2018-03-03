package com.agmcs.FloatingShadow;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

import java.util.Set;

/**
 * Created by agmcs on 2015/2/13.
 */
public class MyAccessibility extends AccessibilityService {
    private EventReceiver receiver;
    private WindowManager windowManager;
    private WindowManager.LayoutParams params;
    private View floatingView;
    private Runnable removeRunnable;
    private Runnable addRunnable;


    private Handler handler = new Handler();


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        Log.d("life_cycle","MyAccessibility  onServicConnected");

        receiver = new EventReceiver();
        registerReceiver(receiver, new IntentFilter("com.agmcs.floatingshadow.accessibility"));
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {
        Log.d("life_cycle","MyAccessibility  interrupt");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("life_cycle","MyAccessibility  destroy");
        if(floatingView != null){
            try {
                windowManager.removeView(floatingView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class EventReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getIntExtra("MSG",0)){
                case 0:{
                    break;
                }
                case Const.ServiceReciver.ACCESSIBILITY_BUTTON:{
                    switch (intent.getIntExtra("BUTTON",0)){
                        case 0:{
                            break;
                        }
                        case Const.Function.RECENT_APP_BY_ACCESSIBILITY:{
                            performGlobalAction(GLOBAL_ACTION_RECENTS);
                            break;
                        }
                        case Const.Function.NOTIFICATIONS_BY_ACCESSIBILITY:{
                            performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS);
                            break;
                        }
                        case Const.Function.HOME_BY_ACCESSIBILITY:{
                            performGlobalAction(GLOBAL_ACTION_HOME);
                            break;
                        }
                        case Const.Function.BACK_BY_ACCESSIBILITY:{
                            performGlobalAction(GLOBAL_ACTION_BACK);

                            break;
                        }
                    }
                    break;
                }
                case Const.ServiceReciver.IMMERSIVE_MODE:{
                    immersiveMode(intent.getBooleanExtra("ENABLE", false)
                            , intent.getBooleanExtra("STATUSBAR", true));
                    break;
                }

            }


        }

    }
    private void immersiveMode(boolean enable, boolean hide_status_bar){
        if (params == null) {
            params = new WindowManager.LayoutParams(
                    0, 0,
                    0, 0,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN |
                            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    PixelFormat.OPAQUE);
            params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
            params.windowAnimations = android.R.style.Animation_Toast;
            params.gravity = Gravity.BOTTOM | Gravity.END;
        }
        if (windowManager == null) {
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        }
        if (floatingView == null) {
            floatingView = new View(getApplicationContext());

            floatingView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        whenBackPress();
                        return true;
                    }
                    return false;
                }
            });
        }

        if (enable) {
            if (!hide_status_bar) {
                floatingView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE|
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            } else {
                floatingView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
            windowManager.addView(floatingView, params);


        } else {
            try {
                windowManager.removeView(floatingView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @TargetApi(16)
    private void whenBackPress(){
        //当按下返回键时,先removeview 200毫秒后按下back 200毫秒后addview
        if(addRunnable == null){
            addRunnable = new Runnable() {
                @Override
                public void run() {
                    windowManager.addView(floatingView,params);
                }
            };
        }
        if(removeRunnable == null){
            removeRunnable = new Runnable() {
                @Override
                public void run() {
                    performGlobalAction(GLOBAL_ACTION_BACK);
                    handler.postDelayed(addRunnable,400);
                }
            };
        }
        windowManager.removeView(floatingView);
        handler.removeCallbacks(removeRunnable);
        handler.removeCallbacks(addRunnable);
        handler.postDelayed(removeRunnable,400);
    }



}
