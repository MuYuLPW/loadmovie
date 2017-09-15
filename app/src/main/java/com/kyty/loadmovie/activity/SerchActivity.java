package com.kyty.loadmovie.activity;

import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.kyty.loadmovie.App;
import com.kyty.loadmovie.R;
import com.kyty.loadmovie.adapter.SerachAdapter;
import com.kyty.loadmovie.bean.ResultBean;
import com.kyty.loadmovie.model.Services;
import com.kyty.loadmovie.parser.BTFuliParser;
import com.kyty.loadmovie.utils.MyUtils;
import com.xunlei.downloadlib.XLTaskHelper;
import com.xunlei.downloadlib.parameter.XLTaskInfo;

import java.io.File;
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
    private long id;
    private File path;
    private int time=0;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0://磁力链接先下载种子
                    analysisMagnet(msg);
                    break;
            }
        }
    };

    //解析磁力链接
    private void analysisMagnet(Message message) {
//        long id= (long) message.obj;
        XLTaskInfo taskInfo = XLTaskHelper.instance().getTaskInfo(id);
        Log.e("tag",taskInfo.mFileSize+"");
        if (taskInfo.mFileSize==taskInfo.mDownloadSize && taskInfo.mFileSize!=0){
            //种子下载完成
            handler.removeCallbacksAndMessages(null);
//            String btPath=path.getAbsolutePath() + "/"+XLTaskHelper.instance().getFileName(edit.getText().toString());
            String btPath=path.getAbsolutePath()+"/abc";
            startBtActivity(btPath);
            return;
        }
        if (time>=100){
            dialog.cancel();
            Toast.makeText(getBaseContext(),"无法解析的磁力链接",Toast.LENGTH_LONG).show();
            handler.removeCallbacksAndMessages(null);
            time=0;
            XLTaskHelper.instance().stopTask(id);
            return;
        }
        time++;
        handler.sendMessageDelayed(handler.obtainMessage(0,id),1000);
    }
    private void startBtActivity(String btPath) {
        dialog.cancel();
        Intent intent=new Intent(this,ListActivity.class);
        intent.putExtra("url",btPath);
        intent.putExtra("path",path.getAbsolutePath());
        intent.putExtra("type",0);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serch);
        Intent intent = getIntent();
        key = intent.getStringExtra("str");
        path= new File(Environment.getExternalStorageDirectory(),"51kandiany");
        if (!path.exists()){
            boolean mkdir = path.mkdir();
        }
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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ResultBean resultBean = list.get(position);
                String magnet = resultBean.magnet;
                startPlay(magnet);
            }
        });
    }

    private void startPlay(String url) {
        dialog.show();
        XLTaskHelper.instance().stopTask(id);
        //磁力链接
        if (!MyUtils.isMagnet(url)){
            Toast.makeText(this,"无效的磁力链接",Toast.LENGTH_LONG).show();
            return;
        }
        try {
            Log.e("tag",path.getAbsolutePath());
            id = XLTaskHelper.instance().addMagentTask(url,path.getAbsolutePath(),"abc");
            Message message = handler.obtainMessage();
            message.what=0;
            Log.e("tag","id="+ id);
            handler.sendMessage(message);
        } catch (Exception e) {
            dialog.cancel();
            Toast.makeText(getBaseContext(),"无效的磁力链接",Toast.LENGTH_LONG).show();
        }
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
                if (list.size()==0) finish();
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
