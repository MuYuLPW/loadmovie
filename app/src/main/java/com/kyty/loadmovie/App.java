package com.kyty.loadmovie;

import android.app.Application;

import com.xunlei.downloadlib.XLTaskHelper;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Administrator on 2017/9/11.
 */

public class App extends Application {
    public static App app;
    public Retrofit retrofit;
    @Override
    public void onCreate() {
        super.onCreate();
        XLTaskHelper.init(this);
        app=this;
        retrofit=new Retrofit.Builder().baseUrl("http://www.baidu.com")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
