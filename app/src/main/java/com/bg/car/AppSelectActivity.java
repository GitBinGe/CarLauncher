package com.bg.car;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.bg.library.UI.Dialog.Progress;
import com.bg.library.UI.View.TitleView;
import com.bg.library.Utils.Log.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BinGe on 2017/12/28.
 */

public class AppSelectActivity extends AppCompatActivity implements AppBroadcast.AppListener{

    private Handler mHandler;


    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_select);

        TitleView titleView = (TitleView) findViewById(R.id.title);
        titleView.setUnit(TitleView.Unit.BACK | TitleView.Unit.TEXT);
        titleView.setTitle("APP SELECT");

        mHandler = new Handler();
        Progress.show(this);
        AppBroadcast.setListener(this);

        if (AppBroadcast.getApps().size() > 0) {
            initApps();
            Progress.dismiss();
        } else {
            refresh();
        }
    }

    private void refresh() {
        new Thread() {
            public void run() {
                final List<PackageInfo> appList = new ArrayList<>();
                try {
                    List<PackageInfo> infos = getPackageManager().getInstalledPackages(0);
                    for (int i = 0; i < infos.size(); i++) {
                        PackageInfo packageInfo = infos.get(i);
                        if (packageInfo.applicationInfo.loadIcon(getPackageManager()) == null) {
                            continue;
                        }
                        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
                        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                        resolveIntent.setPackage(packageInfo.packageName);
                        List<ResolveInfo> list = getPackageManager()
                                .queryIntentActivities(resolveIntent, 0);
                        if (list.size() == 0) {
                            continue;
                        }
                        appList.add(packageInfo);
                    }
                } catch (Exception e) {
                }
                AppBroadcast.setApps(appList);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initApps();
                        Progress.dismiss();
                    }
                }, 10);
            }
        }.start();
    }

    private void initApps() {

        GridView gv = (GridView) findViewById(R.id.apps);
        gv.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return AppBroadcast.getApps().size();
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
                PackageInfo info = AppBroadcast.getApps().get(i);
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
                PackageInfo info = AppBroadcast.getApps().get(i);
                String packageName = info.packageName;
                int id = getIntent().getIntExtra("id", 0);
                if (id > 0) {
                    Intent intent = new Intent();
                    intent.putExtra("id", id);
                    intent.putExtra("package_name", packageName);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else {
                    Util.startApp(AppSelectActivity.this, packageName);
                    finish();
                }
            }
        });
    }

    @Override
    public void appListChange() {
        refresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppBroadcast.setListener(null);
    }
}
