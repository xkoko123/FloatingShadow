package com.agmcs.biubiu;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MainActivity extends AppCompatActivity {
    private Button btn_start, btn_close;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar)findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        setTitle("biubiubiu...");
        startService(new Intent(MainActivity.this, NotificationListener.class));
        getFragmentManager().beginTransaction().replace(R.id.container,new SettingFrag()).commit();
    }



    public static class SettingFrag extends PreferenceFragment {
        SharedPreferences spf;
        Set<String> stringSet;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

            switch (preference.getKey()){
                case "toggle":
//                    Boolean x = spf.getBoolean("toggle", false);
                    Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                    startActivity(intent);
                    break;
                case "xiaomi":
                    new AlertDialog.Builder(getActivity()).setTitle("小米:").setMessage("1. 按Home键（房子那个建）返回桌面\n\n2. 按住Home键，出现程序列表" +
                            "\n\n3. 长按BiuBiu\n\n4. 进入应用信息，选择显示悬浮窗，点击确认").show();
                    break;
                case "notifi_list":
                    List<Map<String,Object>> list_map = new ArrayList<Map<String,Object>>();


                    Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
                    mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                    List<ResolveInfo> resolveInfos = getActivity().getPackageManager().queryIntentActivities(mainIntent, 0);
                    for(ResolveInfo resolveInfo :resolveInfos){
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("name", resolveInfo.loadLabel(getActivity().getPackageManager()).toString());
                        map.put("package", resolveInfo.activityInfo.packageName);
                        list_map.add(map);
                    }

                    stringSet = spf.getStringSet("white_list",new HashSet<String>());

                    String[] title_list = new String[list_map.size()];
                    final String[] package_list = new String[list_map.size()];
                    boolean[] check_list = new boolean[list_map.size()];
                    int i=0;
                    String package_name;
                    for(Map<String,Object> each:list_map){
                        title_list[i] = (String)each.get("name");

                        package_name = (String)each.get("package");
                        package_list[i] =package_name;
                        if(stringSet.contains(package_name)){
                            check_list[i] = true;
                        }
                        i++;
                    }

                    new AlertDialog.Builder(getActivity())
                            .setTitle("弹幕应用列表")
                            .setMultiChoiceItems(title_list, check_list, new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                    if (b == true) {
                                        if (stringSet != null) {
                                            stringSet.add(package_list[i]);
                                        }
                                    } else {
                                        if (stringSet != null) {
                                            stringSet.remove(package_list[i]);
                                        }
                                    }
                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    spf.edit().putStringSet("white_list", stringSet).commit();
                                    getActivity().startService(new Intent(getActivity(),NotificationListener.class));
                                }
                            })
                            .show();
                    break;
            }
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

    }


}
