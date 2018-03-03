package com.agmcs.FloatingShadow;


import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.agmcs.FloatingShadow.Activitys.AppSeclectActivity;
import com.agmcs.FloatingShadow.adapter.AppListAdapter;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class FragQuickList extends Fragment {
    private ListView lv;
    private AppListAdapter adapter;
    private int btn_code =999;

    public FragQuickList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        btn_code = getActivity().getIntent().getIntExtra("BTN",999);
        if(btn_code == 999){
            getActivity().setResult(Activity.RESULT_CANCELED);
            getActivity().finish();
        }

        View view = inflater.inflate(R.layout.fragment_frag_quick_list, container, false);
        lv = (ListView)view.findViewById(R.id.quick_select);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppInfo app = AppSeclectActivity.quickList.get(position);
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                        .putInt("btn" + btn_code + "_mode", 2)
                        .putString("btn" + btn_code + "_package", app.packageName)
                        .putString("btn" + btn_code + "_activity", app.activityPath)
                        .putString("btn"+ btn_code + "_name",app.appName)
                        .commit();
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
        });

        adapter = new AppListAdapter(getActivity().getApplicationContext(),
                R.layout.applist_item,
                AppSeclectActivity.quickList);
        lv.setAdapter(adapter);


        return view;
    }



}
