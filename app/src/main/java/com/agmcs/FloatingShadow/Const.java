package com.agmcs.FloatingShadow;

/**
 * Created by agmcs on 2015/2/10.
 */
public class Const {

    public static class Function{
        public static final int BACK = 100;
        public static final int HOME = 101;
        public static final int MENU = 102;
        public static final int HIDE = 103;//隐藏工具面板
        public static final int LOCK = 104;//锁屏
        public static final int APP_SWITCH = 105;//多任务
        public static final int SHOW = 106;//显示工具面板
        public static final int SLEEP = 107;//休眠程序
        public static final int BACK_BY_ACCESSIBILITY = 108;
        public static final int HOME_BY_ACCESSIBILITY = 109;
        public static final int NOTIFICATIONS_BY_ACCESSIBILITY = 110;
        public static final int RECENT_APP_BY_ACCESSIBILITY = 111;
        public static final int WIFI_TOGGLE = 112;
        public static final int BLUETOOLS_TOGGLE = 113;
        public static final int ROTATION_TOGGLE = 114;
        public static final int GPRS_TOGGLE = 115;
        public static final int IMMERSIVE_TOGGLE = 116;
        public static final int KEEP_SCREEN_ON = 117;
        public static final int TORCH = 118;
        public static final int SCREEN_FILTER = 119;
        public static final int AIRPLANE_MODE = 120; //todo
        public static final int LAST_APP_SWITCH = 121;
        public static final int LOCK_SCREEN_WITHOUT_ROOT = 122;//锁屏
        public static final int SCREEN_SHOT = 123;
    }
    public static class Action{
        public static final int UP = 10;
        public static final int DOWN = 11;
        public static final int RIGHT = 12;
        public static final int LEFT = 13;
        public static final int LONG_PRESS = 14;
        public static final int CLICK = 15;
        public static final int DOUBLIE_CLICK = 16;
    }

    public static class ServiceNeed{
        public static final int AUTO_GET_DARK = 0;
    }
    public static class ServiceReciver{
        public static final int SHOW_BUTTON = 1;
        public static final int REFRESH_SCREEN_FILTER = 2;
        public static final int ACCESSIBILITY_BUTTON = 3;
        public static final int IMMERSIVE_MODE = 4;
        public static final int REFRESH_SKIN = 5;
        public static final int RESTART = 6;
        public static final int REFRESH_IMG = 7;
    }

    public static class Gestrue{
        public static final int LEFT = 1;
        public static final int RIGHT = 1;
        public static final int BOTTOM = 1;
    }


}
