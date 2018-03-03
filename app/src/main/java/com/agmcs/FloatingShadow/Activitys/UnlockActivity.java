package com.agmcs.FloatingShadow.Activitys;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.agmcs.FloatingShadow.R;

import net.testin.android.hnafse;
import net.testin.android.onlineconfig.hnbqse;
import net.testin.android.os.hnbnse;
import net.testin.android.os.hnbtse;


public class UnlockActivity extends ActionBarActivity implements View.OnClickListener{
    private Button ok,unlock_screenfilter,unlock_immersive,remove_ad;
    private SharedPreferences spf;
    private int cur_point;
    private TextView tv;
    private String adValue = "true";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock_function);
        spf = PreferenceManager.getDefaultSharedPreferences(UnlockActivity.this);

        //在线参数
        hnafse.getInstance(UnlockActivity.this).ercysd("toggle", new hnbqse() {
            @Override
            public void erfcsd(String key, String value) {
                // TODO Auto-generated method stub
                // 获取在线参数成功
                adValue = value;
//                boolean ad =spf.getBoolean("is_remove_ad",false);
//                if(!ad && adValue.equals("true")){
//                    hnbyse.erawsd(UnlockFunction.this).erbjsd();
//                    hnbyse.erawsd(UnlockFunction.this).erclsd(
//                            hnbyse.ORIENTATION_LANDSCAPE);
//                    hnbyse.erawsd(UnlockFunction.this).ercvsd(UnlockFunction.this);
//                }
                Log.d("hihinihao", key + "" + value);
            }

            @Override
            public void erfbsd(String key) {
                // TODO Auto-generated method stub
                // 获取在线参数失败，可能原因有：键值未设置或为空、网络异常、服务器异常
                Log.d("hihiniao","wrong");
            }
        });
        //积分墙需要
        hnbnse.getInstance(UnlockActivity.this).erewsd();




//        hnbyse.erawsd(this).ercusd(this, MainActivity.class);
//        开屏广告




        tv = (TextView)findViewById(R.id.cur_point_tv);

        ok = (Button)findViewById(R.id.get_point_btn);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adValue.equals("true")){
                    hnbnse.getInstance(UnlockActivity.this).erggsdDialog(UnlockActivity.this);
                }else{
                    Toast.makeText(UnlockActivity.this,"已取消",Toast.LENGTH_SHORT).show();
                }
            }
        });

        unlock_screenfilter = (Button)findViewById(R.id.unlock_screenfilter);
        unlock_screenfilter.setOnClickListener(this);

        unlock_immersive = (Button)findViewById(R.id.unlock_immersive);
        unlock_immersive.setOnClickListener(this);

        remove_ad = (Button)findViewById(R.id.remove_ad);
        remove_ad.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        refersh();
    }
    private void refersh(){
        if(spf.getBoolean("is_screenfilter_unlock",false)){
            unlock_screenfilter.setEnabled(false);
            unlock_screenfilter.setText("已解锁");
        }

        if(spf.getBoolean("is_immersive_unlock",false)){
            unlock_immersive.setEnabled(false);
            unlock_immersive.setText("已解锁");
        }
        if(spf.getBoolean("is_remove_ad",false)){
            remove_ad.setEnabled(false);
            remove_ad.setText("已解锁");
        }

        cur_point = hnbtse.getInstance(UnlockActivity.this).erfosd();
        tv.setText("当前积分: " + cur_point);
    }

    @Override
    protected void onDestroy() {
        hnbnse.getInstance(UnlockActivity.this).erevsd();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if(id == R.id.remove_ad){
            new AlertDialog.Builder(UnlockActivity.this)
                    .setTitle("确认解锁吗?")
                    .setMessage("解锁将花去你的200积分.")
                    .setCancelable(false)
                    .setPositiveButton("解锁!",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (cur_point < 200) {
                                Toast.makeText(UnlockActivity.this,"积分不足", Toast.LENGTH_SHORT).show();
                            } else {
                                if (hnbtse.getInstance(UnlockActivity.this).ergksd(200)) {
                                    spf.edit().putBoolean("is_remove_ad", true).commit();
                                    refersh();
                                } else {
                                    Toast.makeText(UnlockActivity.this, "购买失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    })
                    .setNegativeButton("考虑考虑",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }else{
            new AlertDialog.Builder(UnlockActivity.this)
                    .setTitle("确认解锁吗?")
                    .setMessage("解锁将花去你的70积分.")
                    .setCancelable(false)
                    .setPositiveButton("解锁!",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (cur_point < 70) {
                                Toast.makeText(UnlockActivity.this, "积分不足", Toast.LENGTH_SHORT).show();
                            } else {
                                if (hnbtse.getInstance(UnlockActivity.this).ergksd(70)) {
                                    switch (id) {
                                        case R.id.unlock_screenfilter: {
                                            spf.edit().putBoolean("is_screenfilter_unlock", true).commit();
                                            break;
                                        }
                                        case R.id.unlock_immersive:{
                                            spf.edit().putBoolean("is_immersive_unlock", true).commit();
                                            break;
                                        }
                                    }
                                    refersh();
                                } else {
                                    Toast.makeText(UnlockActivity.this, "购买失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    })
                    .setNegativeButton("考虑考虑",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }
}
