package com.agmcs.FloatingShadow;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.agmcs.FloatingShadow.Activitys.AppSeclectActivity;
import com.agmcs.FloatingShadow.Activitys.OpenDevicePolicyManagerActivity;
import com.agmcs.FloatingShadow.Activitys.ScreenFilterActivity;
import com.agmcs.FloatingShadow.Receivers.LockReceiver;
import com.agmcs.FloatingShadow.Utils.AppUtils;
import com.agmcs.FloatingShadow.Utils.DensityUtil;
import com.agmcs.FloatingShadow.Utils.RootShellCmd;
import com.agmcs.FloatingShadow.Views.VerticalViewPager;
import com.agmcs.FloatingShadow.adapter.MyPagerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by agmcs on 2015/2/10.
 */
public class FxService extends Service{
    private RootShellCmd root = null;
    private Vibrator vibrator;
    private WindowManager windowManager;
    private WindowManager.LayoutParams BtnParams;
    private WindowManager.LayoutParams ScreenFilterParams = null;
    private SharedPreferences spf;
    private ImageView bb;
    private NotificationManager notificationManager;
    private Receiver receiver;

    private RelativeLayout relativeLayout;


    private boolean keep_screen_on = false;//是否现在屏幕保持常亮
    private View screenOnView;

    private View screenFilter;//光波过滤器
    private  boolean is_screenFilter_on = false;//光波过滤器状态

    private float btn_alpha;//默认状态按钮透明度

    private boolean hide_statusbar;//沉浸模式中是否显示状态栏
    private boolean is_in_immersive_mode = false;
    private boolean is_click;




    private boolean isRoot = false;


    //前9个为快捷面板按键,后6个为触摸事件
    private Operate[] operates = new Operate[]{
            new Operate(),
            new Operate(),
            new Operate(),
            new Operate(),
            new Operate(),
            new Operate(),
            new Operate(),
            new Operate(),
            new Operate(),
            new Operate(),
            new Operate(),
            new Operate(),
            new Operate(),
            new Operate(),
            new Operate(),
            new Operate(),
    };

    private ImageView hide_btn;

    private Handler handler = new Handler();

    private RotateReceiver rotateReciver;
    private boolean is_in_land = false;



    private Runnable darkRunnable = new Runnable() {//2秒自动变暗
        @Override
        public void run() {
            if(is_button_show){
                BtnParams.alpha = auto_dark_alpha_value;
                windowManager.updateViewLayout(bb, BtnParams);
            }
        }
    };

    private boolean is_autodark;//自动变暗
    private float auto_dark_alpha_value;//自动变暗的透明度

    private Runnable clickRunnable = new Runnable() {
        @Override
        public void run() {
            doOperate(Const.Action.CLICK);
            is_click = false;
        }
    };

    private int double_click_delay;
    private boolean fixedLocal = true;//固定位置

    private boolean is_button_show = false;
    //按键位置
    private int last_x = 0;
    private int last_y = 0;
    private int last_x_l = 0;
    private int last_y_l = 0;



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("life_cycle", "FxService  OnCreate");
        root = RootShellCmd.getInstance(getApplicationContext());
        windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
        //振动器
        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);

        //注册广播
        receiver = new Receiver();
        registerReceiver(receiver, new IntentFilter("com.agmcs.floatingshadow.serviceEvent"));
        rotateReciver = new RotateReceiver();//旋转监听
        registerReceiver(rotateReciver,new IntentFilter("android.intent.action.CONFIGURATION_CHANGED"));

        init();

        if(spf == null){
            spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        }

        //在通知中心显示常驻图标
        Intent i = new Intent(this,MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this,0,i,0);
        Notification notification = new NotificationCompat.Builder(getApplicationContext())
                .setContentIntent(pi)
                .setContentText("Floating Shadow is Running")
                .setContentTitle(getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_stat_s)
                .setPriority(Notification.PRIORITY_MIN)
                .build();

        startForeground(9, notification);
