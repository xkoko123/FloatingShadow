package com.agmcs.FloatingShadow;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class FragSDImage extends Fragment {
    private GridView gv;
    private List<Bitmap> bitmaps = new ArrayList<Bitmap>();
    private File[] files;
    private TextView tips;



    public FragSDImage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_frag_sd_image, container, false);
        tips = (TextView)view.findViewById(R.id.sd_image_tips);
        gv = (GridView)view.findViewById(R.id.sd_img);
        bitmaps = getImage();
        //如果sd卡有图片则隐藏提示
        if(bitmaps.size() !=0 ){
            tips.setVisibility(View.GONE);
        }
        BitmapAdapter adapter = new BitmapAdapter(getActivity().getApplicationContext(),
                R.layout.img_item,
                bitmaps);
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
                        .edit()
                        .putBoolean("isImgfromSd", true)
                        .putString("imgPath", files[position].getAbsolutePath())
                        .commit();
                Intent i = new Intent("com.agmcs.floatingshadow.serviceEvent");
                i.putExtra("MSG", Const.ServiceReciver.REFRESH_IMG);
                getActivity().sendBroadcast(i);
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        for(Bitmap bitmap:bitmaps){
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }
        }
        super.onDestroy();
    }

    private List<Bitmap> getImage(){
        List<Bitmap> bitmaps = new ArrayList<Bitmap>();
        File file = new File(Environment.getExternalStorageDirectory(),"ImageRes");
        if(file.exists()){
            files = file.listFiles();
            if(files != null){
                for(File f:files){
                    if (f.getName().endsWith(".png")){
                        Bitmap bitmap= BitmapFactory.decodeFile(f.getAbsolutePath());
                        bitmaps.add(bitmap);
                    }
                }
            }
        }
        return bitmaps;
    }

    public class BitmapAdapter extends ArrayAdapter<Bitmap>{
        private int resId;

        public BitmapAdapter(Context context, int resource, List<Bitmap> objects) {
            super(context, resource, objects);
            this.resId = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Bitmap bitmap = getItem(position);
            View view;
            ViewHolder holder;
            if(convertView == null){
                view = LayoutInflater.from(getContext()).inflate(resId,null);
                holder = new ViewHolder();
                holder.img = (ImageView)view.findViewById(R.id.imageItem);
                view.setTag(holder);
            }else{
                view = convertView;
                holder = (ViewHolder)view.getTag();
            }
            holder.img.setImageBitmap(bitmap);

            return view;
        }
        class ViewHolder{
            ImageView img;
        }
    }

}
