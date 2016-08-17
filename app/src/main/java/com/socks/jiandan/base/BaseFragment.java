package com.socks.jiandan.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.android.volley.Request;
import com.socks.jiandan.BuildConfig;
import com.socks.jiandan.net.RequestManager;
import com.socks.jiandan.utils.logger.LogLevel;
import com.socks.jiandan.utils.logger.Logger;
import com.socks.jiandan.view.imageloader.ImageLoadProxy;


/*fragment 基础类*/
public class BaseFragment extends Fragment implements ConstantString {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG) {
            Logger.init(getClass().getSimpleName()).setLogLevel(LogLevel.FULL).hideThreadInfo();
        } else {
            Logger.init(getClass().getSimpleName()).setLogLevel(LogLevel.NONE).hideThreadInfo();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        JDApplication.getRefWatcher(getActivity()).watch(this); //内存检测
        RequestManager.cancelAll(this); //取消所有网络请求
        ImageLoadProxy.getImageLoader().clearMemoryCache(); //清除图片缓存
    }

    protected void executeRequest(Request request) {  //添加volley 队列  fragment 和acticity 都有，可以写到全局配置中
        RequestManager.addRequest(request, this);
    }
}
