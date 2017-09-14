package com.kyty.loadmovie.model;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Administrator on 2017/9/14.
 */

public interface Services {
    @GET
    public Call<String> getData(@Url String url);

}
