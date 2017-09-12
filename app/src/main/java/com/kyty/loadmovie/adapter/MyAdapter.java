package com.kyty.loadmovie.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kyty.loadmovie.R;
import com.kyty.loadmovie.bean.MovieBean;

import java.util.List;

/**
 * Created by Administrator on 2017/9/11.
 */

public class MyAdapter extends BaseAdapter {
    private  List<MovieBean> list;
    private  Context context;
    public MyAdapter(List<MovieBean> list, Context context){
        this.list=list;
        this.context=context;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView==null){
            convertView =  View.inflate(context, R.layout.item, null);
            TextView tv= (TextView) convertView.findViewById(R.id.tv);
            vh=new ViewHolder();
            vh.tv=tv;
            convertView.setTag(vh);
        }else {
            vh= (ViewHolder) convertView.getTag();
        }
        vh.tv.setText(list.get(position).name);
        return convertView;
    }
    class ViewHolder{
        TextView tv;
    }
}
