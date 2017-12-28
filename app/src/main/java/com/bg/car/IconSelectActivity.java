package com.bg.car;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by BinGe on 2017/12/28.
 */

public class IconSelectActivity extends AppCompatActivity {

    private Handler mHandler;
    private Map<String, Bitmap> bitmaps = new HashMap<>();
    private List<String> icons = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icon_select);
        mHandler = new Handler();
        try {
            String[] icons = getAssets().list("icon8");
            for (String name : icons) {
                this.icons.add("icon8/" + name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        initIcons();
    }

    private void initIcons() {

        GridView gv = (GridView) findViewById(R.id.apps);
        gv.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return icons.size();
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
                    view = LayoutInflater.from(IconSelectActivity.this).inflate(R.layout.app_item, null);
                }
                String info = icons.get(i);
//                ImageView iv = view.findViewById(R.id.image);
//                iv.setImageDrawable(getAssets().open());
                TextView tv = view.findViewById(R.id.text);
                tv.setText(info);
                return view;
            }
        });
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String icon = icons.get(i);
                Intent intent = new Intent();
                intent.putExtra("package_name", getIntent().getStringExtra("package_name"));
                intent.putExtra("icon", icon);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

}
