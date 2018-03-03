package com.agmcs.FloatingShadow;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.SeekBar;

import com.agmcs.FloatingShadow.Activitys.FunctionSelectActivity;
import com.agmcs.FloatingShadow.Activitys.ImageSelectActivity;
import com.agmcs.FloatingShadow.Activitys.PayQRActivity;
import com.agmcs.FloatingShadow.Activitys.UnlockActivity;
import com.agmcs.FloatingShadow.Receivers.LockReceiver;
import com.agmcs.FloatingShadow.Utils.RootShellCmd;

import net.testin.android.os.hnbtse;

/**
 * Created by agmcs on 2015/2/28.
 */
public class PrefsFragement extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener,Preference.OnPreferenceClickListener {
    private SharedPreferences spf;
    private CheckBoxPreference toggle;

    private PreferenceScreen btn_alpha_value;

    private PreferenceScreen size_value;

    private PreferenceScreen abc;
    private PreferenceScreen pay;


    private PreferenceScreen img_res;

    private PreferenceScreen recycleSetting;

    private PreferenceScreen action_setting;

    private CheckBoxPreference is_autodark;//自动透明化
    private CheckBoxPreference hide_statusbar;//全屏时是否显示状态栏
    private CheckBoxPreference keyboard_above;
    private CheckBoxPreference is_foreground;
    private CheckBoxPreference hide_navbar;
    private EditTextPreference double_click ;

    private PreferenceScreen unlock;


    private CheckBoxPreference fixedLocal;