//        启动Service时读取按键位置
        last_x_l = spf.getInt("last_x_l",200);
        last_y_l = spf.getInt("last_y_l",200);
        last_x = spf.getInt("last_x",500);
        last_y = spf.getInt("last_y",500);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("life_cycle", "Fxservice  OnStartCommand");


        //ShaedPreferences中读取设置
        setDefault();

        if(is_button_show){
            windowManager.removeView(bb);
            is_button_show = false;
        }

        try{
            windowManager.removeView(relativeLayout);
        }catch (Exception e){
            e.printStackTrace();
        }


        init_setting();
        //监听控件触摸事件

        //添加按钮
        windowManager.addView(bb, BtnParams);
        is_button_show = true;

        //注册自动变暗事件
        if(is_autodark){
            handler.postDelayed(darkRunnable,1500);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("life_cycle","Fxservice  OnDestroy" );

        //关闭所有通知中心信息
        try {
            notificationManager.cancelAll();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //关闭全屏模式
        if(is_in_immersive_mode){
            doSth(Const.Function.IMMERSIVE_TOGGLE);
        }

        if(keep_screen_on){
            windowManager.removeView(screenOnView);
        }
        if(is_button_show){
            windowManager.removeView(bb);
            is_button_show = false;
        }

        //关闭护目镜
        if(is_screenFilter_on){
            windowManager.removeView(screenFilter);
            is_screenFilter_on = false;

        }

        //注销Service时保存按键位置
        spf.edit().putInt("last_x_l",last_x)
                .putInt("last_y_l",last_y_l)
                .putInt("last_x",last_x)
                .putInt("last_y",last_y)
                .commit();


        //销毁时注销广播,移除按钮
        unregisterReceiver(receiver);
        unregisterReceiver(rotateReciver);
        super.onDestroy();
    }


    private void init(){//不需要动态更新的部分,整个生命周期只执行一次
        if(spf == null){
            spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        }

        //悬浮按钮
        BtnParams = new WindowManager.LayoutParams();
        BtnParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        BtnParams.format = 1;
        btn_alpha = spf.getFloat("btn_alpha_value",1.0f);
        auto_dark_alpha_value = btn_alpha * 0.65f;
        BtnParams.alpha = btn_alpha;
        BtnParams.gravity = Gravity.START |
                Gravity.TOP;
        //代码中设置的单位为px,需把dp换算成px来适配屏幕;
        int size = DensityUtil.dip2px(getApplicationContext(), spf.getInt("size_value", 48));
        BtnParams.height = size;
        BtnParams.width = size;


        hide_btn = (ImageView)LayoutInflater.from(getApplicationContext()).inflate(R.layout.hide_item,null);




        //最外层(包含Viewpager)
        relativeLayout = (RelativeLayout)LayoutInflater.from(getApplicationContext()).inflate(R.layout.toolsbar_md, null);

        relativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    windowManager.removeView(relativeLayout);
                }catch (Exception e){
                    e.printStackTrace();
                }
                return true;
            }
        });

        bb = (ImageView)LayoutInflater.from(getApplicationContext()).inflate(R.layout.heihei,null);
        bb.setOnTouchListener(new View.OnTouchListener() {
            private int beginX, beginY;
            private boolean isMove;
            private boolean isLongPress;
            private int width = BtnParams.width;

            private WindowManager.LayoutParams hideParams = new WindowManager.LayoutParams();

            private Runnable hideButtonRunnable = new Runnable() {
                @Override
                public void run() {
                    hideParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                    hideParams.format = 1;
                    hideParams.width = width;
                    hideParams.height = width;
                    hideParams.gravity = Gravity.START |
                            Gravity.TOP;
                    hideParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            |WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                    hideParams.windowAnimations = android.R.style.Animation_Dialog;

                    Point point = new Point();
                    windowManager.getDefaultDisplay().getSize(point);
                    hideParams.y = point.y - width;
                    hideParams.x = point.x/2 - width/2;
                    try {
                        windowManager.addView(hide_btn, hideParams);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            private Runnable longpressRunnable = new Runnable() {
                @Override
                public void run() {
                    isLongPress = true;
                    vibrator.vibrate(20);
                    //如果固定位置的话就不显示隐藏按钮
                    if(!fixedLocal){
                        handler.postDelayed(hideButtonRunnable, 400);
                    }
                }
            };


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:{
                        if(is_autodark){
                            BtnParams.alpha = btn_alpha;
                            windowManager.updateViewLayout(bb, BtnParams);
                            handler.removeCallbacks(darkRunnable);
                        }
                        beginX = (int)event.getRawX();
                        beginY = (int)event.getRawY();
                        isMove = false;
                        isLongPress = false;
                        handler.postDelayed(longpressRunnable,500);
                        break;
                    }
                    case MotionEvent.ACTION_MOVE:{
                        if(isLongPress){
                            int x = (int)event.getRawX();
                            int y = (int)event.getRawY();
                            //长按移动
                            if (!fixedLocal && (isMove ||
                                    (Math.abs(x - beginX)>20 ||
                                    Math.abs(y - beginY)>20 ))
                                    ){//长按并移动
                                BtnParams.x = x-(int)bb.getWidth()/2;
                                BtnParams.y = y-(int)bb.getHeight();
                                windowManager.updateViewLayout(bb, BtnParams);
                                isMove = true;

                                if((hideParams.x-width/2 <x && x<hideParams.x + width*1.5)&&
                                        (hideParams.y-width/2<y && y<hideParams.y +width*1.5)
                                        ){
                                    BtnParams.x = hideParams.x +(width - BtnParams.width)/2;
                                    BtnParams.y = hideParams.y - (width - BtnParams.width)/2;
                                    windowManager.updateViewLayout(bb, BtnParams);
                                }
                            }
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP:{
                        handler.removeCallbacks(longpressRunnable);
                        handler.removeCallbacks(hideButtonRunnable);
                        try {
                            windowManager.removeView(hide_btn);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //按钮自动变透明计时器
                        if(is_autodark){
                            handler.postDelayed(darkRunnable,1500);
                        }
                        if(is_click){
                            handler.removeCallbacks(clickRunnable);
                            doOperate(Const.Action.DOUBLIE_CLICK);
                            is_click = false;
                            break;
                        }
                        //not move
                        if(!isMove){
                            if(fixedLocal){
                                int y = (int)event.getRawY() - beginY;
                                int x = (int)event.getRawX() - beginX;
                                if(Math.abs(y)>30 || Math.abs(x)>30){
                                    if(y>30 &&(y>Math.abs(x))){//下
                                        doOperate(Const.Action.DOWN);
                                    }else if(y<-30 &&(-y>Math.abs(x))){//上
                                        doOperate(Const.Action.UP);
                                    }else if(x>30 && (x>Math.abs(y))){//右
                                        doOperate(Const.Action.RIGHT);
                                    }else if(x<-30 && (-x>Math.abs(y))){//左
                                        doOperate(Const.Action.LEFT);
                                    }
                                }else if(isLongPress){
                                    doOperate(Const.Action.LONG_PRESS);
                                }else{//单击
                                    handler.postDelayed(clickRunnable, double_click_delay);
                                    is_click = true;
                                }
                            }else{
                                if(!isLongPress){
                                    int y = (int)event.getRawY() - beginY;
                                    int x = (int)event.getRawX() - beginX;
                                    if(y>30 &&(y>Math.abs(x))){//下
                                        doOperate(Const.Action.DOWN);
                                    }else if(y<-30 &&(-y>Math.abs(x))){//上
                                        doOperate(Const.Action.UP);
                                    }else if(x>30 && (x>Math.abs(y))){//右
                                        doOperate(Const.Action.RIGHT);
                                    }else if(x<-30 && (-x>Math.abs(y))){//左
                                        doOperate(Const.Action.LEFT);
                                    }else{//单击
                                        handler.postDelayed(clickRunnable, double_click_delay);
                                        is_click = true;
                                    }
                                }else{//长按
                                    doOperate(Const.Action.LONG_PRESS);
                                }
                            }
                            //moved
                        }else{
                            if((hideParams.x-width/2 <(int)event.getRawX() &&
                                    (int)event.getRawX()<hideParams.x + width*1.5)&&
                                    (hideParams.y-width/2<(int)event.getRawY() &&
                                            (int)event.getRawY()<hideParams.y +width*1.5)
                                    ){
                                //移动到隐藏区
                                BtnParams.x = beginX;
                                BtnParams.y = beginY;
                                doHide();
                                //如果触摸结束时移动了位置则,保存离开时的位置
                            }else if(is_in_land){
                                last_x_l = BtnParams.x;
                                last_y_l = BtnParams.y;
                            }else{
                                last_x = BtnParams.x;
                                last_y = BtnParams.y;
                            }
                        }
                        break;
                    }
                }
                return true;
            }
        });
    }

    //初始化需要动态更新的设置信息
    private void init_setting(){
        Log.d("life_cycle","Fxservice  更新动态更新的设置" );

        if(!is_autodark){
            BtnParams.alpha = btn_alpha;
        }
        BtnParams.x = last_x;
        BtnParams.y = last_y;
        if(spf.getBoolean("keyboard_above",true)){
            BtnParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    |WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        }else{
            BtnParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    |WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    |WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        }

//        setToolsDrawble(btns);//设置面板上图标

        //设置影子图标
        if(spf.getBoolean("isImgfromSd",false)){
            try {
                String path = spf.getString("imgPath","");
                bb.setImageBitmap(BitmapFactory.decodeFile(path));
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),getString(R.string.img_wrong),Toast.LENGTH_SHORT).show();
                bb.setImageResource(R.drawable.skin26);
            }
        }else{
            int resId = spf.getInt("imgResId", R.drawable.skin26);
            bb.setImageResource(resId);
        }
        double_click_delay = Integer.parseInt(spf.getString("double_click", "200"));
    }


    //按键num执行操作
    private void doOperate(final int num){
        Log.d("life_cycle","Fxservice  dooperate" );
        int real_num = num-1;
        switch (operates[real_num].mode){
            case 1:{//执行内置事件
                doSth(operates[real_num].dowhat);
                break;
            }
            case 0:{//打开程序
                Intent i = new Intent();
                i.setClassName(operates[real_num].packageName,operates[real_num].activityPath);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                break;
            }
            case 2:{//打开快捷方式
                Intent i = new Intent();
                i.setClassName(operates[real_num].packageName,operates[real_num].activityPath);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                break;
            }
            case 3:{//未设置时
                Intent i = new Intent(FxService.this,AppSeclectActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("BTN", num);
                startActivity(i);
                break;
            }
        }


    }


    //面板按钮监听器
    private class BtnListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            windowManager.removeView(relativeLayout);
            switch(v.getId()){
                case R.id.btn1:{
                    doOperate(1);
                    break;
                }
                case R.id.btn2:{
                    doOperate(2);
                    break;
                }
                case R.id.btn3:{
                    doOperate(3);
                    break;
                }
                case R.id.btn4:{
                    doOperate(4);
                    break;
                }
                case R.id.btn5:{
                    doOperate(5);
                    break;
                }
                case R.id.btn6:{
                    doOperate(6);
                    break;
                }
                case R.id.btn7:{
                    doOperate(7);
                    break;
                }
                case R.id.btn8:{
                    doOperate(8);
                    break;
                }
                case R.id.btn9:{
                    doOperate(9);
                    break;
                }
            }
        }
    }


    //隐藏按钮并显示一个带有发送"显示按钮广播意图"的消息
    private void doHide(){
        Intent i = new Intent("com.agmcs.floatingshadow.serviceEvent");
        i.putExtra("MSG", Const.ServiceReciver.SHOW_BUTTON);
        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        handler.removeCallbacks(darkRunnable);
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(getApplicationContext())
                .setTicker(getString(R.string.redisplay))
                .setSmallIcon(R.drawable.ic_stat_s)
                .setContentTitle(getString(R.string.redisplay))
                .setContentIntent(pi)
                .build();
        notification.flags = Notification.FLAG_NO_CLEAR;
        notificationManager.notify(0,notification);
        is_button_show = false;
        //移除
        windowManager.removeView(bb);
    }

    private void doSth(int msg){
        switch (msg){
            case Const.Function.BACK:{
                root.simulateKey(KeyEvent.KEYCODE_BACK);
                break;
            }
            case Const.Function.HOME:{
//                root.simulateKey(KeyEvent.KEYCODE_HOME);
//                root.exec2("dumpsys activity | findstr \"top-activit\"");
                root.switchLastApp(getApplicationContext());
//                ShellUtils.CommandResult result = ShellUtils.execCommand("dumpsys activity recents |grep 'Recent #d1:'", true);
//                Log.d("hihi123",result.successMsg);
                break;
            }
            case Const.Function.MENU:{
                root.simulateKey(KeyEvent.KEYCODE_MENU);
                break;
            }
            case Const.Function.LOCK:{//锁屏
                root.simulateKey(KeyEvent.KEYCODE_POWER);
                break;
            }
            case Const.Function.APP_SWITCH:{//多任务
                root.simulateKey(KeyEvent.KEYCODE_APP_SWITCH);
                break;
            }
            case Const.Function.SHOW:{//显示面板
                CardView cardView = (CardView)relativeLayout.findViewById(R.id.toolsbar_md);
                VerticalViewPager viewPager = (VerticalViewPager)cardView.findViewById(R.id.tools_viewpager);
                LinearLayout tools_btns = (LinearLayout)LayoutInflater.from(getApplicationContext()).inflate(R.layout.tools_btns, null);

                BtnListener btnListener = new BtnListener();
                ImageButton[] btns = new ImageButton[9];

                RelativeLayout tools_light_volume = (RelativeLayout)LayoutInflater.from(getApplicationContext()).inflate(R.layout.tools_light_volume, null);
                List<View> view_list = new ArrayList<View>();
                view_list.add(tools_btns);
                view_list.add(tools_light_volume);
                viewPager.setAdapter(new MyPagerAdapter(view_list));

                btns[0] = (ImageButton) tools_btns.findViewById(R.id.btn1);
                btns[0].setOnClickListener(btnListener);
                btns[1] = (ImageButton) tools_btns.findViewById(R.id.btn2);
                btns[1].setOnClickListener(btnListener);
                btns[2] = (ImageButton) tools_btns.findViewById(R.id.btn3);
                btns[2].setOnClickListener(btnListener);
                btns[3] = (ImageButton) tools_btns.findViewById(R.id.btn4);
                btns[3].setOnClickListener(btnListener);
                btns[4] = (ImageButton) tools_btns.findViewById(R.id.btn5);
                btns[4].setOnClickListener(btnListener);
                btns[5] = (ImageButton) tools_btns.findViewById(R.id.btn6);
                btns[5].setOnClickListener(btnListener);
                btns[6] = (ImageButton) tools_btns.findViewById(R.id.btn7);
                btns[6].setOnClickListener(btnListener);
                btns[7] = (ImageButton) tools_btns.findViewById(R.id.btn8);
                btns[7].setOnClickListener(btnListener);
                btns[8] = (ImageButton) tools_btns.findViewById(R.id.btn9);
                btns[8].setOnClickListener(btnListener);

                setToolsDrawble(btns);

                viewPager.setCurrentItem(0);
                SeekBar light = (SeekBar)tools_light_volume.findViewById(R.id.tools_light);
                int  light_progress = Settings.System.getInt(getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS, 255);
                light.setProgress(light_progress);
                light.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        Settings.System.putInt(getContentResolver(),
                                Settings.System.SCREEN_BRIGHTNESS, progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                final AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

                SeekBar volume_music = (SeekBar)tools_light_volume.findViewById(R.id.tools_volume_music);
                volume_music.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
                volume_music.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                volume_music.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

                SeekBar volume_system = (SeekBar)tools_light_volume.findViewById(R.id.tools_volume_system);
                volume_system.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM));
                volume_system.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM));
                volume_system.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, progress, 0);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });


                //弹出窗口

                WindowManager.LayoutParams panal_params = new WindowManager.LayoutParams();
                panal_params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
                panal_params.format = 1;
                panal_params.flags =  WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        |WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        |WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        |WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE ;
                panal_params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_MASK_STATE;
                panal_params.height = WindowManager.LayoutParams.MATCH_PARENT;
                panal_params.width = WindowManager.LayoutParams.MATCH_PARENT;
                panal_params.gravity= Gravity.CENTER;
                panal_params.windowAnimations = android.R.style.Animation_InputMethod;

                try{
                    setToolsDrawble(btns);
                    windowManager.addView(relativeLayout, panal_params);
                }catch(Exception e){
                    e.printStackTrace();
                }
                break;
            }
            case Const.Function.HIDE:{//隐藏面板
                doHide();
                break;
            }
            case Const.Function.SLEEP:{
                root.killTopActivity();
                break;
            }
            case Const.Function.BACK_BY_ACCESSIBILITY:{
                Intent i = new Intent("com.agmcs.floatingshadow.accessibility");
                i.putExtra("MSG", Const.ServiceReciver.ACCESSIBILITY_BUTTON);
                i.putExtra("BUTTON", Const.Function.BACK_BY_ACCESSIBILITY);
                sendBroadcast(i);
                Log.d("hihihi", "发送");
                break;
            }
            case Const.Function.HOME_BY_ACCESSIBILITY:{
                Intent i = new Intent("com.agmcs.floatingshadow.accessibility");
                i.putExtra("MSG", Const.ServiceReciver.ACCESSIBILITY_BUTTON);
                i.putExtra("BUTTON", Const.Function.HOME_BY_ACCESSIBILITY);
                sendBroadcast(i);
                break;
            }
            case Const.Function.NOTIFICATIONS_BY_ACCESSIBILITY:{
                Intent i = new Intent("com.agmcs.floatingshadow.accessibility");
                i.putExtra("MSG", Const.ServiceReciver.ACCESSIBILITY_BUTTON);
                i.putExtra("BUTTON", Const.Function.NOTIFICATIONS_BY_ACCESSIBILITY);
                sendBroadcast(i);
                break;
            }
            case Const.Function.RECENT_APP_BY_ACCESSIBILITY:{
                Intent i = new Intent("com.agmcs.floatingshadow.accessibility");
                i.putExtra("MSG", Const.ServiceReciver.ACCESSIBILITY_BUTTON);
                i.putExtra("BUTTON", Const.Function.RECENT_APP_BY_ACCESSIBILITY);
                sendBroadcast(i);
                break;
            }
            case Const.Function.WIFI_TOGGLE:{
                WifiManager manager = (WifiManager)getSystemService(WIFI_SERVICE);
                if(manager.isWifiEnabled()){
                    manager.setWifiEnabled(false);
                }else{
                    manager.setWifiEnabled(true);
                }
                break;
            }
            case Const.Function.BLUETOOLS_TOGGLE:{
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                if(adapter.isEnabled()){
                    adapter.disable();
                }else{
                    adapter.enable();
                }
                break;
            }
            case Const.Function.ROTATION_TOGGLE:{
                try {
                    int state = Settings.System.getInt(getApplicationContext().getContentResolver(),
                            Settings.System.ACCELEROMETER_ROTATION);
                    if (state == 1){
                        Settings.System.putInt(getContentResolver(), "accelerometer_rotation", 0);
                    }else{
                        Settings.System.putInt(getContentResolver(), "accelerometer_rotation", 1);
                    }
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            }
            case Const.Function.GPRS_TOGGLE:{
                boolean isopen = false;
                isopen = AppUtils.getMobileDataStatus(getApplicationContext());
                Log.d("hihihio", isopen + "");
                if(isopen){
                    AppUtils.toggleMobileData(getApplicationContext(), false);
                }else {
                    AppUtils.toggleMobileData(getApplicationContext(), true);
                }
                break;
            }
            case Const.Function.IMMERSIVE_TOGGLE:{
                if(Build.VERSION.SDK_INT <19){
                    Toast.makeText(getApplicationContext(),getString(R.string.support4_4),Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent i = new Intent("com.agmcs.floatingshadow.accessibility");
                i.putExtra("MSG", Const.ServiceReciver.IMMERSIVE_MODE);
                i.putExtra("ENABLE",!is_in_immersive_mode);
                i.putExtra("STATUSBAR",hide_statusbar);
                is_in_immersive_mode = !is_in_immersive_mode;
                sendBroadcast(i);
                break;
            }
            case Const.Function.KEEP_SCREEN_ON:{
                if(keep_screen_on){
                    windowManager.removeView(screenOnView);
                    keep_screen_on = false;
                    screenOnView = null;
                }else{
                    if(screenOnView == null){
                        screenOnView = new View(getApplicationContext());
                    }
                    WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                    layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            |WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
                            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    layoutParams.width = 1;
                    layoutParams.height = 1;
                    layoutParams.x = 0;
                    layoutParams.y = 0;
                    layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
                    layoutParams.format = 1;
                    layoutParams.alpha = 0f;
                    windowManager.addView(screenOnView,layoutParams);
                    keep_screen_on = true;
                }
                break;
            }
            case Const.Function.SCREEN_FILTER:{
                if(is_screenFilter_on){
                    windowManager.removeView(screenFilter);
                    is_screenFilter_on = false;
                    screenFilter = null;
                    //通知中心控制
                    notificationManager.cancel(1);
                }else{
                    if(screenFilter == null){
                        screenFilter = new View(getApplicationContext());
                        selectScreenFilterColor();
                    }

                    if(ScreenFilterParams == null){
                        //滤镜
                        ScreenFilterParams = new WindowManager.LayoutParams();
                        ScreenFilterParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
                        ScreenFilterParams.format = 1;
                        ScreenFilterParams.flags =WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
                                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN|
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
                        ScreenFilterParams.y = 0;
                        ScreenFilterParams.gravity = Gravity.START |
                                Gravity.TOP;
                        ScreenFilterParams.height = WindowManager.LayoutParams.MATCH_PARENT;
                        ScreenFilterParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                    }

                    windowManager.addView(screenFilter, ScreenFilterParams);
                    is_screenFilter_on = true;

                    if(notificationManager == null){
                        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                    }

                    Intent i = new Intent(getApplicationContext(),ScreenFilterActivity.class);

                    PendingIntent pi = PendingIntent.getActivity(getApplicationContext(),0,i,PendingIntent.FLAG_CANCEL_CURRENT);
                    Notification notification = new NotificationCompat.Builder(getApplicationContext())
                            .setTicker(getString(R.string.screen_filter_dialog))
                            .setSmallIcon(R.drawable.ic_stat_s)
                            .setContentTitle(getString(R.string.screen_filter_dialog))
                            .setContentIntent(pi)
                            .build();

                    notification.flags = Notification.FLAG_NO_CLEAR;
                    notificationManager.notify(1, notification);
                }
                break;
            }
            case Const.Function.LAST_APP_SWITCH: {
                //5.0以上优先使用root实现
                if(Build.VERSION.SDK_INT>= 21){
                    //如果root了使用root
                    if(isRoot){
                        root.switchLastApp(getApplicationContext());
                        break;
                    }
//                    否则使用UsageStatsManager
                    ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                    PackageManager pm = getApplicationContext().getPackageManager();
                    Bundle anim =
                            ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out).toBundle();

                    ActivityInfo homeInfo = new Intent(Intent.ACTION_MAIN).addCategory(
                            Intent.CATEGORY_HOME).resolveActivityInfo(pm, 0);


                    UsageStatsManager mUsageStatsManager = (UsageStatsManager)getSystemService("usagestats");
                    long time = System.currentTimeMillis();
                    // We get usage stats for the last 10 seconds
                    List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000*10, time);
                    // Sort the stats by the last time used
                    if(stats != null) {
                        SortedMap<Long,UsageStats> mySortedMap = new TreeMap<Long,UsageStats>().descendingMap();
                        for (UsageStats usageStats : stats) {
                            mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                        }
                        if(mySortedMap != null && !mySortedMap.isEmpty()) {
                            String packageName;
                            Log.d("hihihi","luncher is" + mySortedMap.get(mySortedMap.firstKey()).getPackageName());
                            for(Long key:mySortedMap.keySet()){
                                packageName = mySortedMap.get(key).getPackageName();
                                Log.d("hihihi","now####"+ packageName);
                                if(packageName.equals(homeInfo.packageName)){
                                    continue;
                                }
                                if(packageName.equals("com.android.systemui")){
                                    continue;
                                }
                                if(packageName.equals(mySortedMap.get(mySortedMap.firstKey()).getPackageName())){
                                    //firstkey是自己
                                    continue;
                                }
                                PackageManager packageManager = getPackageManager();
                                Log.d("hihihi","open0 " + packageName);
                                Intent i = packageManager.getLaunchIntentForPackage(packageName);
                                if(i == null){
                                    continue;
                                }
                                i.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY|
                                        Intent.FLAG_ACTIVITY_TASK_ON_HOME|
                                        Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i,anim);
                                Log.d("hihihi","open " + packageName);
                                break;
                            }
                        }
                    }
                    break;
                }else{
                    //5.0以下
                    ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                    PackageManager pm = getApplicationContext().getPackageManager();
                    Bundle anim =
                            ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out).toBundle();

                    ActivityInfo homeInfo = new Intent(Intent.ACTION_MAIN).addCategory(
                            Intent.CATEGORY_HOME).resolveActivityInfo(pm, 0);
                    List<ActivityManager.RecentTaskInfo> recentTaskInfos = am.getRecentTasks(15, ActivityManager.RECENT_IGNORE_UNAVAILABLE);
                    for (ActivityManager.RecentTaskInfo x : recentTaskInfos.subList(1,recentTaskInfos.size())) {
                        ComponentName componentName = x.baseIntent.getComponent();
                        String classname = componentName.getClassName();
                        String packagename = componentName.getPackageName();
                        if((classname.equals(homeInfo.name) && packagename.equals(homeInfo.packageName)) ||
                                (classname.equals("com.android.internal.app.ResolverActivity")
                                        && packagename.equals("android"))  ){
                            continue;
                        }
                        if(classname.equals("com.android.systemui.recent.RecentsActivity")){
                            continue;
                        }
                        if(recentTaskInfos.get(0).baseIntent.getComponent().getClassName().equals(homeInfo.name) &&
                                recentTaskInfos.get(0).baseIntent.getComponent().getPackageName().equals(homeInfo.packageName)){
                            //如果程序是通过home键退出的...那么5秒后切换
                            Intent i = new Intent();
                            i.setClassName(packagename,classname);
                            i.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY|
                                    Intent.FLAG_ACTIVITY_TASK_ON_HOME|
                                    Intent.FLAG_ACTIVITY_NEW_TASK);
                            try{
                                if(Build.VERSION.SDK_INT>16){
                                    startActivity(i,anim);
                                }else{
                                    startActivity(i);
                                }
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                            break;
                        }
                        if(x.id>=0){
                            try{
                                if(Build.VERSION.SDK_INT>16){
                                    am.moveTaskToFront(x.id,ActivityManager.MOVE_TASK_WITH_HOME,anim );
                                }else{
                                    am.moveTaskToFront(x.id,ActivityManager.MOVE_TASK_WITH_HOME);
                                }
                            }catch(Exception e){
                            }
                        }else{
                            Intent i = new Intent();
                            i.setClassName(packagename,classname);
                            i.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY|
                                    Intent.FLAG_ACTIVITY_TASK_ON_HOME|
                                    Intent.FLAG_ACTIVITY_NEW_TASK);
                            try{
                                if(Build.VERSION.SDK_INT>16) {
                                    startActivity(i, anim);
                                }else{
                                    startActivity(i);
                                }
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                        break;
                    }
                }

                break;
            }
            case Const.Function.LOCK_SCREEN_WITHOUT_ROOT:{
                DevicePolicyManager devicePolicyManager = (DevicePolicyManager)getSystemService(Activity.DEVICE_POLICY_SERVICE);
                ComponentName componentName = new ComponentName(FxService.this, LockReceiver.class);

                if(devicePolicyManager.isAdminActive(componentName)){
                    devicePolicyManager.lockNow();
                }else{//判断是否打开设备管理器选项
                    //通过一个透明的Activity打开设备管理器
                    Intent i = new Intent(FxService.this, OpenDevicePolicyManagerActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
                break;
            }
            case Const.Function.SCREEN_SHOT:{
                if(is_button_show){
                    //如果截图前按键是显示的就隐藏再显示

                    windowManager.removeView(bb);
                    is_button_show = false;
                    root.screenshot(getApplicationContext(), new RootShellCmd.afterExec() {
                        @Override
                        public void afterDo() {
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        windowManager.addView(bb, BtnParams);
                                        is_button_show = true;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, 500);
                        }
                    });
                }else{
                    //截图前按键不显示就直接截图
                    root.screenshot(getApplicationContext(), new RootShellCmd.afterExec() {
                        @Override
                        public void afterDo() {
                        }
                    });
                }
                break;
            }
        }
    }


    private void setDefault(){
        Log.d("life_cycle","Fxservice  设置默认值" );
        isRoot = root.isRoot();

        is_autodark = spf.getBoolean("is_autodark",true);
        hide_statusbar = spf.getBoolean("hide_statusbar",true);
        BtnParams.alpha = btn_alpha;
        fixedLocal = spf.getBoolean("fixedLocal",false);
        int mode;
        int num;
        for(int i = 0; i<16 ; i++){
            num = i+1;//真实键值
            mode = spf.getInt("btn" + num + "_mode",1);
            Log.d("life_cy",mode + "读取到的mode");
            operates[i].mode = mode;
            switch (mode){
                case 0:{
                    operates[i].packageName = spf.getString("btn" + num + "_package","");
                    operates[i].activityPath = spf.getString("btn" + num + "_activity", "");
                    break;
                }
                case 1:{
                    operates[i].dowhat = spf.getInt("btn" + num + "_value", Const.Function.BACK_BY_ACCESSIBILITY);
                    operates[i].imgId = spf.getInt("btn" + num + "_img", R.drawable.ic_add_circle_outline_white_48dp);
                    operates[i].imgId2 = spf.getInt("btn" + num + "_img2", R.drawable.ic_add_circle_outline_white_48dp);
                    break;
                }
                case 2:{
                    operates[i].packageName = spf.getString("btn" + num + "_package","");
                    operates[i].activityPath = spf.getString("btn" + num + "_activity","");
                    break;
                }
            }
        }
    }//setDefault结尾

    private void setToolsDrawble(ImageButton[] btns){
        Log.d("life_cycle", "Fxservice  设置面板图标");
        Drawable drawable;
        for(int i =0;i<9;i++){
            switch (operates[i].mode){
                case 0:
                case 2:{
                    drawable = AppUtils.getAppIcon(getApplicationContext(),operates[i].packageName);
                    if(drawable != null){
                        btns[i].setImageDrawable(drawable);
                        btns[i].clearColorFilter();
//                        btns[i].setAlpha(0.9f);
                        drawable = null;
                    }
                    break;
                }
                case 1:{
                    btns[i].setImageResource(operates[i].imgId2);
                    btns[i].setColorFilter(Color.parseColor("#CFD8DC"));
                    try {
                        loadstatus(operates[i],btns[i]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case 3:{
                    btns[i].setImageResource(R.drawable.ic_add_circle_outline_white_48dp);
                    break;
                }
            }
        }
    }
    //传入一个mode为激活功能的Operate对象
    private void loadstatus(Operate op,ImageButton btn){
        Log.d("life_cycle", "Fxservice  加载面板按钮状态");
        switch(op.dowhat){
            case Const.Function.WIFI_TOGGLE:{
                WifiManager manager = (WifiManager)getSystemService(WIFI_SERVICE);
                if(manager.isWifiEnabled()){
                    btn.setColorFilter(Color.parseColor("#FF0F8170"));
                }else{
                    btn.setColorFilter(Color.parseColor("#CFD8DC"));
                }
                break;
            }
            case Const.Function.BLUETOOLS_TOGGLE:{
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                if(adapter.isEnabled()){
                    btn.setColorFilter(Color.parseColor("#FF0F8170"));
                }else{
                    btn.setColorFilter(Color.parseColor("#CFD8DC"));
                }
                break;
            }
            case Const.Function.ROTATION_TOGGLE:{
                int status = 0;
                try {
                    status = Settings.System.getInt(getApplicationContext().getContentResolver(),Settings.System.ACCELEROMETER_ROTATION);
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
                if(1 == status){
                    btn.setColorFilter(Color.parseColor("#FF0F8170"));
                }else{
                    btn.setColorFilter(Color.parseColor("#CFD8DC"));
                }
                break;
            }
            case Const.Function.IMMERSIVE_TOGGLE:{
                if(is_in_immersive_mode){
                    btn.setColorFilter(Color.parseColor("#FF0F8170"));
                }else{
                    btn.setColorFilter(Color.parseColor("#CFD8DC"));
                }
                break;
            }
            case Const.Function.KEEP_SCREEN_ON:{
                if(keep_screen_on){
                    btn.setColorFilter(Color.parseColor("#FF0F8170"));
                }else{
                    btn.setColorFilter(Color.parseColor("#CFD8DC"));
                }
                break;
            }
            case Const.Function.SCREEN_FILTER:{
                if(is_screenFilter_on){
                    btn.setColorFilter(Color.parseColor("#FF0F8170"));
                }else{
                    btn.setColorFilter(Color.parseColor("#CFD8DC"));
                }
                break;
            }
            case Const.Function.GPRS_TOGGLE:{
                boolean isopen = AppUtils.getMobileDataStatus(getApplicationContext());
                if(isopen){
                    btn.setColorFilter(Color.parseColor("#FF0F8170"));
                }else{
                    btn.setColorFilter(Color.parseColor("#CFD8DC"));
                }
                break;
            }
        }
    }

    //重新显示图标
    public class Receiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            int msg = intent.getIntExtra("MSG",0);
            switch (msg){
                case 0:{
                    break;
                }
                case Const.ServiceReciver.SHOW_BUTTON: {
                    try{
                        windowManager.addView(bb, BtnParams);
                        is_button_show = true;
                        if(is_autodark){
                            handler.postDelayed(darkRunnable,1500);
                        }
                    }catch (Exception e){
                        startService(new Intent(getApplicationContext(),FxService.class));
                        e.printStackTrace();
                    }

                    try {
                        notificationManager.cancel(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                //动态调整护目镜
                case Const.ServiceReciver.REFRESH_SCREEN_FILTER:{
                    if(screenFilter != null){
                        int alpha = intent.getIntExtra("ALPHA",15);
                        int color = intent.getIntExtra("COLOR",1);
                        selectScreenFilterColor(alpha,color);
                    }
                    break;
                }
                //动态修改皮肤样式
                case Const.ServiceReciver.REFRESH_SKIN:{

                    int size = intent.getIntExtra("SIZE",999);
                    if(size != 999){
                        size = DensityUtil.dip2px(getApplicationContext(), size);
                        BtnParams.height = size;
                        BtnParams.width = size;
                    }

                    Float alpha = intent.getFloatExtra("ALPHA",0.0f);
                    if(alpha!=0.0f){
                        BtnParams.alpha = alpha;
                        btn_alpha = alpha;
                        auto_dark_alpha_value = btn_alpha * 0.5f;
                    }

                    if(is_button_show){
                        windowManager.updateViewLayout(bb, BtnParams);
                    }

                    break;
                }
                case Const.ServiceReciver.RESTART:{
                    if(is_button_show){
                        windowManager.removeView(bb);
                        is_button_show = false;
                    }
                    try{
                        windowManager.removeView(relativeLayout);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    //ShaedPreferences中读取设置
                    setDefault();

                    //监听控件触摸事件init_setting()
                    windowManager.addView(bb, BtnParams);
                    is_button_show = true;
                    if(is_autodark){
                        handler.postDelayed(darkRunnable,1500);
                    }
                    break;
                    }
                case Const.ServiceReciver.REFRESH_IMG:{
                    if(spf.getBoolean("isImgfromSd",false)){
                        try {
                            String path = spf.getString("imgPath","");
                            bb.setImageBitmap(BitmapFactory.decodeFile(path));
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),getString(R.string.img_wrong),Toast.LENGTH_SHORT).show();
                            bb.setImageResource(R.drawable.skin26);
                        }
                    }else{
                        int resId = spf.getInt("imgResId", R.drawable.skin26);
                        bb.setImageResource(resId);
                    }
                    if(is_button_show){
                        windowManager.updateViewLayout(bb, BtnParams);
                    }
                    break;
                }
            }
        }
    }

    //第一次打开护目镜
    private void selectScreenFilterColor(){
        int color = spf.getInt("screen_filter_color", 1);
        int alpha = spf.getInt("screen_filter_display", 15);
        screenFilter.setBackgroundColor(AppUtils.getColors(alpha, color));
        if(is_screenFilter_on){
            windowManager.updateViewLayout(screenFilter, ScreenFilterParams);
        }
    }

    //调整护目镜 重载
    private void selectScreenFilterColor(int alpha,int color){
        screenFilter.setBackgroundColor(AppUtils.getColors(alpha, color));
        if(is_screenFilter_on){
            windowManager.updateViewLayout(screenFilter, ScreenFilterParams);
        }
    }

    private class RotateReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if ( intent.getAction().equals(Intent.ACTION_CONFIGURATION_CHANGED) ) {

                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                    // it's Landscape
                    is_in_land = true;

                    BtnParams.x = last_x_l;
                    BtnParams.y = last_y_l;

                    if(is_button_show){
                        windowManager.updateViewLayout(bb, BtnParams);
                    }

                }
                else {
                    Log.d("hihihi","Port");
                    is_in_land = false;

                    BtnParams.x = last_x;
                    BtnParams.y = last_y;

                    if(is_button_show){
                        windowManager.updateViewLayout(bb, BtnParams);
                    }


                }
            }
        }


    }















}