package com.agmcs.FloatingShadow.Activitys;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.agmcs.FloatingShadow.AppInfo;
import com.agmcs.FloatingShadow.Const;
import com.agmcs.FloatingShadow.FragAppList;
import com.agmcs.FloatingShadow.FragFunctionList;
import com.agmcs.FloatingShadow.FragQuickList;
import com.agmcs.FloatingShadow.R;
import com.agmcs.FloatingShadow.adapter.FragAdapter;

import java.util.ArrayList;
import java.util.List;


public class AppSeclectActivity extends FragmentActivity {
    private ViewPager vp;
    private TextView textView;
//    private String[] titleList = new String[]{
//            getString(R.string.app_select_app)
//            ,getString(R.string.app_select_function)
//            ,getString(R.string.app_select_shot)
//    };
private String[] titleList = new String[]{
        "应用程序"
        ,"功能"
        ,"快捷方式"
};
    private ProgressDialog dialog;
    public static List<AppInfo> quickList;
    public static List<AppInfo> appList;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:{
                    quickList = (List<AppInfo>)msg.obj;
                    break;
                }
                case 1:{
                    appList = (List<AppInfo>)msg.obj;
                    break;
                }
            }
            if(quickList != null && appList!=null){
                dialog.dismiss();
                List<Fragment> fragments = new ArrayList<Fragment>();
                fragments.add(new FragAppList());
                fragments.add(new FragFunctionList());
                fragments.add(new FragQuickList());
                FragAdapter adapter = new FragAdapter(getSupportFragmentManager(),fragments,titleList);
                vp.setAdapter(adapter);
                vp.setCurrentItem(1);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("life_cycle", "AppSelectedActivity  oncreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_seclect);

        vp = (ViewPager)findViewById(R.id.vp);
        textView = (TextView)findViewById(R.id.select_title);
        int btn = getIntent().getIntExtra("BTN",999);
        Toast.makeText(AppSeclectActivity.this
                ,getString(R.string.current) + PreferenceManager.getDefaultSharedPreferences(AppSeclectActivity.this)
                .getString("btn" + btn + "_name","")
                ,Toast.LENGTH_LONG
        ).show();
        switch (btn){
            case 1:{
                textView.setText(getString(R.string.btn1));
                break;
            }
            case 2:{
                textView.setText(getString(R.string.btn2));
                break;
            }
            case 3:{
                textView.setText(getString(R.string.btn3));
                break;
            }
            case 4:{
                textView.setText(getString(R.string.btn4));
                break;
            }
            case 5:{
                textView.setText(getString(R.string.btn5));
                break;
            }
            case 6:{
                textView.setText(getString(R.string.btn6));
                break;
            }
            case 7:{
                textView.setText(getString(R.string.btn7));
                break;
            }
            case 8:{
                textView.setText(getString(R.string.btn8));
                break;
            }
            case 9:{
                textView.setText(getString(R.string.btn9));
                break;
            }
            case Const.Action.CLICK:{
                textView.setText(getString(R.string.click));
                break;
            }
            case Const.Action.RIGHT:{
                textView.setText(getString(R.string.right));
                break;
            }
            case Const.Action.LEFT:{
                textView.setText(getString(R.string.left));
                break;
            }
            case Const.Action.DOWN:{
                textView.setText(getString(R.string.down));
                break;
            }
            case Const.Action.UP:{
                textView.setText(getString(R.string.up));
                break;
            }
            case Const.Action.LONG_PRESS:{
                textView.setText(getString(R.string.long_press));
                break;
            }
            case Const.Action.DOUBLIE_CLICK:{
                textView.setText(getString(R.string.double_click));
                break;
            }
        }

        //防止阻塞ui线程,dialog显示不出来;
        dialog = new ProgressDialog(AppSeclectActivity.this);
        dialog.setTitle(getString(R.string.load_app));
        dialog.setCancelable(false);
        dialog.show();
        initAppList();
        initQuickList();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("life_cycle","AppSelectedActivity  Destroy");
        Intent i = new Intent("com.agmcs.floatingshadow.serviceEvent");
        i.putExtra("MSG", Const.ServiceReciver.RESTART);
        sendBroadcast(i);
    }

    //读取快捷方式列表
    private void initQuickList(){
        //用来存储获取的应用信息数据

        new Thread(new Runnable(){
            @Override
            public void run() {
                //shortcut
                List<AppInfo> list = new ArrayList<AppInfo>();
                Intent it = new Intent(Intent.ACTION_CREATE_SHORTCUT);
                List<ResolveInfo> resolveInfos = getPackageManager().queryIntentActivities(it,0);
                for(int i=0;i<resolveInfos.size();i++) {
                    ResolveInfo resolveInfo = resolveInfos.get(i);
                    AppInfo tmpInfo = new AppInfo();
                    tmpInfo.appName = resolveInfo.loadLabel(getPackageManager()).toString();
                    tmpInfo.packageName = resolveInfo.activityInfo.packageName;
                    tmpInfo.activityPath = resolveInfo.activityInfo.name;
                    tmpInfo.appIcon = resolveInfo.loadIcon(getPackageManager());
                    list.add(tmpInfo);
                }

                Message msg = handler.obtainMessage();
                msg.what = 0;
                msg.obj = list;
                handler.sendMessage(msg);
            }
        }).start();
    }

    //读取应用列表
    private void initAppList(){

        //用来存储获取的应用信息数据
        //防止阻塞ui线程,dialog显示不出来;
        new Thread(new Runnable(){
            @Override
            public void run() {

                Intent it = new Intent(Intent.ACTION_MAIN);
                it.addCategory("android.intent.category.LAUNCHER");
                List<ResolveInfo> resolveInfos = getPackageManager().queryIntentActivities(it,0);
                List<AppInfo> appList = new ArrayList<AppInfo>();
                List<AppInfo> systemlist = new ArrayList<AppInfo>();//系统程序列表,系统程序排在后面
                for(int i=0;i<resolveInfos.size();i++) {
                    ResolveInfo resolveInfo = resolveInfos.get(i);
                    AppInfo tmpInfo = new AppInfo();
                    tmpInfo.appName = resolveInfo.loadLabel(getPackageManager()).toString();
                    tmpInfo.packageName = resolveInfo.activityInfo.packageName;
                    Log.d("hihihi",tmpInfo.packageName);
                    tmpInfo.activityPath = resolveInfo.activityInfo.name;
                    tmpInfo.appIcon = resolveInfo.loadIcon(getPackageManager());
                    if((resolveInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM)==0)
                    {
                        appList.add(tmpInfo);
                    }else{
                        systemlist.add(tmpInfo);
                    }
                }
                appList.addAll(systemlist);
                systemlist = null;
                Message msg = handler.obtainMessage();
                msg.what = 1;
                msg.obj = appList;
                handler.sendMessage(msg);
            }
        }).start();
    }



}
