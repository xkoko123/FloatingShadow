package com.agmcs.FloatingShadow.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;

import java.lang.reflect.Method;

/**
 * Created by agmcs on 2015/2/12.
 */
public class AppUtils {
    private static AppUtils appUtils;


    //通过包名获得图片
    public static Drawable getAppIcon(Context context, String packname){
        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo info = pm.getApplicationInfo(packname, 0);
            return info.loadIcon(pm);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }


    //改变移动流量状态
    public static void toggleMobileData(Context context, boolean enabled) {
        try {
            final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final Class<?> conmanClass = Class.forName(conman.getClass().getName());
            final Method setMobileDataEnabledMethod = conmanClass.getMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);
            setMobileDataEnabledMethod.invoke(conman, Boolean.valueOf(enabled));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //获取移动数据开关状态
    public static boolean getMobileDataStatus(Context context)
    {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        Class cmClass = connManager.getClass();
        Class[] argClasses = null;
        Object[] argObject = null;
        Boolean isOpen = false;
        try
        {

            Method method = cmClass.getMethod("getMobileDataEnabled", argClasses);

            isOpen = (Boolean)method.invoke(connManager, argObject);
        }catch(Exception e)
        {
            e.printStackTrace();
        }

        return isOpen;
    }


    //选择颜色
    public static int getColors(int alpha, int selectedMode){
        float f1 = 10.0F;
        float f2 = 60.0F;
        float f3 = 180.0F;
        float f4;
        float f5;
        float f6;
        switch (selectedMode){
            case 1:{//自然
                f4 = 200.0F;
                f5 = f3;
                f6 = f1;
                break;
            }
//            case 2:{//黄色
//                f6 = 90.0F;
//                f4 = f3;
//                f5 = f3;
//                f1 = 90.0F;
//                f2 = 0.0F;
//                break;
//            }
            case 2:{//棕色
                f6 = 90.0F;
                f4 = f3;
                f5 = f3;
                f3 = 120.0F;
                f1 = f2;
                break;
            }
            case 3:{//红色
                f6 = f2;
                f4 = 120.0F;
                f5 = f3;
                f2 = 0.0F;
                f1 = 0.0F;
                f3 = 0.0F;
                break;
            }
            case 4:{//黑色
                f4 = 50.0F;
                f5 = 200.0F;
                f3 = 50.0F;
                f2 = 0.0F;
                f1 = 0.0F;
                f6 = 0.0F;
                break;
            }
            default:{
                f4 = 200.0F;
                f5 = f3;
                f6 = f1;
                if (alpha < 0)
                    alpha = 0;
                break;
            }
        }
        return Color.argb(
                (int) (alpha / 80.0D * (f5 - 0.0F) + 0.0F),
                (int) (alpha / 80.0D * (f6 - f4) + f4),
                (int) (alpha / 80.0D * (f1 - f3) + f3),
                (int) (alpha / 80.0D * (0.0F - f2) + f2)
        );
    }


}
