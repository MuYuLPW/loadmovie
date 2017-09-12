package com.kyty.loadmovie.bean;

/**
 * Created by Administrator on 2017/9/11.
 */

public class MovieBean {
    public String name;
    public String playUrl;
    public int index;

    public MovieBean(String name, String playUrl,int index) {
        this.name = name;
        this.playUrl = playUrl;
        this.index=index;
    }
}
