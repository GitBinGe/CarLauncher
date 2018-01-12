package com.bg.car;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;

import com.bg.library.Utils.Log.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BinGe on 2018/1/12.
 */

public class AppBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.d("AppBroadcast");
        apps.clear();
        if (listener != null) {
            listener.appListChange();
        }
    }

    private static List<PackageInfo> apps = new ArrayList<>();
    private static AppListener listener;

    public static void setListener(AppListener l) {
        listener = l;
    }

    public static void setApps(List<PackageInfo> list) {
        apps.clear();
        if (list != null) {
            apps.addAll(list);
        }
    }

    public static List<PackageInfo> getApps() {
        return apps;
    }

    public interface AppListener {
        void appListChange();
    }

}
