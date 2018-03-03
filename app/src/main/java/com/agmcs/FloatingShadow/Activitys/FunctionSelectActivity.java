package com.agmcs.FloatingShadow.Activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.agmcs.FloatingShadow.Const;
import com.agmcs.FloatingShadow.R;
import com.agmcs.FloatingShadow.Utils.AppUtils;
import com.agmcs.FloatingShadow.Views.VerticalViewPager;


public class FunctionSelectActivity extends ActionBarActivity implements View.OnClickListener{
    private ImageButton up_btn;
    private ImageButton down_btn;
    private ImageButton right_btn;
    private ImageButton left_btn;
    private ImageButton click_btn;
    private ImageButton long_btn;
    private ImageButton double_btn;
    private LinearLayout linearLayout;
    private CardView toolsbar;
    private SharedPreferences spf;
    private AppUtils appUtils;

    private ImageButton[] btns = new ImageButton[9];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_select);
        setTitle(getString(R.string.action));

        up_btn = (ImageButton)findViewById(R.id.up_setting_btn);
        down_btn = (ImageButton)findViewById(R.id.down_setting_btn);
        right_btn = (ImageButton)findViewById(R.id.right_setting_btn);
        left_btn = (ImageButton)findViewById(R.id.left_setting_btn);
        click_btn = (ImageButton)findViewById(R.id.click_setting_btn);
        long_btn = (ImageButton)findViewById(R.id.long_setting_btn);
        double_btn = (ImageButton)findViewById(R.id.double_setting_btn);

        up_btn.setOnClickListener(this);
        down_btn.setOnClickListener(this);
        right_btn.setOnClickListener(this);
        left_btn.setOnClickListener(this);
        click_btn.setOnClickListener(this);
        long_btn.setOnClickListener(this);
        double_btn.setOnClickListener(this);

        linearLayout = (LinearLayout)findViewById(R.id.card_view_linear);

        RelativeLayout relativeLayout = (RelativeLayout)LayoutInflater.from(FunctionSelectActivity.this).inflate(R.layout.toolsbar_md,null);
        LinearLayout tools_btns = (LinearLayout)LayoutInflater.from(FunctionSelectActivity.this).inflate(R.layout.tools_btns,null);
        CardView cardView = (CardView)relativeLayout.findViewById(R.id.toolsbar_md);

        VerticalViewPager verticalViewPager = (VerticalViewPager)relativeLayout.findViewById(R.id.tools_viewpager);
        cardView.removeView(verticalViewPager);

        cardView.addView(tools_btns);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        relativeLayout.setLayoutParams(lp);
        linearLayout.addView(relativeLayout);

        btns[0] = (ImageButton)tools_btns.findViewById(R.id.btn1);
        btns[1] = (ImageButton)tools_btns.findViewById(R.id.btn2);
        btns[2] = (ImageButton)tools_btns.findViewById(R.id.btn3);
        btns[3] = (ImageButton)tools_btns.findViewById(R.id.btn4);
        btns[4] = (ImageButton)tools_btns.findViewById(R.id.btn5);
        btns[5] = (ImageButton)tools_btns.findViewById(R.id.btn6);
        btns[6] = (ImageButton)tools_btns.findViewById(R.id.btn7);
        btns[7] = (ImageButton)tools_btns.findViewById(R.id.btn8);
        btns[8] = (ImageButton)tools_btns.findViewById(R.id.btn9);

        btns[0].setOnClickListener(this);
        btns[1].setOnClickListener(this);
        btns[2].setOnClickListener(this);
        btns[3].setOnClickListener(this);
        btns[4].setOnClickListener(this);
        btns[5].setOnClickListener(this);
        btns[6].setOnClickListener(this);
        btns[7].setOnClickListener(this);
        btns[8].setOnClickListener(this);



    }

    @Override
    protected void onResume() {
        super.onResume();
        Drawable drawable;

        for(int i =0;i<9;i++){
            int code;
            code = i+1;
            if(spf == null){
                spf = PreferenceManager.getDefaultSharedPreferences(FunctionSelectActivity.this);
            }
            int mode = spf.getInt("btn" + code + "_mode", 3);
            switch (mode){
                case 0:
                case 2:{
                    drawable = appUtils.getAppIcon(getApplicationContext(), spf.getString("btn" + code + "_package", ""));
                    if(drawable != null){
                        btns[i].setImageDrawable(drawable);
                        btns[i].clearColorFilter();
                        drawable = null;
                    }
                    break;
                }
                case 1:{
                    btns[i].setImageResource(spf.getInt("btn" + code + "_img2", R.drawable.ic_launcher));
                    btns[i].setColorFilter(Color.parseColor("#CFD8DC"));
                    break;
                }
                case 3:{
                    btns[i].setImageResource(R.drawable.ic_add_circle_outline_white_48dp);
                    break;
                }
            }
        }
    }


    @Override
    public void onClick(View v) {
        Intent i = new Intent(FunctionSelectActivity.this,AppSeclectActivity.class);
        switch (v.getId()){
            case R.id.up_setting_btn:{
                i.putExtra("BTN", Const.Action.UP);
                break;
            }
            case R.id.down_setting_btn:{
                i.putExtra("BTN", Const.Action.DOWN);
                break;
            }
            case R.id.right_setting_btn:{
                i.putExtra("BTN", Const.Action.RIGHT);
                break;
            }
            case R.id.left_setting_btn:{
                i.putExtra("BTN", Const.Action.LEFT);
                break;
            }
            case R.id.click_setting_btn:{
                i.putExtra("BTN", Const.Action.CLICK);
                break;
            }
            case R.id.long_setting_btn:{
                i.putExtra("BTN", Const.Action.LONG_PRESS);
                break;
            }
            case R.id.double_setting_btn:{
                i.putExtra("BTN", Const.Action.DOUBLIE_CLICK);
                break;
            }
            case R.id.btn1:{
                i.putExtra("BTN",1);
                break;
            }
            case R.id.btn2:{
                i.putExtra("BTN",2);
                break;
            }
            case R.id.btn3:{
                i.putExtra("BTN",3);
                break;
            }
            case R.id.btn4:{
                i.putExtra("BTN",4);
                break;
            }
            case R.id.btn5:{
                i.putExtra("BTN",5);
                break;
            }
            case R.id.btn6:{
                i.putExtra("BTN",6);
                break;
            }
            case R.id.btn7:{
                i.putExtra("BTN",7);
                break;
            }
            case R.id.btn8:{
                i.putExtra("BTN",8);
                break;
            }
            case R.id.btn9:{
                i.putExtra("BTN",9);
                break;
            }
        }
        startActivityForResult(i,1);
    }

}
