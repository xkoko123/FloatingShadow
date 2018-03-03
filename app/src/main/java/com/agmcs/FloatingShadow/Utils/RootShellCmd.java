package com.agmcs.FloatingShadow.Utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 单例类
 * Created by agmcs on 2015/2/10.
 */
public class RootShellCmd {
    private static RootShellCmd rootShellCmd = null;
    private  DataOutputStream os = null;
    private  BufferedReader reader = null;
    private  Process process = null;
    private String LaunchPackageName = null;
    private boolean root;


    public static synchronized RootShellCmd getInstance(Context context) {
        if (rootShellCmd == null) {
            rootShellCmd = new RootShellCmd(context);
        }
        return rootShellCmd;
    }

    public RootShellCmd(Context context) {
        LaunchPackageName = getLaunchInfo(context);
        root = isRoot();
    }

    /**
     * 判断当前是否获得root权限
     */
    public final boolean isRoot(){
        try
        {
            process  = Runtime.getRuntime().exec("su");
            process.getOutputStream().write("exit\n".getBytes());
            process.getOutputStream().flush();
            int i = process.waitFor();
            if(0 == i){
                process = Runtime.getRuntime().exec("su");
                return true;
            }
        } catch (Exception e)
        {
            return false;
        }
        return false;
    }

    /**
     * 根权限执行Shell命令
     */
    public final void exec(final String cmd) {
        if(!root){
            root = isRoot();
            return;
        }

        try {
            if(process == null){
                process = Runtime.getRuntime().exec("su");
            }
            if (os == null) {
                os = new DataOutputStream(process.getOutputStream());
            }
            if(reader == null){
                reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        os.writeBytes(cmd + "\n");
                        os.flush();

//                        process.waitfor();
//                        os.writeBytes("exit\n");
//                        os.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 后台模拟按键
     */
    public final void simulateKey(int keyCode) {
        if(!root){
            root = isRoot();
            return;
        }
        exec("input keyevent " + keyCode + "\n");
    }

    /**
     * 获取栈顶程序包名
     */
    public final String getTopActivityName(){
        exec("dumpsys activity | grep top-activit |awk '{print $8}'|awk '{print $2}' -F '(:|/)'");
        String line = "";
        try {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("hihi123", "line:" + line);
        return line;
    }

    /**
    * 休眠进程
    */
    public final void killApp(String packageName){
        if(!packageName.equals("com.agmcs.FloatingShadow") &&
                !packageName.equals(LaunchPackageName)){
            exec("am force-stop " + packageName);
        }
    }

    /**
     * 休眠当前进程
     */
    public final void killTopActivity(){
        if(!root){
            root = isRoot();
            return;
        }
        killApp(getTopActivityName());
    }

    /**
     * 执行完命令后执行的回调
     */
    public interface afterExec {
        public void afterDo();
    }

    public final void switchLastApp(Context context){
        if(!root){
            root = isRoot();
            return;
        }
        for(int i = 1; i<4;i++){

            //获得Task ID
            exec("dumpsys activity recents |grep 'Recent #"+
                    i+
                    ":'|awk -F '(#| )' '{{print $9}}'");
            String taskID = "";
            try {
                taskID = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

//            获得PackageName className
            exec("dumpsys activity recents|grep -A 9 '#"+
                    taskID+
                    "'|grep 'realActivity' |awk -F = '{{print $2}}'");
            String packageName = "";
            String className = "";
            try {
                String[] result = reader.readLine().split("/");
                packageName = result[0];
                className = result[1];
                if(className.startsWith(".")){
                    className = packageName+className;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(className.equals("com.android.systemui.recents.RecentsActivity")||
                    packageName.equals(LaunchPackageName)){
                continue;
            }

            exec("dumpsys activity recents|grep -A 9 '#"+
                    taskID+
                    "'|grep 'Activities'");
            String x = "";
            try {
                x = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (x.indexOf("[ActivityRecord") != -1){
                //如果程序是正在运行的 从后台把程序拉出来
                Log.d("hihi123",x +"正在运行");
            try{
                Bundle anim =
                        ActivityOptionsCompat.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
                if(Build.VERSION.SDK_INT>16){
                    am.moveTaskToFront(Integer.parseInt(taskID),ActivityManager.MOVE_TASK_WITH_HOME,anim );
                }else{
                    am.moveTaskToFront(Integer.parseInt(taskID),ActivityManager.MOVE_TASK_WITH_HOME);
                }
            }catch(Exception e){
            }
        }else{
                //程序不是正在运行的,就新打开程序
                Log.d("hihi123",x + "不在运行");
                Intent intent = new Intent();
                intent.setClassName(packageName,className);
                intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY |
                        Intent.FLAG_ACTIVITY_TASK_ON_HOME |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle anim =
                        ActivityOptionsCompat.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                try{
                    if(Build.VERSION.SDK_INT>16) {
                        context.startActivity(intent,anim);
                    }else{
                        context.startActivity(intent);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }


            Log.d("hihi123",className + "-====" + packageName+"----" + taskID);
            //启动
            break;
        }


    }

    /**
     * 截图,回调函数应实现重新显示button的功能
     */
    public final void screenshot(Context context, final afterExec afterExec){//截图
        if(!root){
            root = isRoot();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(500);
                }catch (Exception e){
                    e.printStackTrace();
                }

                Date now = new Date();
                String path = Environment.getExternalStorageDirectory().getPath() + "/screenshot/";
                File file = new File(path);
                if(!file.exists()){
                    file.mkdirs();
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
                exec("/system/bin/screencap -p " + path + sdf.format(now) + ".png");
                afterExec.afterDo();
            }
        }).start();
    }


    /**
     * 获取Launch的PackageName
     */
    private static String getLaunchInfo(Context context){
        PackageManager pm = context.getPackageManager();
        ActivityInfo homeInfo = new Intent(Intent.ACTION_MAIN).addCategory(
                Intent.CATEGORY_HOME).resolveActivityInfo(pm, 0);
        return homeInfo.packageName;
    }

    /**
     * root后 读取 qemu.hw.mainkeys 的设置信息,1为隐藏虚拟键盘, 0为显示
     */
    public boolean isMainKeysHide(){
        if(!root){
            root = isRoot();
            return false;
        }

        exec("getprop qemu.hw.mainkeys");
        String status = "";
        try {
            status = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(status == null){
            return false;
        }
        return status.equals("1")?true:false;
    }


    /**
     * root后 设置 qemu.hw.mainkeys信息,true为隐藏虚拟键盘, false为显示
     */
    public void setMainKeysHide(boolean status){
        if(!root){
            root = isRoot();
            return;
        }

        exec("setprop qemu.hw.mainkeys " + (status?1:0)+" && stop && start");
//        exec("killall system_server");

    }



}
