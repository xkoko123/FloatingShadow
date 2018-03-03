package com.agmcs.FloatingShadow;

import android.graphics.drawable.Drawable;

/**
 * Created by agmcs on 2015/2/11.
 */
public class AppInfo {
    public String appName="";
    public int dowhat = 0;
    public String packageName="";
    public String activityPath = "";
    public Drawable appIcon=null;
    public int imgId;
    public int imgId2;

    public AppInfo() {
    }
    public AppInfo(String appName, int dowhat) {
        this.appName = appName;
        this.dowhat = dowhat;
    }

    public AppInfo(String appName, int dowhat, int imgId, int ImgId2) {
        this.appName = appName;
        this.dowhat = dowhat;
        this.imgId = imgId;
        this.imgId2 = ImgId2;
    }
}
