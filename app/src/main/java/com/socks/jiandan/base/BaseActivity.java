/*
 * Copyright (c) 2014, 青岛司通科技有限公司 All rights reserved.
 * File Name：BaseActivity.java
 * Version：V1.0
 * Author：zhaokaiqiang
 * Date：2014-8-6
 */
package com.socks.jiandan.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.Request;
import com.socks.jiandan.BuildConfig;
import com.socks.jiandan.R;
import com.socks.jiandan.net.RequestManager;
import com.socks.jiandan.utils.logger.LogLevel;
import com.socks.jiandan.utils.logger.Logger;

/*实现的接口主要为一些字段
  *
   *
   * 继承一些string的字段
   * */
public abstract class BaseActivity extends AppCompatActivity implements ConstantString {

    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        if (BuildConfig.DEBUG) {
            Logger.init(getClass().getSimpleName()).setLogLevel(LogLevel.FULL).hideThreadInfo();
        } else {
            Logger.init(getClass().getSimpleName()).setLogLevel(LogLevel.NONE).hideThreadInfo();
        }
    }

    @Override
    public void finish() {  //finsh  结束时 调用
        super.finish();
        //activity 进入和退出的动画
        overridePendingTransition(R.anim.anim_none, R.anim.trans_center_2_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JDApplication.getRefWatcher(this).watch(this);  //关闭检测内存泄漏
        RequestManager.cancelAll(this);   //关闭所有网络队列
    }

    ///////////////////////////////////////////////////////////////////////////
    // 抽象方法In Activity
    ///////////////////////////////////////////////////////////////////////////


    //只能在子类中使用
    protected abstract void initView();

    protected abstract void initData();

    ///////////////////////////////////////////////////////////////////////////
    // 公用方法 Common Operation
    ///////////////////////////////////////////////////////////////////////////

    public void replaceFragment(int id_content, Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(id_content, fragment);
        transaction.commit();
    }

    public void executeRequest(Request<?> request) {  //添加 到volley队列
        RequestManager.addRequest(request, this);
    }

}