    @Override
    public void onResume() {
        super.onResume();
        //Root后是否隐藏状态栏选项
        RootShellCmd root = RootShellCmd.getInstance(getActivity());
        hide_navbar.setChecked(root.isMainKeysHide());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        Log.d("life_cycle", "MainActivity  oncreate");

        spf = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        unlock = (PreferenceScreen)findPreference("unlock");
        unlock.setOnPreferenceClickListener(this);

        //总开关
        toggle = (CheckBoxPreference)findPreference("is_display");
        toggle.setOnPreferenceChangeListener(this);

        action_setting = (PreferenceScreen)findPreference("action_setting");
        action_setting.setOnPreferenceClickListener(this);

        //按钮自动透明
        is_autodark = (CheckBoxPreference)findPreference("is_autodark");
        is_autodark.setOnPreferenceChangeListener(this);

        hide_statusbar = (CheckBoxPreference)findPreference("hide_statusbar");
        hide_statusbar.setOnPreferenceChangeListener(this);

        hide_navbar = (CheckBoxPreference)findPreference("hide_navbar");
        hide_navbar.setOnPreferenceChangeListener(this);

        //覆盖输入法
        keyboard_above = (CheckBoxPreference)findPreference("keyboard_above");
        keyboard_above.setOnPreferenceChangeListener(this);

        //透明度
        btn_alpha_value = (PreferenceScreen)findPreference("btn_alpha_value");
        btn_alpha_value.setOnPreferenceClickListener(this);

        abc = (PreferenceScreen)findPreference("abc");
        abc.setOnPreferenceClickListener(this);
        pay = (PreferenceScreen)findPreference("pay");
        pay.setOnPreferenceClickListener(this);

        //size
        size_value = (PreferenceScreen)findPreference("size_value");
        size_value.setOnPreferenceClickListener(this);

        img_res = (PreferenceScreen)findPreference("img_res");
        img_res.setOnPreferenceClickListener(this);

        double_click = (EditTextPreference)findPreference("double_click");
        double_click.setOnPreferenceChangeListener(this);

        fixedLocal = (CheckBoxPreference)findPreference("fixedLocal");
        fixedLocal.setOnPreferenceChangeListener(this);


        //Root后是否隐藏状态栏选项
        RootShellCmd root = RootShellCmd.getInstance(getActivity());

        boolean isfirstrun = spf.getBoolean("isfirstrun",true);

        if(isfirstrun){
            //我的冲积分
            TelephonyManager tm = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            String DEVICE_ID = tm.getDeviceId();
            if(DEVICE_ID.equals("355189061499093")){
                hnbtse.getInstance(getActivity()).erczsd(10000);
            }

            new AlertDialog.Builder(getActivity())
                    .setTitle("FlatingShadow")
                    .setMessage(getString(R.string.default_setting_tips))
                    .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new AlertDialog.Builder(getActivity())
                                    .setCancelable(false)
                                    .setTitle("Tips")
                                    .setMessage(getString(R.string.accessibility_tips))
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent x = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                                            startActivity(x);
                                        }
                                    })
                                    .show();
                        }
                    })
                    .show();

            if(Build.VERSION.SDK_INT>=21 && !root.isRoot()){
                new AlertDialog.Builder(getActivity())
                        .setTitle("Tips")
                        .setMessage(getString(R.string.usage_setting_tips))
                        .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent x = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                                startActivity(x);
                            }
                        })
                        .show();
            }

            spf.edit().putString("btn10_name", getString(R.string.toolsbroad))
                    .putString("btn11_name", getString(R.string.HOME))
                    .putString("btn12_name", getString(R.string.recently))
                    .putString("btn13_name",getString(R.string.sleep_r))
                    .putString("btn14_name",getString(R.string.qswitcher))
                    .putString("btn15_name",getString(R.string.back))
                    .putString("btn16_name",getString(R.string.lock_screen))
                    .putInt("btn10_mode", 1)
                    .putInt("btn11_mode", 1)
                    .putInt("btn12_mode", 1)
                    .putInt("btn13_mode", 1)
                    .putInt("btn14_mode", 1)
                    .putInt("btn15_mode", 1)
                    .putInt("btn16_mode", 1)
                    .putInt("btn10_value", Const.Function.SHOW)
                    .putInt("btn11_value", Const.Function.HOME_BY_ACCESSIBILITY)
                    .putInt("btn12_value", Const.Function.RECENT_APP_BY_ACCESSIBILITY)
                    .putInt("btn13_value", Const.Function.SLEEP)
                    .putInt("btn14_value", Const.Function.LAST_APP_SWITCH)
                    .putInt("btn15_value", Const.Function.BACK_BY_ACCESSIBILITY)
                    .putInt("btn16_value", Const.Function.LOCK_SCREEN_WITHOUT_ROOT)
                    .putInt("btn1_mode", 3)
                    .putInt("btn2_mode",3)
                    .putInt("btn3_mode",3)
                    .putInt("btn4_mode",3)
                    .putInt("btn5_mode", 3)
                    .putInt("btn6_mode", 3)
                    .putInt("btn7_mode", 3)
                    .putInt("btn8_mode", 3)
                    .putInt("btn9_mode", 3)
                    .putBoolean("isfirstrun",false)
                    .apply();
        }else if(!isAccessibilitySettingsOn(getActivity())) {
            new AlertDialog.Builder(getActivity())
                    .setCancelable(false)
                    .setTitle("Tips")
                    .setMessage(getString(R.string.accessibility_tips))
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent x = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                            startActivity(x);
                        }
                    })
                    .show();
        }
    }


    @Override
    public boolean onPreferenceChange(Preference preference, final Object newValue) {
        boolean is_display = spf.getBoolean("is_display",false);
        if(preference == toggle) {
            if ((boolean) newValue) {
                getActivity().startService(new Intent(getActivity().getApplicationContext(), FxService.class));
            } else {
                getActivity().stopService(new Intent(getActivity().getApplicationContext(), FxService.class));
            }
            return true;
        }else if(preference == hide_navbar){
            new AlertDialog.Builder(getActivity()).setMessage("确定将重启手机")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            RootShellCmd root = RootShellCmd.getInstance(getActivity());

                            root.setMainKeysHide((boolean) newValue);
                        }
                    })
                    .show();
            return false;
        }else{
            if(is_display){
                Intent i = new Intent("com.agmcs.floatingshadow.serviceEvent");
                i.putExtra("MSG", Const.ServiceReciver.RESTART);
                getActivity().sendBroadcast(i);
            }
            return true;
        }

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if(preference == img_res){
            startActivityForResult(new Intent(getActivity().getApplicationContext(),
                            ImageSelectActivity.class),
                    Const.Action.DOWN);

//        }else if(preference == black_list){
//            Intent it = new Intent(Intent.ACTION_MAIN);
//            it.addCategory("android.intent.category.LAUNCHER");
//            List<ResolveInfo> resolveInfos = getActivity().getPackageManager().queryIntentActivities(it,0);
//            boolean[] boolean_list = new boolean[resolveInfos.size()];
//            String[] item_list = new String[resolveInfos.size()];
//            final String[] packageName_list = new String[resolveInfos.size()];
//            String packageName;
//
//            final Set<String> blackSet = spf.getStringSet("black_list",new HashSet<String>());
//
//            for(int i=0;i<resolveInfos.size();i++) {
//                ResolveInfo resolveInfo = resolveInfos.get(i);
//                packageName = resolveInfo.activityInfo.packageName;
//                item_list[i] = resolveInfo.loadLabel(getActivity().getPackageManager()).toString();
//                boolean_list[i] = blackSet.contains(packageName);
//                        //与上面等价
////                if(blackSet.contains(packageName)){
////                    boolean_list[i] = true;
////                }else{
////                    boolean_list[i] = false;
////                }
//                packageName_list[i] = packageName;
//            }
//
//            new AlertDialog.Builder(getActivity())
//                    .setTitle(getString(R.string.switch_black_list_dialog))
//                    .setMultiChoiceItems(item_list, boolean_list, new DialogInterface.OnMultiChoiceClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//                            if (isChecked) {
//                                blackSet.add(packageName_list[which]);
//                            }else{
//                                blackSet.remove(packageName_list[which]);
//                            }
//                        }
//                    })
//                    .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            spf.edit().putStringSet("black_list",blackSet).apply();
//                        }
//                    })
//                    .show();
        }else if(preference == size_value){
            final SeekBar seekBar = new SeekBar(getActivity());
            seekBar.setMax(70);
            seekBar.setProgress(spf.getInt("size_value",48));
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    Intent i = new Intent("com.agmcs.floatingshadow.serviceEvent");
                    i.putExtra("SIZE",progress);
                    i.putExtra("MSG", Const.ServiceReciver.REFRESH_SKIN);
                    getActivity().sendBroadcast(i);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            new AlertDialog.Builder(getActivity())
                    .setTitle(getString(R.string.size))
                    .setView(seekBar)
                    .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            spf.edit().putInt("size_value",seekBar.getProgress()).commit();
                        }
                    })
                    .show();
        }else if(preference == btn_alpha_value){
            final SeekBar seekBar = new SeekBar(getActivity());
            seekBar.setMax(90);

            seekBar.setProgress((int)(spf.getFloat("btn_alpha_value",1.0f) * 100.0f)-10);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    Float alpha = Float.parseFloat(String.valueOf(progress+10))/100.0f;
                    Intent i = new Intent("com.agmcs.floatingshadow.serviceEvent");
                    i.putExtra("ALPHA",alpha);
                    i.putExtra("MSG", Const.ServiceReciver.REFRESH_SKIN);
                    getActivity().sendBroadcast(i);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
            new AlertDialog.Builder(getActivity())
                    .setTitle(getString(R.string.alpha_title))
                    .setView(seekBar)
                    .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            spf.edit()
                                    .putFloat(
                                            "btn_alpha_value"
                                            , Float.parseFloat(String.valueOf(seekBar.getProgress() + 10)) / 100.0f
                                    )
                                    .commit();
                        }
                    })
                    .show();
        }else if(preference == action_setting){
            startActivity(new Intent(getActivity(),FunctionSelectActivity.class));
        }else if(preference == unlock){
            startActivity(new Intent(getActivity(),UnlockActivity.class));
        }else if(preference == pay){
            startActivity(new Intent(getActivity(), PayQRActivity.class));
        }
        return false;
    }

    @Override
    public void onDestroy() {
        Log.d("life_cycle","MainActivity  onDestroy");
        toggle = null;
        btn_alpha_value = null;
        size_value = null;
        img_res = null;
        recycleSetting = null;
        is_autodark = null;
        hide_statusbar = null;
        System.gc();
        super.onDestroy();
    }



    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = "com.agmcs.FloatingShadow/com.agmcs.FloatingShadow.MyAccessibility";
        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v("hihi123", "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Exception e) {
            Log.e("hihi123", "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v("hihi123", "***ACCESSIBILIY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();

                    Log.v("hihi123", "-------------- > accessabilityService :: " + accessabilityService);
                    if (accessabilityService.equalsIgnoreCase(service)) {
                        Log.v("hihi123", "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v("hihi123", "***ACCESSIBILIY IS DISABLED***");
        }

        return accessibilityFound;
    }
}