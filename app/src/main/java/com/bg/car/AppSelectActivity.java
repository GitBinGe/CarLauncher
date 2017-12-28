package com.bg.car;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BinGe on 2017/12/28.
 */

public class AppSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_select);

        final List<PackageInfo> appList = new ArrayList<>();
        try {
            List<PackageInfo> infos = getPackageManager().getInstalledPackages(0);
            for (int i = 0; i < infos.size(); i++) {
                PackageInfo packageInfo = infos.get(i);
                if (packageInfo.applicationInfo.loadIcon(getPackageManager()) == null) {
                    continue;
                }
                appList.add(packageInfo);
            }
        } catch (Exception e) {
        }

        GridView gv = (GridView) findViewById(R.id.apps);
        gv.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return appList.size();
            }

            @Override
            public Object getItem(int i) {
                return null;
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                if (view == null) {
                    view = LayoutInflater.from(AppSelectActivity.this).inflate(R.layout.app_item, null);
                }
                PackageInfo info = appList.get(i);
                ImageView iv = view.findViewById(R.id.image);
                iv.setImageDrawable(info.applicationInfo.loadIcon(getPackageManager()));
                TextView tv = view.findViewById(R.id.text);
                tv.setText(info.applicationInfo.loadLabel(getPackageManager()));
                return view;
            }
        });
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PackageInfo info = appList.get(i);
                String packageName = info.packageName;
                int id = getIntent().getIntExtra("id", 0);

                Intent intent = new Intent();
                intent.putExtra("id", id);
                intent.putExtra("package_name", packageName);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

    }

}
