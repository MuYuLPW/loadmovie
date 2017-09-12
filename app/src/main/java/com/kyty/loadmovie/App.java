package com.kyty.loadmovie;

import android.app.Application;

import com.xunlei.downloadlib.XLTaskHelper;

/**
 * Created by Administrator on 2017/9/11.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        XLTaskHelper.init(this);
    }
}
