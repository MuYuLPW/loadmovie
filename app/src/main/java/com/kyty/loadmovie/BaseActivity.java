package com.kyty.loadmovie;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Administrator on 2017/9/13.
 */

public class BaseActivity extends AppCompatActivity {
    public ProgressDialog dialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog=new ProgressDialog(this);
        dialog.setMessage("正在努力加载中。。。");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
    }

}
