package com.agmcs.FloatingShadow.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.agmcs.FloatingShadow.AppInfo;
import com.agmcs.FloatingShadow.R;

import java.util.List;

/**
 * Created by agmcs on 2015/2/11.
 */
public class AppListAdapter extends ArrayAdapter<AppInfo> {
    private int resId;

    public AppListAdapter(Context context, int resource, List<AppInfo> objects) {
        super(context, resource, objects);
        this.resId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;
        ViewHolder holder;
        AppInfo app = getItem(position);

        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resId,null);
            holder = new ViewHolder();
            holder.img = (ImageView)view.findViewById(R.id.app_img);
            holder.title = (TextView)view.findViewById(R.id.app_title);
            view.setTag(holder);
        }else{
            view = convertView;
            holder = (ViewHolder)view.getTag();
        }
        holder.title.setText(app.appName);
        if(app.appIcon == null){
            holder.img.setImageResource(app.imgId);
            holder.img.setColorFilter(Color.parseColor("#424242"));
        }else{
            holder.img.setImageDrawable(app.appIcon);
        }

        return view;
    }
    //缓冲器
    public class ViewHolder{
        TextView title;
        ImageView img;
    }
}
