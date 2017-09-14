package com.kyty.loadmovie.parser;


import com.kyty.loadmovie.bean.ResultBean;

import java.util.List;


/**
 * Created by Administrator on 2016-02-21.
 */
public interface Parser {

    public List<ResultBean> parser(String str);
}
