package com.agmcs.FloatingShadow;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.agmcs.FloatingShadow.Activitys.UnlockActivity;
import com.agmcs.FloatingShadow.Receivers.LockReceiver;
import com.agmcs.FloatingShadow.adapter.AppListAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class FragFunctionList extends Fragment {
    private ListView lv;
    private AppListAdapter adapter;
    private List<AppInfo> functionList = null;
    private int btn_code =999;


    public FragFunctionList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_frag_function_list, container, false);
        btn_code = getActivity().getIntent().getIntExtra("BTN",999);
        if(btn_code == 999){
            getActivity().setResult(Activity.RESULT_CANCELED);
            getActivity().finish();
        }

        lv = (ListView)view.findViewById(R.id.function_select);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppInfo app = functionList.get(position);
                if(app.dowhat == Const.Function.SCREEN_FILTER){
                    //如果没解锁
                    if(!PreferenceManager
                            .getDefaultSharedPreferences(
                                    getActivity()
                                            .getApplicationContext()
                            ).getBoolean("is_screenfilter_unlock",false)
                            ){
                        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                                .setTitle("Error...")
                                .setMessage("还没解锁")
                                .setPositiveButton("去解锁?",new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        getActivity().startActivity(new Intent(getActivity(),UnlockActivity.class));
                                    }
                                })
                                .setNegativeButton(getString(R.string.unlock_cancel),new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        getActivity().finish();
                                    }
                                })
                                .show();
                    }else{
                        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                                .putInt("btn" + btn_code + "_mode", 1)
                                .putInt("btn" + btn_code + "_value", app.dowhat)
                                .putInt("btn" + btn_code + "_img", app.imgId)
                                .putInt("btn" + btn_code + "_img2", app.imgId2)
                                .putString("btn" + btn_code + "_name", app.appName)
                                .commit();
                        getActivity().setResult(Activity.RESULT_OK);
                        getActivity().finish();
                    }
                }else if(app.dowhat == Const.Function.IMMERSIVE_TOGGLE){
                    //如果没解锁
                    if(!PreferenceManager
                            .getDefaultSharedPreferences(
                                    getActivity()
                                            .getApplicationContext()
                            ).getBoolean("is_immersive_unlock",false)
                            ){
                        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                                .setTitle("Error...")
                                .setMessage("还没解锁")
                                .setPositiveButton("去解锁?",new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        getActivity().startActivity(new Intent(getActivity(),UnlockActivity.class));
                                    }
                                })
                                .setNegativeButton(getString(R.string.unlock_cancel),new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        getActivity().finish();
                                    }
                                })
                                .show();
                    }else{
                        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                                .putInt("btn" + btn_code + "_mode", 1)
                                .putInt("btn" + btn_code + "_value", app.dowhat)
                                .putInt("btn" + btn_code + "_img", app.imgId)
                                .putInt("btn" + btn_code + "_img2", app.imgId2)
                                .putString("btn" + btn_code + "_name", app.appName)
                                .commit();
                        getActivity().setResult(Activity.RESULT_OK);
                        getActivity().finish();
                    }
                }else{
                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                            .putInt("btn" + btn_code + "_mode", 1)
                            .putInt("btn" + btn_code + "_value", app.dowhat)
                            .putInt("btn" + btn_code + "_img", app.imgId)
                            .putInt("btn" + btn_code + "_img2", app.imgId2)
                            .putString("btn" + btn_code + "_name", app.appName)
                            .commit();
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                }
            }

        });
        if(functionList == null){
            init();
        }
        adapter = new AppListAdapter(getActivity().getApplicationContext(),
                R.layout.applist_item,
                functionList);
        lv.setAdapter(adapter);

        return view;
    }



    private void init(){
        functionList = new ArrayList<AppInfo>();
        functionList.add(new AppInfo(getString(R.string.back),Const.Function.BACK_BY_ACCESSIBILITY, R.drawable.ic_arrow_back_black_48dp, R.drawable.ic_arrow_back_white_48dp));
        functionList.add(new AppInfo(getString(R.string.HOME),Const.Function.HOME_BY_ACCESSIBILITY, R.drawable.ic_home_black_48dp, R.drawable.ic_home_white_48dp));
        functionList.add(new AppInfo(getString(R.string.notify),Const.Function.NOTIFICATIONS_BY_ACCESSIBILITY, R.drawable.ic_notifications_black_48dp, R.drawable.ic_notifications_none_white_48dp));
        functionList.add(new AppInfo(getString(R.string.recently),Const.Function.RECENT_APP_BY_ACCESSIBILITY, R.drawable.ic_apps_black_48dp, R.drawable.ic_apps_white_48dp));
        functionList.add(new AppInfo(getString(R.string.lock_screen),Const.Function.LOCK_SCREEN_WITHOUT_ROOT, R.drawable.ic_lock_black_48dp, R.drawable.ic_lock_white_48dp));
        functionList.add(new AppInfo(getString(R.string.lock_screen_r),Const.Function.LOCK, R.drawable.ic_lock_black_48dp, R.drawable.ic_lock_white_48dp));
        functionList.add(new AppInfo(getString(R.string.menu_r),Const.Function.MENU, R.drawable.ic_menu_black_48dp, R.drawable.ic_menu_white_48dp));
        functionList.add(new AppInfo(getString(R.string.back_r),Const.Function.BACK, R.drawable.ic_arrow_back_black_48dp, R.drawable.ic_arrow_back_white_48dp));
        functionList.add(new AppInfo(getString(R.string.home_r),Const.Function.HOME, R.drawable.ic_home_black_48dp, R.drawable.ic_home_white_48dp));
        functionList.add(new AppInfo(getString(R.string.sleep_r),Const.Function.SLEEP, R.drawable.ic_highlight_remove_black_48dp, R.drawable.ic_highlight_remove_white_48dp));

        functionList.add(new AppInfo(getString(R.string.recently_r),Const.Function.APP_SWITCH, R.drawable.ic_apps_black_48dp, R.drawable.ic_apps_white_48dp));
        functionList.add(new AppInfo(getString(R.string.screen_shot),Const.Function.SCREEN_SHOT, R.drawable.ic_camera_alt_black_48dp, R.drawable.ic_camera_alt_white_48dp));
        functionList.add(new AppInfo(getString(R.string.immersive),Const.Function.IMMERSIVE_TOGGLE, R.drawable.ic_fullscreen_black_48dp, R.drawable.ic_fullscreen_white_48dp));
        functionList.add(new AppInfo(getString(R.string.screenfilter),Const.Function.SCREEN_FILTER, R.drawable.ic_invert_colors_on_black_48dp, R.drawable.ic_invert_colors_on_white_48dp));
        functionList.add(new AppInfo(getString(R.string.qswitcher),Const.Function.LAST_APP_SWITCH, R.drawable.ic_autorenew_black_48dp, R.drawable.ic_autorenew_white_48dp));

        functionList.add(new AppInfo(getString(R.string.toolsbroad),Const.Function.SHOW, R.drawable.ic_polymer_black_48dp, R.drawable.ic_polymer_white_48dp));
        functionList.add(new AppInfo(getString(R.string.hide),Const.Function.HIDE, R.drawable.ic_arrow_drop_up_black_48dp, R.drawable.ic_arrow_drop_up_white_48dp));

        functionList.add(new AppInfo(getString(R.string.keep_screen),Const.Function.KEEP_SCREEN_ON, R.drawable.ic_info_outline_black_48dp, R.drawable.ic_info_outline_white_48dp));
        functionList.add(new AppInfo(getString(R.string.wifi_toggle),Const.Function.WIFI_TOGGLE, R.drawable.ic_network_wifi_black_48dp, R.drawable.ic_network_wifi_white_48dp));
        functionList.add(new AppInfo(getString(R.string.bluetools_toggle),Const.Function.BLUETOOLS_TOGGLE, R.drawable.ic_bluetooth_black_48dp, R.drawable.ic_bluetooth_white_48dp));
        functionList.add(new AppInfo(getString(R.string.rotate_toggle),Const.Function.ROTATION_TOGGLE, R.drawable.ic_screen_lock_rotation_black_48dp, R.drawable.ic_screen_lock_rotation_white_48dp));
        functionList.add(new AppInfo(getString(R.string.data_toggle),Const.Function.GPRS_TOGGLE, R.drawable.ic_swap_vert_black_48dp, R.drawable.ic_swap_vert_white_48dp));

    }

}
