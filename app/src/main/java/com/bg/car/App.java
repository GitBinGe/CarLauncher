package com.bg.car;

import android.app.Application;

import com.bg.library.Library;

/**
 * Created by BinGe on 2017/12/28.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Library.initialize(this);
    }
}
