package com.socks.jiandan.net;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.socks.jiandan.BuildConfig;
import com.socks.jiandan.base.JDApplication;
import com.socks.jiandan.utils.logger.Logger;


/*volley 网络请求库的封装和自定义*/
public class RequestManager {

    public static final int OUT_TIME = 10000;

    public static final int TIMES_OF_RETRY = 1;

    //队列
    public static RequestQueue mRequestQueue = Volley.newRequestQueue(JDApplication.getContext());//全局context对象

    private RequestManager() {
    }

    public static void addRequest(Request<?> request, Object tag) {
        if (tag != null) {
            request.setTag(tag); //设置标签
        }
        //给每个请求重设超时、重试次数
        request.setRetryPolicy(new DefaultRetryPolicy(
                OUT_TIME,
                TIMES_OF_RETRY,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mRequestQueue.add(request); //添加到请求队列

        if (BuildConfig.DEBUG) {
            Logger.d(request.getUrl());
        }

    }

    public static void cancelAll(Object tag) {
        mRequestQueue.cancelAll(tag);
    }
}