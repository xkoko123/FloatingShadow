package com.agmcs.FloatingShadow.Utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by agmcs on 2015/2/12.
 */
public class DensityUtil {
    /**
     * 从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dpValue,context.getResources().getDisplayMetrics());
    }

    /**
     * 从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,pxValue,context.getResources().getDisplayMetrics());
    }
}
