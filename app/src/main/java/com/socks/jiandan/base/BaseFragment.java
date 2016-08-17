package com.socks.jiandan.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.android.volley.Request;
import com.socks.jiandan.BuildConfig;
import com.socks.jiandan.net.RequestManager;
import com.socks.jiandan.utils.logger.LogLevel;
import com.socks.jiandan.utils.logger.Logger;
import com.socks.jiandan.view.imageloader.ImageLoadProxy;


/*fragment ������*/
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
        JDApplication.getRefWatcher(getActivity()).watch(this); //�ڴ���
        RequestManager.cancelAll(this); //ȡ��������������
        ImageLoadProxy.getImageLoader().clearMemoryCache(); //���ͼƬ����
    }

    protected void executeRequest(Request request) {  //���volley ����  fragment ��acticity ���У�����д��ȫ��������
        RequestManager.addRequest(request, this);
    }
}
