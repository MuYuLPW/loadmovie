package com.kyty.loadmovie;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.kyty.loadmovie.utils.MyUtils;
import com.xunlei.downloadlib.XLTaskHelper;
import com.xunlei.downloadlib.parameter.TorrentInfo;
import com.xunlei.downloadlib.parameter.XLTaskInfo;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edit;
    private Button but;
    private File path;
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
    private ImageView iv;
    private int time=0;
    private long id;


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
        Intent intent=new Intent(MainActivity.this,ListActivity.class);
        intent.putExtra("url",btPath);
        intent.putExtra("path",path.getAbsolutePath());
        intent.putExtra("type",0);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        path= new File(Environment.getExternalStorageDirectory(),"51kandiany");
        if (!path.exists()){
            boolean mkdir = path.mkdir();
        }
        initView();
        initListener();
    }

    private void initListener() {
        but.setOnClickListener(this);
        iv.setOnClickListener(this);
        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()==0){
                    iv.setVisibility(View.GONE);
                }else {
                    iv.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initView() {
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        edit = (EditText) findViewById(R.id.edit);
        but = (Button) findViewById(R.id.btn_1);
        iv = (ImageView) findViewById(R.id.iv);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("*/*");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        startActivity(intent);

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "选择本地种子文件"), 1);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "亲，木有文件管理器啊-_-!!", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == 1) {
            Uri uri = data.getData();
            startBtActivity(uri.getPath());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_1:
                startPlay();
                break;
            case R.id.iv:
                edit.setText("");
                break;
        }
    }

    private void startPlay() {
        String url = edit.getText().toString();
        if (TextUtils.isEmpty(url)){
            Toast.makeText(this,"请输入迅雷链接或磁力链接",Toast.LENGTH_LONG).show();
            return;
        }
        if (url.startsWith("magnet:?")){
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
                message.obj= id;
                Log.e("tag","id="+ id);
                handler.sendMessage(message);
            } catch (Exception e) {
                Toast.makeText(getBaseContext(),"无效的磁力链接",Toast.LENGTH_LONG).show();
            }
        }else {
            //迅雷链接
            Intent intent=new Intent(this,ListActivity.class);
            intent.putExtra("url",url);
            intent.putExtra("path",path.getAbsolutePath());
            intent.putExtra("type",1);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        edit.setText(MyUtils.getClipboardText(this));
    }
}
