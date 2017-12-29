package com.bg.car;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.bg.library.Utils.Log.LogUtils;

/**
 * Created by BinGe on 2017/12/29.
 */

public class TimeService extends Service {

    private Binder binder = new Binder();
    private DateCallback callback;

    public class Binder extends android.os.Binder {
        public TimeService getService() {
            return TimeService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        LogUtils.d("TimeService:启动");
        super.onCreate();
        DateReceiver receiver = new DateReceiver();
        registerReceiver(receiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.d("TimeService:开始");
        return super.onStartCommand(intent, flags, startId);
    }

    public void setDateCallback(DateCallback callback) {
        this.callback = callback;
    }

    class DateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (callback != null) {
                callback.onDateChange();
            }
        }
    }

    public interface DateCallback {
        void onDateChange();
    }

}
