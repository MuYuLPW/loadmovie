package com.kyty.loadmovie;

import android.content.Intent;
import android.support.constraint.solver.SolverVariable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.kyty.loadmovie.adapter.MyAdapter;
import com.kyty.loadmovie.bean.MovieBean;
import com.kyty.loadmovie.utils.AbDateUtil;
import com.kyty.loadmovie.utils.MediaFile;
import com.xunlei.downloadlib.XLTaskHelper;
import com.xunlei.downloadlib.parameter.TorrentFileInfo;
import com.xunlei.downloadlib.parameter.TorrentInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ListActivity extends BaseActivity {

    private int type;
    private String url;
    private String path;
    private File file;
    private ListView lv;
    private List<MovieBean> list=new ArrayList<>();
    private MyAdapter adapter;
    private long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Intent intent = getIntent();
        type = intent.getIntExtra("type", -1);
        url = intent.getStringExtra("url");
        path = intent.getStringExtra("path")+"/"+AbDateUtil.getStringByFormat(System.currentTimeMillis(),AbDateUtil.dateFormatYMD_3);

        file = new File(path);
        if (!file.exists()){
            file.mkdir();
        }
        initView();
        initData();
        initListener();
    }

    private void initListener() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieBean movieBean = list.get(position);
                Intent intent=new Intent(ListActivity.this,VideoActivity.class);
                intent.putExtra("url",movieBean.playUrl);
                intent.putExtra("name",movieBean.name);
                startActivity(intent);
            }
        });
    }

    private void initData() {
        if (type==0){//bt
            TorrentInfo torrentInfo = XLTaskHelper.instance().getTorrentInfo(url);
            try {
                id = XLTaskHelper.instance().addTorrentTask(url, path, new int[torrentInfo.mFileCount]);
                TorrentFileInfo[] mSubFileInfo = torrentInfo.mSubFileInfo;
                Log.e("tag","===="+id);
                for (TorrentFileInfo info:mSubFileInfo) {
                    String mFileName = info.mFileName;
                    if (MediaFile.isVideoFileType(mFileName)){
                        String loclUrl = XLTaskHelper.instance().getLoclUrl(path +"/"+mFileName);
                        Log.e("tag",path +"/"+mFileName);
                        int mFileIndex = info.mFileIndex;
                        list.add(new MovieBean(mFileName,loclUrl,mFileIndex));
                        adapter.notifyDataSetChanged();
                    }
                }
            }catch (Exception e){
                Toast.makeText(this,"解析失败",Toast.LENGTH_LONG).show();
            }
        }else if (type==1){
            id= XLTaskHelper.instance().addThunderTask(url, path, null);
            String fileName = XLTaskHelper.instance().getFileName(url);
            if (MediaFile.isVideoFileType(fileName)){
                String loclUrl = XLTaskHelper.instance().getLoclUrl(path +"/"+fileName);
                Log.e("tag",path +"/"+fileName);
                list.add(new MovieBean(fileName,loclUrl,-1));
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void initView() {
        lv = (ListView) findViewById(R.id.list);
        adapter = new MyAdapter(list,this);
        lv.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        XLTaskHelper.instance().deleteTask(id,path);
    }
}
