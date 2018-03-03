package com.agmcs.FloatingShadow;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */

public class FragLocalImage extends Fragment {
    private GridView gv;
    private SharedPreferences spf;
    ArrayList<HashMap<String, Object>> lstImageItem;


    public FragLocalImage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_frag_local_image, container, false);
        gv = (GridView)view.findViewById(R.id.local_img);
        spf = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        lstImageItem = new ArrayList<HashMap<String, Object>>();
        lstImageItem = initfreelist();

        SimpleAdapter adapter = new SimpleAdapter(getActivity(),
                lstImageItem,
                R.layout.img_item,
                new String[]{"imageItem",},
                new int[]{R.id.imageItem,});
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                spf.edit()
                        .putBoolean("isImgfromSd",false)
                        .putInt("imgResId", (Integer) (lstImageItem.get(position).get("imageItem")))
                        .apply();
                Intent i = new Intent("com.agmcs.floatingshadow.serviceEvent");
                i.putExtra("MSG", Const.ServiceReciver.REFRESH_IMG);
                getActivity().sendBroadcast(i);
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        lstImageItem = null;
        super.onDestroy();
    }
    private ArrayList<HashMap<String, Object>> initfreelist() {
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> map;


        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin19);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin20);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin21);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin22);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin23);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin24);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin25);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin26);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin27);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin28);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin29);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin30);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin31);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin32);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin33);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin34);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin35);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin36);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin37);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin38);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin39);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin40);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin41);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin42);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin43);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin44);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin45);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin46);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin47);
        list.add(map);

        //ugly
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.ic_launcher);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin2);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin3);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin4);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin5);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin6);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin7);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin8);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin9);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin10);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin11);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin12);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin13);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin14);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin15);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin16);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin17);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("imageItem", R.drawable.skin18);
        list.add(map);

        return list;
    }
}
