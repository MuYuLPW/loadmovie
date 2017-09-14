package com.kyty.loadmovie.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.kyty.loadmovie.App;
import com.kyty.loadmovie.R;
import com.kyty.loadmovie.adapter.SerachAdapter;
import com.kyty.loadmovie.bean.ResultBean;
import com.kyty.loadmovie.model.Services;
import com.kyty.loadmovie.parser.BTFuliParser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SerchActivity extends BaseActivity {

    private String key;
    private int page=1;
    private ListView listView;
    List<ResultBean> list=new ArrayList<>();
    private SerachAdapter adapter;
    private boolean hasMore=true;
    private boolean isLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serch);
        Intent intent = getIntent();
        key = intent.getStringExtra("str");
        initView();
        initDate();
        initListener();
    }

    private void initListener() {
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem+visibleItemCount==totalItemCount){
                    //分页加载
                    if (isLoading)return;
                    if (hasMore)
                        getDate();
                }
            }
        });
    }

    private void initDate() {
        getDate();
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.lv);
        adapter = new SerachAdapter(list,this);
        listView.setAdapter(adapter);
    }

    public void getDate() {
        isLoading=true;
        dialog.show();
        String string = getResources().getString(R.string.BTFuLi);
        String url = String.format(string, key, page);
        Services services = App.app.retrofit.create(Services.class);
        Call<String> data = services.getData(url);
        data.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                isLoading=false;
                page++;
                dialog.cancel();
                String res=response.body().toString();
                List<ResultBean> resultBeen = analysisXML(res);
                if (resultBeen.size()>0) {
                    list.addAll(resultBeen);
                    adapter.notifyDataSetChanged();
                }else {
                    hasMore=false;
                    Toast.makeText(getBaseContext(),"别扯了，到底了",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                isLoading=false;
                Toast.makeText(SerchActivity.this,"未获取到数据",Toast.LENGTH_LONG).show();
                dialog.cancel();
                finish();
            }
        });
    }
//解析html文件
    private List<ResultBean> analysisXML(String res) {
        BTFuliParser btFuliParser=new BTFuliParser();
        List<ResultBean> parser = btFuliParser.parser(res);
        return parser;

    }
}
